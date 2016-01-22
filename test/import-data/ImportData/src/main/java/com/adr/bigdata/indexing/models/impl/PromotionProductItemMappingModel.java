package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class PromotionProductItemMappingModel extends SolrModel {
	public static final String PPM_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static final String PPM_WH_PI_MAPPING_STATUS = "status";
	public static final String PPM_WH_PI_MAPPING_UPDATE_TIME = "updateTime";
	public static final String PPM_PROMOTION_PRICE = "promotionPrice";

	public SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, Integer status, Double promotionPrice) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
		if (status != null) {
			doc.addField(SolrFields.IS_PROMOTION_MAPPING, new SingleMap("set", status == 1));
		}
		if (promotionPrice != null) {
			doc.addField(SolrFields.PROMOTION_PRICE, new SingleMap("set", promotionPrice));
		}
		return doc;
	}

	public void addAndCommit(List<SolrInputDocument> docs) throws SolrServerException, IOException {
		getSolrClient().add(docs);
		getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
	}
	
	public void add(List<SolrInputDocument> docs) throws SolrServerException, IOException {
		getSolrClient().add(docs);
	}
	
	public void commit() throws SolrServerException, IOException {
		getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
	}

	public void updateToCache(List<Integer> updatedWarehouseProductItemMappingIds, List<Long> updatedTimes) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		for (int i = 0; i < updatedWarehouseProductItemMappingIds.size(); i++) {
			map.put(updatedWarehouseProductItemMappingIds.get(i), updatedTimes.get(i));
		}

		try {
			getCacheWrapper().getCacheMap(CacheFields.PROMOTION_PRODUCT_ITEM_MAPPING).putAll(map);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimePromotionOfWhPiMappings(Set<Integer> whpimIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.PROMOTION_PRODUCT_ITEM_MAPPING)).getAll(whpimIds);
		} catch (Exception e) {
			getLogger().error("warehouseProductItemMappingIds " + whpimIds, e);
			return new HashMap<Integer, Long>();
		}
	}
}
