package com.mario.consumer.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.google.gson.Gson;
import com.hazelcast.core.IMap;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.mario.consumer.test.db.dao.CategoryDAO;
import com.mario.consumer.test.statics.CacheFields;
import com.mario.consumer.test.vo.CategoryTreeVO;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.db.sql.DBIAdapter;

public class GetCategoryTreeHandler extends BaseRequestHandler {

	private static final Gson gson = new Gson();
	private String dataSourceName;

	@Override
	public void init(PuObjectRO initParams) {
		if (initParams != null && initParams.variableExists("dataSourceName")) {
			this.dataSourceName = initParams.getString("dataSourceName");
		}
	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		if (type == -1) {
			getLogger().debug("handling request");
			return 1;
		} else if (type == 0) {
			int nThreads = 100;
			int _catId = type;
			if (data != null) {
				if (data.variableExists("numthread")) {
					nThreads = data.getInteger("numthread");
				}
				if (data.variableExists("catid")) {
					_catId = data.getInteger("catid");
				}
			}
			final int catId = _catId;
			CountDownLatch startSignal = new CountDownLatch(1);
			CountDownLatch endSignal = new CountDownLatch(nThreads);
			long[] latencies = new long[nThreads];
			for (int i = 0; i < latencies.length; i++) {
				latencies[i] = -1;
			}
			for (int i = 0; i < nThreads; i++) {
				final int threadId = i;
				(new Thread() {
					private int id = threadId;

					@Override
					public void run() {
						try {
							startSignal.await();
							long startTime = System.nanoTime();
							getCategoryTree(catId);
							latencies[this.id] = System.nanoTime() - startTime;
						} catch (Exception e) {
							getLogger().error(
									"error while calculate category tree: ", e);
						} finally {
							endSignal.countDown();
						}
					}
				}).start();
			}

			long globalStartTime = System.nanoTime();
			startSignal.countDown();
			endSignal.await();
			long globalTotalTime = System.nanoTime() - globalStartTime;

			int successCount = 0;
			long minLat = Long.MAX_VALUE;
			long maxLat = Long.MIN_VALUE;
			double avgLat = 0;
			long totalLat = 0;
			for (long lat : latencies) {
				if (lat > 0) {
					successCount++;
					if (lat > maxLat) {
						maxLat = lat;
					}
					if (lat < minLat) {
						minLat = lat;
					}
					totalLat += lat;
				}
			}
			if (successCount > 0) {
				avgLat = totalLat / successCount;
			} else {
				getLogger().error("fail totally");
				return "fail totally";
			}

			return new PuObject("job name", "get category tree", "catId",
					catId, "num threads", nThreads, "successful requests",
					successCount, "success (%)", successCount * 100
							/ latencies.length, "operators per second",
					successCount / (globalTotalTime / 1e9), "max latency (ms)",
					maxLat / 1e6, "min latency (ms)", minLat / 1e6,
					"avg latency (ms)", avgLat / 1e6, "total time (ms)",
					globalTotalTime / 1e6);
		} else {
			// getLogger().debug("getting category tree for catid: " + type);
			return gson.toJson(getCategoryTree(type));
		}
	}

	public DBIAdapter getDbAdapter() {
		return this.getApi().getDatabaseAdapter(this.dataSourceName);
	}

	public static final int DISABLE_CATEGORY_ID = -1;

