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
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class MerchantModel extends SolrModel {
	public static final String MC_ID = "id";
	public static final String MC_NAME = "name";
	public static final String MC_STATUS = "status";
	public static final String MC_UPDATE_TIME = "updateTime";
	public static final String MC_IMAGE = "image";
	public static final String MC_DESCRIPTION = "description";

	public void updateToSolr(List<Integer> warehouseProductItemmappingIds, String mcName, int mcStatus)
			throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int warehouseProductItemMappingId : warehouseProductItemmappingIds) {
			SolrInputDocument doc = createSolrDocument(warehouseProductItemMappingId, mcName, mcStatus);
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

	public List<Integer> getWarehouseProductItemmappingIdsByMerchant(int merchantId) throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByMerchantId(merchantId);
		}
	}

	public MerchantBean getMerchantByCache(int merchantId) {
		try {
			return (MerchantBean) getCacheWrapper().getCacheMap(CacheFields.MERCHANT).get(merchantId);
		} catch (Exception e) {
			getLogger().error("get merchant " + merchantId + " error ", e);
			return null;
		}
	}

	public void updateMerchantToCache(MerchantBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.MERCHANT).put(bean.getId(), bean);
		} catch (Exception e) {
			getLogger().error("merchant " + bean.getId() + " error ", e);
		}
	}

	private SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, String mcName, Integer mcStatus) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
		if (mcName != null) {
			doc.addField(SolrFields.MERCHANT_NAME, new SingleMap("set", mcName));
		}
		if (mcStatus != null) {
			doc.addField(SolrFields.IS_MERCHANT_ACTIVE, new SingleMap("set", mcStatus == 1));
		}
		return doc;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, MerchantBean> getMerchantsByCache(Set<Integer> merchantIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.MERCHANT)).getAll(merchantIds);
		} catch (Exception e) {
			getLogger().error("get merchant " + merchantIds + " error ", e);
			return new HashMap<Integer, MerchantBean>();
		}
	}
}
