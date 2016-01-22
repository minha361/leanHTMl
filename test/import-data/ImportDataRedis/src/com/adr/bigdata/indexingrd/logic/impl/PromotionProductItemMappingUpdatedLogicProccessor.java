package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexingrd.APIFields;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.PromotionProductItemMappingModel;
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
		
		getProfillingLogger().debug(
				"time proccess " + promotionProductItemMappingModels.size() + " promotionProductItemMapping is "
						+ (System.currentTimeMillis() - start));
	}

}