	public CategoryTreeVO getCategoryTree(int catId) throws Exception {

		if (catId == DISABLE_CATEGORY_ID) {
			return null;
		}

		IMap<Integer, CategoryBean> id2Cat = null;
		IMap<Integer, Set<Integer>> id2CatChildIds = null;
		try {
			id2Cat = getApi().getHazelcastInstance().getMap(
					CacheFields.CATEGORY);
			id2CatChildIds = getApi().getHazelcastInstance().getMap(
					CacheFields.CATEGORY_PARENT);
		} catch (Exception e) {
			getLogger().error("fail to get category map from cache", e);
		}

		/* Get Ancestor and this, remove fist element */
		CategoryBean thisCategoryBean = null;
		List<CategoryBean> ancestorBeans = null;
		if (id2Cat != null) {
			thisCategoryBean = id2Cat.get(catId);
			// getLogger().debug(
			// "get thisCategoryBean from cache: " + thisCategoryBean);
		}
		if (thisCategoryBean == null) {
			getLogger().debug(
					"can not get thisCategoryBean from cache: "
							+ thisCategoryBean);
			try (CategoryDAO categoryDAO = getDbAdapter().openDAO(
					CategoryDAO.class)) {
				/* Get from DB */
				getLogger().debug("hit to db to get ancestorBeans");
				ancestorBeans = categoryDAO.getAncestor(catId);
				// TODO update to cache
				thisCategoryBean = ancestorBeans.get(0);
				ancestorBeans.remove(0);
				getLogger().debug(
						"thisCategoryBean geted from db: " + thisCategoryBean);
				getLogger().debug(
						"ancestorBeans geted from db: " + ancestorBeans);
			}
		} else {
			List<Integer> ancestorIds = thisCategoryBean.getPath();
			ancestorIds.remove(0);
			ancestorBeans = reOrder(
					id2Cat.getAll(new HashSet<Integer>(ancestorIds)),
					ancestorIds);
		}
		if (thisCategoryBean.isLeaf()) {
			getLogger().debug("thisCategoryBean is leaf");
			thisCategoryBean = ancestorBeans.get(0);
			getLogger().debug("new thisCategoryBean is: " + thisCategoryBean);
			ancestorBeans.remove(0);
		}
		/* End : Get Ancestor and this */

		/* Check ancestor active */
		if (!checkCategoryActive(ancestorBeans, thisCategoryBean)) {
			getLogger().debug("category is not active");
			return null;
		}
		/* End: Check ancestor active */

		// if size > 0, get its children and its sibling and concat to
		// return; if size == 0 return it and its children; if size == 0
		// mean error -> retturn null
		if (ancestorBeans.size() > 0) { // size > 0
			CategoryBean parentBean = ancestorBeans.get(0);

			List<CategoryBean> siblingBeans = getSiblingBeans(id2Cat,
					id2CatChildIds, thisCategoryBean.getId(), parentBean);

			List<CategoryBean> childrenBeans = getChildBeans(id2Cat,
					id2CatChildIds, thisCategoryBean.getId());

			/* Fill this category */
			List<CategoryTreeVO> childCategoryTreeVOs = getChildCategoryVOsFromBeans(childrenBeans);
			CategoryTreeVO thisCategoryTreeVO = getThisCategoryVOFromBean(
					thisCategoryBean, childCategoryTreeVOs);

			/* Fill parent Category */
			List<CategoryTreeVO> siblingCategoryTreeVOs = getSiblingCategoryVOsFromBeans(
					siblingBeans, thisCategoryTreeVO);
			CategoryTreeVO parentCategoryTreeVO = getThisCategoryVOFromBean(
					parentBean, siblingCategoryTreeVOs);

			/* Fill other */
			ancestorBeans.remove(0);
			CategoryTreeVO prevVO = parentCategoryTreeVO;
			for (int i = 0; i < ancestorBeans.size(); i++) {
				CategoryTreeVO curVO = getThisCategoryVOFromBean(
						ancestorBeans.get(i), Arrays.asList(prevVO));
				prevVO = curVO;
			}

			return prevVO;
		} else { // size = 0
			List<CategoryBean> childrenBeans = getChildBeans(id2Cat,
					id2CatChildIds, catId);

			List<CategoryTreeVO> childCategoryTreeVOs = getChildCategoryVOsFromBeans(childrenBeans);
			CategoryTreeVO thisCategoryTreeVO = getThisCategoryVOFromBean(
					thisCategoryBean, childCategoryTreeVOs);
			return thisCategoryTreeVO;
		}
	}

	private List<CategoryBean> getChildBeans(
			IMap<Integer, CategoryBean> id2Cat,
			IMap<Integer, Set<Integer>> id2CatChildIds, int catId)
			throws Exception {

		List<CategoryBean> childrenBeans = null;
		if (id2CatChildIds == null) {
			try (CategoryDAO categoryDAO = getDbAdapter().openDAO(
					CategoryDAO.class)) {
				getLogger().debug("get childrenBeans from db");
				childrenBeans = categoryDAO.getChildren(catId);
				getLogger().debug(
						"childrenBeans geted from db: " + childrenBeans);
			}
		} else {
			Set<Integer> childIds = id2CatChildIds.get(catId);
			childrenBeans = reOrder(id2Cat.getAll(childIds));
		}

		if (childrenBeans == null) {
			childrenBeans = Collections.emptyList();
		}
		return childrenBeans;
	}

