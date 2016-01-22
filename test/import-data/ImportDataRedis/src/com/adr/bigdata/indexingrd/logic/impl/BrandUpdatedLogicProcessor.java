package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_DESCRIPTION;
import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_ID;
import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_IMAGE;
import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_NAME;
import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.BrandModel.BRAND_UPDATE_TIME;

import java.util.ArrayList;
import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.WPMAndVisibleBean;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.BrandModel;
import com.nhb.common.data.PuObject;

class BrandUpdatedLogicProcessor extends BaseLogicProcessor {
	private BrandModel brandModel;

	@Override
	public void execute(PuObject brand) throws Exception {
		long start = System.currentTimeMillis();
		if (brandModel == null) {
			brandModel = getModel(BrandModel.class);
		}

		Integer brandId = brand.getInteger(BRAND_ID);
		String brandName = brand.getString(BRAND_NAME);
		Integer brandStatus = brand.getInteger(BRAND_STATUS);
		String brandImage = brand.getString(BRAND_IMAGE);
		Long updateTime = brand.getLong(BRAND_UPDATE_TIME);
		String brandDescription = brand.getString(BRAND_DESCRIPTION);
		if (brandId == null || brandName == null || brandStatus == null || brandImage == null || updateTime == null) {
			getLogger().error("missing fields");
			return;
		}

		// Check cache
		Long cachedUpdateTime = brandModel.getUpdateTimeFromCache(brandId);
		if (cachedUpdateTime != null && cachedUpdateTime >= updateTime) {
			getLogger().info("brandId: " + brandId + " has been updated by a newer");
			return;
		}

		// Get warehouseProductItemIds from DB
		List<WPMAndVisibleBean> warehouseProductItemMappings = brandModel
				.getWarehouseProductItemMappingIdsByBrand(brandId);
		if (warehouseProductItemMappings == null) {
			getLogger().error("can't find warehouseProductItemMappingIds with brandId: " + brandId);
			return;
		}
		List<Integer> warehouseProductItemMappingIds = new ArrayList<>();
		for (WPMAndVisibleBean wpm : warehouseProductItemMappings) {
			warehouseProductItemMappingIds.add(wpm.getWpmid());
		}

		brandModel.updateToRedis(warehouseProductItemMappingIds, brandName);

		// If success, update to cache
		BrandBean bean = new BrandBean();
		bean.setId(brandId);
		bean.setImage(brandImage);
		bean.setName(brandName);
		bean.setStatus(brandStatus);
		bean.setDescription(brandDescription);
		bean.setUpdateTime(updateTime);
		brandModel.updateBrandToOuputCache(bean);//
		brandModel.updateUpdateTimeToCache(brandId, updateTime);

		//send to queue
		//		brandModel.sendToQueue(warehouseProductItemMappings, brandName);

		getProfillingLogger().debug("time proccess brand " + brandId + " is " + (System.currentTimeMillis() - start));
	}
}
