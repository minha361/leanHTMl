package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;
import com.adr.bigdata.indexing.db.sql.daos.CategoryDAO;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class CategoryModel extends SolrModel {
	public static final String CAT_ID = "id";
	public static final String CAT_PARENT = "parent";
	public static final String CAT_STATUS = "status";
	public static final String CAT_NAME = "name";
	public static final String CAT_UPDATE_TIME = "updateTime";

	public List<CategoryBean> getAllCategories() throws Exception {
		try (CategoryDAO dao = getDbAdapter().openDAO(CategoryDAO.class)) {
			return dao.getAllCats();
		}
	}

	public List<WarehouseProductItemCategoryBean> getWarehouseProductItemCategoryBeansByCat(int categoryId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByCategoryId(categoryId);
		}
	}

	public Map<Integer, CategoryBean> updateCategoryToCache(List<CategoryBean> beans) {
		Map<Integer, CategoryBean> catMap = new HashMap<Integer, CategoryBean>();
		Map<Integer, Set<Integer>> parentMap = new HashMap<Integer, Set<Integer>>();
		for (CategoryBean bean : beans) {
			catMap.put(bean.getId(), bean);
			Set<Integer> childIds = parentMap.getOrDefault(bean.getParentId(), null);
			if (childIds == null) {
				childIds = new HashSet<Integer>();
			}
			childIds.add(bean.getId());
			parentMap.put(bean.getParentId(), childIds);
		}
		try {
			getCacheWrapper().getCacheMap(CacheFields.CATEGORY).putAll(catMap);
			getCacheWrapper().getCacheMap(CacheFields.CATEGORY_PARENT).putAll(parentMap);
		} catch (Exception e) {
			getLogger().error("", e);
		}
		return catMap;
	}

	public CategoryBean getCategoryByCache(int categoryId) {
		try {
			return (CategoryBean) getCacheWrapper().getCacheMap(CacheFields.CATEGORY).get(categoryId);
		} catch (Exception e) {
			getLogger().error("get category " + categoryId + " error ", e);
			return null;
		}
	}

	public void updateToSolr(List<WarehouseProductItemCategoryBean> warehouseProductItemmappingIds,
			Map<Integer, CategoryBean> cats) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (WarehouseProductItemCategoryBean warehouseProductItemMappingId : warehouseProductItemmappingIds) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId.getId());
			doc.addField(SolrFields.IS_CATEGORY_ACTIVE,
					new SingleMap("set", cats.get(warehouseProductItemMappingId.getCategoryId()).getStatus() == 1));
			doc.addField(SolrFields.CATEGORY_PATH,
					new SingleMap("set", cats.get(warehouseProductItemMappingId.getCategoryId()).getPath()));
			docs.add(doc);

			if (docs.size() >= getNumDocPerRequest()) {
				getSolrClient().add(docs);
//				getSolrClient().commit();
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			getSolrClient().add(docs);
//			getSolrClient().commit();
		}
		if (isCommit()) {
			getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, CategoryBean> getCategoriesByCache(Set<Integer> categoryIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.CATEGORY)).getAll(categoryIds);
		} catch (Exception e) {
			getLogger().error("get category " + categoryIds + " error ", e);
			return new HashMap<Integer, CategoryBean>();
		}
	}
}
