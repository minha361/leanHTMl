package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class BrandModel extends SolrModel {
	public static final String BRAND_ID = "id";
	public static final String BRAND_NAME = "name";
	public static final String BRAND_STATUS = "status";
	public static final String BRAND_IMAGE = "image";
	public static final String BRAND_DESCRIPTION = "description";
	public static final String BRAND_UPDATE_TIME = "updateTime";

	public void updateBrandToCache(BrandBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.BRAND).put(bean.getId(), bean);
		} catch (Exception e) {
			getLogger().error("update Brand " + bean.getId() + " error ", e);
		}
	}

	public BrandBean getBrandByCache(int brandId) {
		try {
			return (BrandBean) getCacheWrapper().getCacheMap(CacheFields.BRAND).get(brandId);
		} catch (Exception e) {
			getLogger().error("get brand " + brandId + " error ", e);
			return null;
		}
	}

	public List<Integer> getWarehouseProductItemMappingIdsByBrand(int brandId) throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByBrandId(brandId);
		}
	}

	public void updateToSolr(List<Integer> warehouseProductItemMappingIds, String brandName, int brandStatus)
			throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int warehouseProductItemMappingId : warehouseProductItemMappingIds) {
			SolrInputDocument doc = createSolrDocument(warehouseProductItemMappingId, brandName, brandStatus);
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

	private SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, String brandName,
			Integer brandStatus) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
		if (brandStatus != null) {
			doc.addField(SolrFields.IS_BRAND_ACTIVE, new SingleMap("set", brandStatus == 1));
		}
		if (brandName != null) {
			doc.addField(SolrFields.BRAND_NAME, new SingleMap("set", brandName));
		}
		return doc;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, BrandBean> getBrandsByCache(Set<Integer> brandIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.BRAND)).getAll(brandIds);
		} catch (Exception e) {
			getLogger().error("get brand " + brandIds + " error ", e);
			return new HashMap<Integer, BrandBean>();
		}
	}
}
