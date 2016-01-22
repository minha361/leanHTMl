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
import com.adr.bigdata.indexing.utils.StatusesTool;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class WarehouseProductItemMappingModel extends SolrModel {
	public static final String WH_PI_MAPPING_ID = "id";
	public static final String WH_PI_MAPPING_WH_ID = "warehouseId";
	public static final String WH_PI_MAPPING_PRODUCT_ITEM_ID = "productItemId";
	public static final String WH_PI_MAPPING_MERCHANT_ID = "merchantId";
	public static final String WH_PI_MAPPING_QUANTITY = "quantity";
	public static final String WH_PI_MAPPING_MERCHANT_SKU = "merchantSKU";
	public static final String WH_PI_MAPPING_SELL_PRICE = "sellPrice";
	public static final String WH_PI_MAPPING_ORIGINAL_PRICE = "originalPrice";
	public static final String WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS = "merchantProductItemStatus";
	public static final String WH_PI_MAPPING_SAFETY_STOCK = "safetyStock";
	public static final String WH_PI_MAPPING_UPDATE_TIME = "updateTime";
	public static final String WH_PI_MAPPING_IS_VISIBLE = "isVisible";
	public static final String WH_PI_MAPPING_VAT_STATUS = "vatStatus";
	public static final String WH_PI_MAPPING_PRICE_STATUS = "priceStatus";

	// public Long getLastUpdateTimeOfWarehouseProductItemMapping(int whpimId) {
	// try {
	// return (Long) getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE_PRODUCT_ITEM_MAPPING).get(whpimId);
	// } catch (Exception e) {
	// getLogger().error("whpimId " + whpimId + " error ", e);
	// return null;
	// }
	// }

	public SolrInputDocument createSolrDocument(int whpimId, Integer whId, Integer piId, Integer mcId,
			Integer quantity, String mcSKU, Double sellPrice, Double oriPrice, Integer mcPIStatus, Integer safetyStock,
			Integer isVisible, Integer priceStatus, Integer vatStatus) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, whpimId);

		if (whId != null) {
			doc.addField(SolrFields.WAREHOUSE_ID, new SingleMap("set", whId));
		}

		if (piId != null) {
			doc.addField(SolrFields.PRODUCT_ITEM_ID, new SingleMap("set", piId));
		}

		if (mcId != null) {
			doc.addField(SolrFields.MERCHANT_ID, new SingleMap("set", mcId));
		}

		if (quantity != null) {
			doc.addField(SolrFields.QUANTITY, new SingleMap("set", quantity));
		}

		if (mcSKU != null) {
			doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_SKU, new SingleMap("set", mcSKU));
		}

		if (sellPrice != null) {
			doc.addField(SolrFields.SELL_PRICE, new SingleMap("set", sellPrice));

			if ((oriPrice != null) && (oriPrice > sellPrice)) {
				doc.addField(SolrFields.PRICE_FLAG, new SingleMap("set", true));
			} else {
				doc.addField(SolrFields.PRICE_FLAG, new SingleMap("set", false));
			}
		} else {
			doc.addField(SolrFields.SELL_PRICE, new SingleMap("set", null));
			doc.addField(SolrFields.PRICE_FLAG, new SingleMap("set", false));
		}

		if (mcPIStatus != null) {
			doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_STATUS, new SingleMap("set", mcPIStatus));
		}

		// if (safetyStock != null) {
		// doc.addField(SolrFields.SAFETY_STOCK, new SingleMap("set", safetyStock));
		// }

		if (isVisible != null) {
			doc.addField(SolrFields.VISIBLE, new SingleMap("set", StatusesTool.getListBits(isVisible)));
			doc.addField(SolrFields.ON_SITE, new SingleMap("set", isVisible));
		}

		if (priceStatus != null) {
			doc.addField(SolrFields.PRICE_STATUS, new SingleMap("set", priceStatus));
		}

		if (vatStatus != null) {
			doc.addField(SolrFields.VAT_STATUS, new SingleMap("set", vatStatus));
		}

		if (piId != null && mcId != null) {
			doc.addField(SolrFields.PRODUCT_ITEM_ID_MERCHANT_ID, new SingleMap("set", piId + "_" + mcId));
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
			getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE_PRODUCT_ITEM_MAPPING).putAll(map);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimeOfWarehouseProductItemMappings(Set<Integer> whpimIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE_PRODUCT_ITEM_MAPPING)).getAll(whpimIds);
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<Integer, Long>();
		}
	}
}