	private List<CategoryBean> getSiblingBeans(
			IMap<Integer, CategoryBean> id2Cat,
			IMap<Integer, Set<Integer>> id2CatChildIds, int catId,
			CategoryBean parentBean) throws Exception {

		List<CategoryBean> siblingBeans = null;
		if (id2CatChildIds == null) {
			try (CategoryDAO categoryDAO = getDbAdapter().openDAO(
					CategoryDAO.class)) {
				getLogger().debug("get siblingBeans from db");
				siblingBeans = categoryDAO.getSibling(catId);
				getLogger()
						.debug("siblingBeans geted from db: " + siblingBeans);
			}
		} else {
			Set<Integer> siblingIds = id2CatChildIds.get(parentBean.getId());
			siblingBeans = reOrder(id2Cat.getAll(siblingIds));
		}

		if (siblingBeans == null) {
			siblingBeans = Collections.emptyList();
		}
		return siblingBeans;
	}

	private boolean checkCategoryActive(Collection<CategoryBean> ancestorBeans,
			CategoryBean thisCategoryBean) {
		boolean ancestorActive = true;
		for (CategoryBean ancestorBean : ancestorBeans) {
			if (ancestorBean.getStatus() != 1) {
				ancestorActive = false;
				break;
			}
		}
		if (thisCategoryBean.getStatus() != 1) {
			ancestorActive = false;
		}
		return ancestorActive;
	}

	private List<CategoryTreeVO> getChildCategoryVOsFromBeans(
			Collection<CategoryBean> categoryBeans) {
		List<CategoryTreeVO> categoryTreeVOs = new ArrayList<>();
		for (CategoryBean categoryBean : categoryBeans) {
			CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
			categoryTreeVO.setCategoryId(categoryBean.getId());
			categoryTreeVO.setCategoryName(categoryBean.getName());
			categoryTreeVO.setCategoryParentId(categoryBean.getParentId());

			categoryTreeVO.setIsLeaf(categoryBean.isLeaf());

			categoryTreeVO.setListCategorySub(Collections.emptyList());
			categoryTreeVOs.add(categoryTreeVO);
		}
		return categoryTreeVOs;
	}

	private List<CategoryTreeVO> getSiblingCategoryVOsFromBeans(
			Collection<CategoryBean> categoryBeans,
			CategoryTreeVO thisCategoryTreeVO) {
		List<CategoryTreeVO> categoryTreeVOs = new ArrayList<>();
		for (CategoryBean categoryBean : categoryBeans) {
			if (categoryBean.getId() == thisCategoryTreeVO.getCategoryId()) {
				categoryTreeVOs.add(thisCategoryTreeVO);
			} else {
				CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
				categoryTreeVO.setCategoryId(categoryBean.getId());
				categoryTreeVO.setCategoryName(categoryBean.getName());
				categoryTreeVO.setCategoryParentId(categoryBean.getParentId());
				categoryTreeVO.setIsLeaf(categoryBean.isLeaf());
				categoryTreeVO.setListCategorySub(Collections.emptyList());
				categoryTreeVOs.add(categoryTreeVO);
			}
		}
		return categoryTreeVOs;
	}

	private CategoryTreeVO getThisCategoryVOFromBean(CategoryBean categoryBean,
			List<CategoryTreeVO> childCategoryTreeVOs) {
		CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
		categoryTreeVO.setCategoryId(categoryBean.getId());
		categoryTreeVO.setCategoryName(categoryBean.getName());
		categoryTreeVO.setCategoryParentId(categoryBean.getParentId());
		categoryTreeVO.setIsLeaf(categoryBean.isLeaf());
		categoryTreeVO.setListCategorySub(childCategoryTreeVOs);
		return categoryTreeVO;
	}

	private List<CategoryBean> reOrder(
			Map<Integer, CategoryBean> categoryBeans, List<Integer> order) {
		List<CategoryBean> result = new ArrayList<>();
		for (int id : order) {
			result.add(categoryBeans.get(id));
			// FIXME check misss and get from db in one connection
		}
		return result;
	}

	private List<CategoryBean> reOrder(Map<Integer, CategoryBean> categoryBeans) {
		List<CategoryBean> result = new ArrayList<CategoryBean>();
		for (CategoryBean bean : categoryBeans.values()) {
			if (bean.getStatus() == 1) {
				result.add(bean);
			}
		}
		return result;
	}

}
