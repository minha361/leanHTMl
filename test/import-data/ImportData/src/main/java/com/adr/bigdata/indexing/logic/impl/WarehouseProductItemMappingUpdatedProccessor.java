package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_ID;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_PRICE_STATUS;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_VAT_STATUS;
import static com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel;
import com.nhb.common.data.PuObject;

public class WarehouseProductItemMappingUpdatedProccessor extends BaseLogicProcessor {

	private WarehouseProductItemMappingModel warehouseProductItemMappingModel;

	@Override
	public void execute(PuObject data) throws Exception {
		long start = System.currentTimeMillis();
		if (warehouseProductItemMappingModel == null) {
			warehouseProductItemMappingModel = getModel(WarehouseProductItemMappingModel.class);
		}

		Collection<PuObject> warehouseProductItemMappings = data.getPuObjectArray(APIFields.LIST);

		List<Integer> updatedWarehouseProductItemMappingIds = new ArrayList<Integer>();
		List<Long> updatedTimes = new ArrayList<Long>();
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		// Get batch update time in cache
		Set<Integer> whpimIds = new HashSet<Integer>();
		for (PuObject warehouseProductItemMapping : warehouseProductItemMappings) {
			Integer whpimId = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_ID);
			if (whpimId != null) {
				whpimIds.add(whpimId);
			}
		}
		Map<Integer, Long> lastUpdateTimeOfWarehouseProductItemMappings = warehouseProductItemMappingModel
				.getLastUpdateTimeOfWarehouseProductItemMappings(whpimIds);
		getLogger().debug(
				"lastUpdateTimeOfWarehouseProductItemMappings size: "
						+ lastUpdateTimeOfWarehouseProductItemMappings.size());
		// End: Get batch update time in cache
		// Update to solr
		for (PuObject warehouseProductItemMapping : warehouseProductItemMappings) {
			Integer whpimId = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_ID);
			Integer whId = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_WH_ID);
			Integer piId = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_PRODUCT_ITEM_ID);
			Integer mcId = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_MERCHANT_ID);
			Integer quantity = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_QUANTITY);
			String mcSKU = warehouseProductItemMapping.getString(WH_PI_MAPPING_MERCHANT_SKU);
			Double sellPrice = warehouseProductItemMapping.getDouble(WH_PI_MAPPING_SELL_PRICE);
			Double oriPrice = warehouseProductItemMapping.getDouble(WH_PI_MAPPING_ORIGINAL_PRICE);
			Integer mcPIStatus = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS);
			Integer safetyStock = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_SAFETY_STOCK);
			Long updateTime = warehouseProductItemMapping.getLong(WH_PI_MAPPING_UPDATE_TIME);
			Integer isVisble = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_IS_VISIBLE);
			Integer priceStatus = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_PRICE_STATUS);
			Integer vatStatus = warehouseProductItemMapping.getInteger(WH_PI_MAPPING_VAT_STATUS);
			if (whpimId == null || whId == null || piId == null || mcId == null || quantity == null || mcSKU == null
					|| sellPrice == null || oriPrice == null || mcPIStatus == null || safetyStock == null
					|| updateTime == null || isVisble == null || priceStatus == null || vatStatus == null) {
				getLogger().error("missing some fields of warehouseProductItemMappingId: " + whpimId);
				continue;
			}

			// Check cache
			Long lastUpdateTime = lastUpdateTimeOfWarehouseProductItemMappings.get(whpimId);
			if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
				getLogger().info("warehouseProductItemMappingId: " + whpimId + " has been updated by a newer");
				continue;
			} else {
				updatedWarehouseProductItemMappingIds.add(whpimId);
				updatedTimes.add(updateTime);
			}
			// End: Check cache

			SolrInputDocument doc = warehouseProductItemMappingModel.createSolrDocument(whpimId, whId, piId, mcId,
					quantity, mcSKU, sellPrice, oriPrice, mcPIStatus, safetyStock, isVisble, priceStatus, vatStatus);
			docs.add(doc);

			if (docs.size() >= warehouseProductItemMappingModel.getNumDocPerRequest()) {
				warehouseProductItemMappingModel.add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}

		}
		if (!docs.isEmpty()) {
			warehouseProductItemMappingModel.add(docs);
		}
		if (warehouseProductItemMappingModel.isCommit()) {
			warehouseProductItemMappingModel.commit();
		}
		// End: Update to solr

		warehouseProductItemMappingModel.updateToCache(updatedWarehouseProductItemMappingIds, updatedTimes);
		getProfillingLogger().debug(
				"time proccess " + warehouseProductItemMappings.size() + "warehouseProductItemMapping is "
						+ (System.currentTimeMillis() - start));
	}

}
