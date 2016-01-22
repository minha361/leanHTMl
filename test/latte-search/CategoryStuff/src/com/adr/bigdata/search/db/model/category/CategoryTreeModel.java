package com.adr.bigdata.search.db.model.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.search.db.bean.category.CategoryBean;
import com.adr.bigdata.search.db.dao.category.CategoryDAO;
import com.adr.bigdata.search.db.ultils.category.CategoryFields;
import com.adr.bigdata.search.db.vo.category.CategoryTreeVO;
import com.adr.bigdata.search.model.DBModel;

public class CategoryTreeModel extends DBModel {
	public static final int DISABLE_CATEGORY_ID = -1;

	public List<SolrInputDocument> getAllCategoryTreeDocs() throws Exception {
		try (CategoryDAO dao = openDAO(CategoryDAO.class)) {
			List<CategoryBean> allCatBeans = dao.getAllCats();

			Map<Integer, CategoryBean> id2Cat = new HashMap<>();
			Map<Integer, List<Integer>> id2CatChildIds = new HashMap<>();

			for (CategoryBean cat : allCatBeans) {
				id2Cat.put(cat.getId(), cat);
				if (id2CatChildIds.containsKey(cat.getParentId())) {
					id2CatChildIds.get(cat.getParentId()).add(cat.getId());
				} else {
					List<Integer> lst = new ArrayList<>();
					lst.add(cat.getId());
					id2CatChildIds.put(cat.getParentId(), lst);
				}
			}

			List<SolrInputDocument> result = new ArrayList<>();
			for (CategoryBean catBean : allCatBeans) {
				try {
					CategoryTreeVO vo = getCategoryTreeVo(id2Cat, id2CatChildIds, catBean.getId());

					SolrInputDocument doc = new SolrInputDocument();
					doc.addField(CategoryFields.ID, catBean.getId());
					doc.addField(CategoryFields.NAME, catBean.getName());
					if (vo != null) {
						doc.addField(CategoryFields.TREE, vo.toJsonString());
					} else {
						doc.addField(CategoryFields.TREE, "{}");
					}

					result.add(doc);
				} catch (Exception e) {
					getLogger().error("error when create tree " + catBean, e);
				}
			}

			return result;
		}
	}

	private CategoryTreeVO getCategoryTreeVo(Map<Integer, CategoryBean> id2Cat,
			Map<Integer, List<Integer>> id2CatChildIds, int catId) {
		if (catId == DISABLE_CATEGORY_ID) {
			return null;
		}

		if (!id2Cat.containsKey(catId) || id2Cat.get(catId).getStatus() != 1) {
			getLogger().debug("The catId: {} is not exists or not active - {}", catId, id2Cat.get(catId));
			return null;
			//TODO return this category
		}
		CategoryBean catBean = id2Cat.get(catId);
		List<CategoryBean> ancestorBeans = getAncestorBeans(id2Cat, catBean);
		if (catBean.isLeaf()) {
			// is current category is leaf then move to parent
			if (!ancestorBeans.isEmpty()) {
				catBean = ancestorBeans.get(0);
				ancestorBeans.remove(0);
			} else {
				// category level 1 - very very special case
				CategoryTreeVO thisCategoryTreeVO = getThisCategoryVOFromBean(catBean, null);
				return thisCategoryTreeVO;
			}
		}

		if (!ancestorBeans.isEmpty()) {
			CategoryBean parentBean = ancestorBeans.get(0);
			List<CategoryBean> siblingBeans = getChildBeans(id2Cat, id2CatChildIds, parentBean.getId());
			List<CategoryBean> childrenBeans = getChildBeans(id2Cat, id2CatChildIds, catBean.getId());

			/* Fill this category */
			List<CategoryTreeVO> childCategoryTreeVOs = getChildCategoryVOsFromBeans(childrenBeans);
			CategoryTreeVO thisCategoryTreeVO = getThisCategoryVOFromBean(catBean, childCategoryTreeVOs);

			/* Fill parent Category */
			List<CategoryTreeVO> siblingCategoryTreeVOs = getSiblingCategoryVOsFromBeans(siblingBeans,
					thisCategoryTreeVO);
			CategoryTreeVO parentCategoryTreeVO = getThisCategoryVOFromBean(parentBean, siblingCategoryTreeVOs);

			/* Fill other */
			ancestorBeans.remove(0);
			CategoryTreeVO prevVO = parentCategoryTreeVO;
			for (int i = 0; i < ancestorBeans.size(); i++) {
				CategoryTreeVO curVO = getThisCategoryVOFromBean(ancestorBeans.get(i), Arrays.asList(prevVO));
				prevVO = curVO;
			}

			return prevVO;
		} else {
			// category level 1 - special case
			List<CategoryBean> childrenBeans = getChildBeans(id2Cat, id2CatChildIds, catBean.getId());

			List<CategoryTreeVO> childCategoryTreeVOs = getChildCategoryVOsFromBeans(childrenBeans);
			CategoryTreeVO thisCategoryTreeVO = getThisCategoryVOFromBean(catBean, childCategoryTreeVOs);
			return thisCategoryTreeVO;
		}
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

	private List<CategoryTreeVO> getChildCategoryVOsFromBeans(Collection<CategoryBean> categoryBeans) {
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

	private List<CategoryTreeVO> getSiblingCategoryVOsFromBeans(Collection<CategoryBean> categoryBeans,
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

	private List<CategoryBean> getChildBeans(Map<Integer, CategoryBean> id2Cat,
			Map<Integer, List<Integer>> id2CatChildIds, int parentId) {
		List<CategoryBean> siblingBeans = new ArrayList<>();
		List<Integer> siblingIds = id2CatChildIds.get(parentId);
		for (int siblingId : siblingIds) {
			if (id2Cat.get(siblingId).getStatus() == 1) {
				siblingBeans.add(id2Cat.get(siblingId));
			}
		}
		return siblingBeans;
	}

	private List<CategoryBean> getAncestorBeans(Map<Integer, CategoryBean> id2Cat, CategoryBean catBean) {
		List<Integer> ancestorIds = catBean.getPath();
		List<CategoryBean> result = new ArrayList<>();
		// Remove current category in the path
		for (int i = 1; i < ancestorIds.size(); i++) {
			int catId = ancestorIds.get(i);
			result.add(id2Cat.get(catId));
		}
		return result;
	}
}
