package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_DESCRIPTION;
import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_ID;
import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_IMAGE;
import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_NAME;
import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.MerchantModel.MC_UPDATE_TIME;

import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.MerchantModel;
import com.nhb.common.data.PuObject;

public class MerchantUpdatedLogicProccessor extends BaseLogicProcessor {

	private MerchantModel merchantModel;

	@Override
	public void execute(PuObject merchant) throws Exception {
		long start = System.currentTimeMillis();
		if (merchantModel == null) {
			merchantModel = getModel(MerchantModel.class);
		}

		Integer merchantId = merchant.getInteger(MC_ID);
		String mcName = merchant.getString(MC_NAME);
		Integer mcStatus = merchant.getInteger(MC_STATUS);
		Long updateTime = merchant.getLong(MC_UPDATE_TIME);
		String image = merchant.getString(MC_IMAGE);
		String description = merchant.getString(MC_DESCRIPTION);
		if (merchantId == null || mcName == null || mcStatus == null || updateTime == null) {
			getLogger().error("missing fields");
			return;
		}

		
		getProfillingLogger().debug(
				"time proccess merchant " + merchantId + " is " + (System.currentTimeMillis() - start));
	}

}
