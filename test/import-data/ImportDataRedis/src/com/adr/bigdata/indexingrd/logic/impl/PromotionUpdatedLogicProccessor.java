package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.PromotionModel.PM_FINISH_DATE;
import static com.adr.bigdata.indexingrd.models.impl.PromotionModel.PM_ID;
import static com.adr.bigdata.indexingrd.models.impl.PromotionModel.PM_START_DATE;
import static com.adr.bigdata.indexingrd.models.impl.PromotionModel.PM_UPDATE_TIME;

import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.PromotionModel;
import com.adr.bigdata.indexingrd.models.impl.PromotionProductItemMappingModel;
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

		
		getProfillingLogger().debug(
				"time proccess promotion " + promotionId + " is " + (System.currentTimeMillis() - start));
	}

}
