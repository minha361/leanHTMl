package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.PromotionModel.PM_FINISH_DATE;
import static com.adr.bigdata.indexing.models.impl.PromotionModel.PM_ID;
import static com.adr.bigdata.indexing.models.impl.PromotionModel.PM_START_DATE;
import static com.adr.bigdata.indexing.models.impl.PromotionModel.PM_UPDATE_TIME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingPromotionBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.PromotionModel;
import com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel;
import com.nhb.common.data.PuObject;

public class PromotionUpdatedLogicProccessor extends BaseLogicProcessor {
	private PromotionModel promotionModel;
	private PromotionProductItemMappingModel promotionProductItemMappingModel;

	@Override
	public void execute(PuObject promotion) throws Exception {
		long start = System.currentTimeMillis();
		if (promotionModel == null) {
			promotionModel = getModel(PromotionModel.class);
		}
		if (promotionProductItemMappingModel == null) {
			promotionProductItemMappingModel = getModel(PromotionProductItemMappingModel.class);
		}

		Integer promotionId = promotion.getInteger(PM_ID);
		Long updateTime = promotion.getLong(PM_UPDATE_TIME);
		String sStartDate = promotion.getString(PM_START_DATE);
		String sFinishDate = promotion.getString(PM_FINISH_DATE);
		if (promotionId == null || updateTime == null || sStartDate == null || sFinishDate == null) {
			getLogger().error("missing some fields");
			return;
		}

		// Check cache of Promotion
		Long lastUpdateTime = promotionModel.getLastUpdateTimeOfPromotion(promotionId);
		if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
			getLogger().info("promotionId: " + promotionId + " has been updated by a newer");
			return;
		}
		// End: Check cache of Promotion

		List<WarehouseProductItemMappingPromotionBean> warehouseProductItemmappings = promotionModel
				.getWarehouseProductItemmapping(promotionId);
		if (warehouseProductItemmappings == null) {
			getLogger().info("No warehouseProductItemMappingIds founded with promotionId: " + promotionId);
			return;
		}

		List<Integer> updatedWarehouseProductItemMappingIds = new ArrayList<Integer>();
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		// Get batch update time form cache
		Set<Integer> whpimIds = new HashSet<Integer>();
		for (WarehouseProductItemMappingPromotionBean warehouseProductItemMapping : warehouseProductItemmappings) {
			whpimIds.add(warehouseProductItemMapping.getWarehouseProductItemMappingId());
		}
		Map<Integer, Long> promotionProductItemMappingLastUpdateTimes = promotionProductItemMappingModel
				.getLastUpdateTimePromotionOfWhPiMappings(whpimIds);
		// End: Get batch update time form cache

		for (WarehouseProductItemMappingPromotionBean warehouseProductItemMapping : warehouseProductItemmappings) {
			// Check cache of promotionProductItemMapping
			Long promotionProductItemMappingLastUpdateTime = promotionProductItemMappingLastUpdateTimes
					.get(warehouseProductItemMapping.getWarehouseProductItemMappingId());
			if (promotionProductItemMappingLastUpdateTime != null
					&& promotionProductItemMappingLastUpdateTime >= updateTime) {
				getLogger().info(
						"promotion of warehouseProductItemMapping: "
								+ warehouseProductItemMapping.getWarehouseProductItemMappingId()
								+ " has been updated by a newer");
				continue;
			} else {
				updatedWarehouseProductItemMappingIds.add(warehouseProductItemMapping
						.getWarehouseProductItemMappingId());
			}
			// End: Check cache of promotionProductItemMapping

			SolrInputDocument doc = promotionModel.createSolrDocument(warehouseProductItemMapping);
			docs.add(doc);

			if (docs.size() >= promotionModel.getNumDocPerRequest()) {
				promotionModel.add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			promotionModel.add(docs);
		}
		if (promotionModel.isCommit()) {
			promotionModel.commit();
		}

		// Get all ProductGroup of all WarehouseProductItemMappingIds
		// Find Best Sell Price for a ProductGroup

		promotionModel.updateToCache(promotionId, updatedWarehouseProductItemMappingIds, updateTime);
		getProfillingLogger().debug(
				"time proccess promotion " + promotionId + " is " + (System.currentTimeMillis() - start));
	}

}
