package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;
import com.adr.bigdata.indexing.db.sql.daos.CategoryDAO;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.vos.CategoriesVO;
import com.nhb.common.data.PuObject;

public class CategoryDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws ParseException, Exception {
		byteArrayToPuObject();
		PuObject category = getPuObject();

		Integer categoryId = category
				.getInteger(com.adr.bigdata.indexing.db.sql.models.CategoryModel.CAT_ID);

		if (categoryId == null || categoryId <= 0) {
			return null;
		}

		Integer cParent = category
				.getInteger(com.adr.bigdata.indexing.db.sql.models.CategoryModel.CAT_PARENT);
		Integer cStatus = category
				.getInteger(com.adr.bigdata.indexing.db.sql.models.CategoryModel.CAT_STATUS);
		String cName = category
				.getString(com.adr.bigdata.indexing.db.sql.models.CategoryModel.CAT_NAME);
		Long updateTime = category
				.getLong(com.adr.bigdata.indexing.db.sql.models.CategoryModel.CAT_UPDATE_TIME);
		if (cParent == null || cStatus == null || cName == null
				|| updateTime == null) {
			getLogger().error("missing fields");
			return null;
		}

		/* Check cache */

		CategoriesVO cats = null;
		if (getCheckingUpdateTime()) {
			try {
				CategoryBean categoryBean = (CategoryBean) getCacheWrapper()
						.getCacheMap(CacheFields.CATEGORY).get(categoryId);
				if (categoryBean != null
						&& categoryBean.getUpdateTime() >= updateTime) {
					// if item is existing in cache and update time is greater
					// than
					// this current process's params, ignore...
					getLogger().info(
							"categoryId: " + categoryId
									+ " has been upadated by a newer");
					return null;
				} else {
					// otherwise, try to get all categories from database and
					// update
					// into cache
					try (CategoryDAO dao = getDbAdapter().openDAO(
							CategoryDAO.class)) {
						cats = new CategoriesVO();
						for (CategoryBean bean : dao.getAllCats()) {
							if (bean.getId() == categoryId) {
								bean.setUpdateTime(updateTime);
							}

							cats.addCat(bean);

							getCacheWrapper().getCacheMap(CacheFields.CATEGORY)
									.put(bean.getId(), bean);

							@SuppressWarnings("unchecked")
							Set<Integer> childIds = (Set<Integer>) getCacheWrapper()
									.getCacheMap(CacheFields.CATEGORY_PARENT)
									.getOrDefault(bean.getParentId(), null);
							if (childIds == null) {
								childIds = new HashSet<Integer>();
							}
							childIds.add(bean.getId());
							getCacheWrapper().getCacheMap(
									CacheFields.CATEGORY_PARENT).put(
									bean.getParentId(), childIds);
						}
					}
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* End: Check cache */

		/* Get warehouseProductItemIds from DB */
		List<WarehouseProductItemCategoryBean> warehouseProductItemmappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemmappingIds = dao.getByCategoryId(categoryId);
		}
		if (warehouseProductItemmappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with categoryId: "
							+ categoryId);
			return null;
		}
		/* End: Get warehouseProductItemIds from DB */

		Map<Integer, CategoryBean> mCats = new HashMap<Integer, CategoryBean>();
		CategoryBean categoryBean = (CategoryBean) getCacheWrapper()
				.getCacheMap(CacheFields.CATEGORY).get(categoryId);
		if (categoryBean != null && categoryBean.getUpdateTime() >= updateTime) {
			getLogger().info(
					"categoryId: " + categoryId
							+ " has been upadated by a newer");
			return null;
		} else {
			CategoryDAO dao = getDbAdapter().openDAO(CategoryDAO.class);
			List<CategoryBean> beans = dao.getAllCats();
			for (CategoryBean bean : beans)
				mCats.put(bean.getId(), bean);
		}

		JavaRDD<SolrInputDocument> docs = jsc
				.parallelize(warehouseProductItemmappingIds)
				.map(new Function<WarehouseProductItemCategoryBean, SolrInputDocument>() {

					private static final long serialVersionUID = -8237588336027780118L;

					@Override
					public SolrInputDocument call(
							WarehouseProductItemCategoryBean warehouseProductItemMappingId)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								warehouseProductItemMappingId.getId());

						Map<String, Object> fsCatActive = new HashMap<String, Object>();
						fsCatActive
								.put("set",
										mCats.get(
												warehouseProductItemMappingId
														.getCategoryId())
												.getStatus() == 1);
						doc.addField(SolrFields.IS_CATEGORY_ACTIVE, fsCatActive);

						Map<String, Object> fsCatPath = new HashMap<String, Object>();
						fsCatPath.put(
								"set",
								mCats.get(
										warehouseProductItemMappingId
												.getCategoryId()).getPath());
						doc.addField(SolrFields.CATEGORY_PATH, fsCatPath);
						return doc;
					}

				});
		return docs;
	}
}
