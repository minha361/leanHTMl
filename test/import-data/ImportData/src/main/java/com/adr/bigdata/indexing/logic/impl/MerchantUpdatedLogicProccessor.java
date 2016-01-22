package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_ID;
import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_IMAGE;
import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_NAME;
import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_STATUS;
import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_UPDATE_TIME;
import static com.adr.bigdata.indexing.models.impl.MerchantModel.MC_DESCRIPTION;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.MerchantModel;
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

		// Check cache
		MerchantBean merchantBean = merchantModel.getMerchantByCache(merchantId);
		if (merchantBean != null && merchantBean.getUpdateTime() >= updateTime) {
			getLogger().info("merchantId: " + merchantId + " has been updated by a newer");
			return;
		}

		// Get warehouseProductItemIds from DB
		List<Integer> warehouseProductItemmappingIds = merchantModel
				.getWarehouseProductItemmappingIdsByMerchant(merchantId);
		if (warehouseProductItemmappingIds == null) {
			getLogger().error("can't find warehouseProductItemmappingIds with merchantId: " + merchantId);
			return;
		}

		merchantModel.updateToSolr(warehouseProductItemmappingIds, mcName, mcStatus);

		// If success, update to cache
		MerchantBean bean = new MerchantBean();
		bean.setId(merchantId);
		bean.setName(mcName);
		bean.setStatus(mcStatus);
		bean.setUpdateTime(updateTime);
		bean.setImage(image);
		bean.setInfo(description);
		merchantModel.updateMerchantToCache(bean);
		getProfillingLogger().debug(
				"time proccess merchant " + merchantId + " is " + (System.currentTimeMillis() - start));
	}

}
