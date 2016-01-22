/**
 * 
 */
package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.LandingPageModel.LANDING_PAGE_GROUP_ID;
import static com.adr.bigdata.indexingrd.models.impl.LandingPageModel.LANDING_PAGE_ID;
import static com.adr.bigdata.indexingrd.models.impl.LandingPageModel.PRODUCT_ITEM_ID;
import static com.adr.bigdata.indexingrd.models.impl.LandingPageModel.UPDATE_TIME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.LandingPageStatusBean;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.LandingPageModel;
import com.nhb.common.data.PuObject;

/**
 * @author ndn
 *
 */
public class LandingPageUpdatedProccessor extends BaseLogicProcessor {
	private LandingPageModel landingPageModel = null;

	@Override
	public void execute(PuObject data) throws Exception {
		if (landingPageModel == null) {
			landingPageModel = getModel(LandingPageModel.class);
		}
		
		long start = System.currentTimeMillis();

		Integer landingPageId = data.getInteger(LANDING_PAGE_ID);
		Integer landingPageGroupId = data.getInteger(LANDING_PAGE_GROUP_ID);
		Integer productItemId = data.getInteger(PRODUCT_ITEM_ID);
		Long updateTime = data.getLong(UPDATE_TIME);

		if (landingPageId == null || landingPageGroupId == null || updateTime == null) {
			getLogger().error("landingPageId or landingPageGroupId or updateTime is null");
			return;
		}
		
		List<LandingPageStatusBean> warehouseProductItemMappings;
		if (productItemId != null && productItemId > 0) {
			warehouseProductItemMappings = landingPageModel
					.getWPIMIds(landingPageId, landingPageGroupId, productItemId);
		} else {
			warehouseProductItemMappings = landingPageModel.getWPIMIds(landingPageId, landingPageGroupId);
		}
		Set<Integer> warehouseProductItemMappingIds = new HashSet<Integer>();
		Map<Integer, LandingPageStatusBean> mWarehouseProductItemMappings = new HashMap<Integer, LandingPageStatusBean>();
		warehouseProductItemMappings.forEach(e -> {
			warehouseProductItemMappingIds.add(e.getWarehouseProductItemMappingId());
			mWarehouseProductItemMappings.put(e.getWarehouseProductItemMappingId(), e);
		});

		// Check cache
		List<LandingPageStatusBean> updatedBeans = new ArrayList<LandingPageStatusBean>();
		Map<String, String> lastUpdateTimes = landingPageModel.getLastUpdateTimes(landingPageId);
		for (int warehouseProductItemMappingId : warehouseProductItemMappingIds) {
			Long lastUpdateTime = Long.valueOf(lastUpdateTimes.get(warehouseProductItemMappingId));
			if (lastUpdateTime != null && lastUpdateTime > updateTime) {
				getLogger().info("landingPage of warehouseProductItemId: {} has been updated by a newer", warehouseProductItemMappingId);
			} else {
				updatedBeans.add(mWarehouseProductItemMappings.get(warehouseProductItemMappingId));
			}
		}

		landingPageModel.updateToRedis(landingPageId, landingPageGroupId, updatedBeans);// send to Queue
		landingPageModel.updateTimeToCache(landingPageId, updatedBeans, updateTime);//update update time to redis. 
		landingPageModel.sendLastUpdatedWhpIdsInteger(warehouseProductItemMappingIds);

		getProfillingLogger().debug("total time proccess {} of landing page is {}", 0, (System.currentTimeMillis() - start));
	}

}
