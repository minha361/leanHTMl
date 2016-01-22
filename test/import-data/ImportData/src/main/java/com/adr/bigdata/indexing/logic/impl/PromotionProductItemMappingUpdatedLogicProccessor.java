package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_ID;
import static com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_STATUS;
import static com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_UPDATE_TIME;
import static com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel.PPM_PROMOTION_PRICE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel;
import com.nhb.common.data.PuObject;

public class PromotionProductItemMappingUpdatedLogicProccessor extends BaseLogicProcessor {

	private PromotionProductItemMappingModel promotionProductItemMappingModel;

	@Override
	public void execute(PuObject data) throws Exception {
		long start = System.currentTimeMillis();
		if (promotionProductItemMappingModel == null) {
			promotionProductItemMappingModel = getModel(PromotionProductItemMappingModel.class);
		}

		Collection<PuObject> promotionProductItemMappingModels = data.getPuObjectArray(APIFields.LIST);

		List<Integer> updatedWarehouseProductItemMappingIds = new ArrayList<Integer>();
		List<Long> updatedTimes = new ArrayList<Long>();

		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		// Get batch update time in cache
		Set<Integer> whpimIds = new HashSet<Integer>();
		for (PuObject promotionProductItemMappingModel : promotionProductItemMappingModels) {
			Integer warehouseProductItemMappingId = promotionProductItemMappingModel.getInteger(PPM_WH_PI_MAPPING_ID);
			if (warehouseProductItemMappingId != null) {
				whpimIds.add(warehouseProductItemMappingId);
			}
		}
		Map<Integer, Long> lastUpdateTimePromotionOfWhPiMappings = this.promotionProductItemMappingModel.getLastUpdateTimePromotionOfWhPiMappings(whpimIds);
		// End: Get batch update time in cache
		for (PuObject promotionProductItemMappingModel : promotionProductItemMappingModels) {
			Integer warehouseProductItemMappingId = promotionProductItemMappingModel.getInteger(PPM_WH_PI_MAPPING_ID);
			Integer status = promotionProductItemMappingModel.getInteger(PPM_WH_PI_MAPPING_STATUS);
			Long updateTime = promotionProductItemMappingModel.getLong(PPM_WH_PI_MAPPING_UPDATE_TIME);
			Double promotionPrice = promotionProductItemMappingModel.getDouble(PPM_PROMOTION_PRICE);
			if (warehouseProductItemMappingId == null || status == null || updateTime == null || promotionPrice == null) {
				getLogger().error("missing som fields");
				continue;
			}

			// Check cache
			Long lastUpdateTime = lastUpdateTimePromotionOfWhPiMappings.get(warehouseProductItemMappingId);
			if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
				getLogger().info(
						"promotion of warehouseProductItemMappingId: " + warehouseProductItemMappingId
								+ " has been update by a newer");
				continue;
			} else {
				updatedWarehouseProductItemMappingIds.add(warehouseProductItemMappingId);
				updatedTimes.add(updateTime);
			}

			SolrInputDocument doc = this.promotionProductItemMappingModel.createSolrDocument(
					warehouseProductItemMappingId, status, promotionPrice);
			docs.add(doc);

			if (docs.size() >= this.promotionProductItemMappingModel.getNumDocPerRequest()) {
				this.promotionProductItemMappingModel.add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}

		}
		if (!docs.isEmpty()) {
			this.promotionProductItemMappingModel.add(docs);
		}
		if (this.promotionProductItemMappingModel.isCommit()) {
			this.promotionProductItemMappingModel.commit();
		}

		this.promotionProductItemMappingModel.updateToCache(updatedWarehouseProductItemMappingIds, updatedTimes);
		getProfillingLogger().debug(
				"time proccess " + promotionProductItemMappingModels.size() + " promotionProductItemMapping is "
						+ (System.currentTimeMillis() - start));
	}

}
