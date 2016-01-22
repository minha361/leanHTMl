package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingPromotionBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;

public class PromotionModel extends SolrModel {
	public static final long HOUR = 3600000;

	public static final String PM_ID = "id";
	public static final String PM_START_DATE = "startDate";
	public static final String PM_FINISH_DATE = "finishDate";
	public static final String PM_STATUS = "status";
	public static final String PM_PROMOTION_PRICE = "promotionPrice";
	public static final String PM_PM_PI_MAPPING = "promotionProductItemMappings";
	public static final String PM_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static final String PM_PM_PI_MAPPING_STATUS = "status";
	public static final String PM_UPDATE_TIME = "updateTime";

	public Long getLastUpdateTimeOfPromotion(int promotionId) {
		try {
			return (Long) getCacheWrapper().getCacheMap(CacheFields.PROMOTION).get(promotionId);
		} catch (Exception e) {
			getLogger().error(promotionId + " promotionId", e);
			return null;
		}
	}

	public List<WarehouseProductItemMappingPromotionBean> getWarehouseProductItemmapping(int promotionId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByPromotionId(promotionId);
		}
	}

	public SolrInputDocument createSolrDocument(WarehouseProductItemMappingPromotionBean warehouseProductItemMapping)
			throws ParseException {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
				warehouseProductItemMapping.getWarehouseProductItemMappingId());
		doc.addField(SolrFields.START_TIME_DISCOUNT, new SingleMap("set", warehouseProductItemMapping
				.getStartDateDiscount().getTime() - (getTimeZoneGap() * HOUR)));
		doc.addField(SolrFields.FINISH_TIME_DISCOUNT, new SingleMap("set", warehouseProductItemMapping
				.getFinishDateDiscount().getTime() - (getTimeZoneGap() * HOUR)));
		doc.addField(SolrFields.IS_PROMOTION, new SingleMap("set", warehouseProductItemMapping.isPromotionStatus()));
		doc.addField(SolrFields.IS_PROMOTION_MAPPING,
				new SingleMap("set", warehouseProductItemMapping.isPromotionMappingStatus()));
		doc.addField(SolrFields.PROMOTION_PRICE, new SingleMap("set", warehouseProductItemMapping.getPromotionPrice()));
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

	public void updateToCache(Integer promotionId, List<Integer> updatedWarehouseProductItemMappingIds, Long updateTime) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.PROMOTION).put(promotionId, updateTime);
		} catch (Exception e) {
			getLogger().error("", e);
		}

		Map<Integer, Long> map = new HashMap<Integer, Long>();
		for (int whpimid : updatedWarehouseProductItemMappingIds) {
			map.put(whpimid, updateTime);
		}

		try {
			getCacheWrapper().getCacheMap(CacheFields.PROMOTION_PRODUCT_ITEM_MAPPING).putAll(map);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
}
