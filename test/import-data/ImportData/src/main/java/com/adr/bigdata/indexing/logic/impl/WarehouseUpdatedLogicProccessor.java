package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.WarehouseModel.WH_CITY_ID;
import static com.adr.bigdata.indexing.models.impl.WarehouseModel.WH_ID;
import static com.adr.bigdata.indexing.models.impl.WarehouseModel.WH_STATUS;
import static com.adr.bigdata.indexing.models.impl.WarehouseModel.WH_UPDATE_TIME;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemTypeBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.WarehouseModel;
import com.nhb.common.data.PuObject;

public class WarehouseUpdatedLogicProccessor extends BaseLogicProcessor {

	private WarehouseModel warehouseModel;

	@Override
	public void execute(PuObject warehouse) throws Exception {
		long start = System.currentTimeMillis();
		if (warehouseModel == null) {
			warehouseModel = getModel(WarehouseModel.class);
		}

		Integer warehouseId = warehouse.getInteger(WH_ID);
		Integer whStatus = warehouse.getInteger(WH_STATUS);
		Integer whCityId = warehouse.getInteger(WH_CITY_ID);
		Long updateTime = warehouse.getLong(WH_UPDATE_TIME);
		if (warehouseId == null || whStatus == null || whCityId == null || updateTime == null) {
			getLogger().error("missing fields");
			return;
		}

		// Check cache
		WarehouseBean warehouseBean = warehouseModel.getWarehouseByCache(warehouseId);
		if (warehouseBean != null && warehouseBean.getUpdateTime() >= updateTime) {
			getLogger().info("warehouseId: " + warehouseId + " has been updated by a newer");
			return;
		}

		// Get warehouseProductItemIds from DB
		List<WarehouseProductItemTypeBean> warehouseProductItemmappingIds = warehouseModel
				.getWarehouseProductItemmappingIdsByWarehouse(warehouseId);
		if (warehouseProductItemmappingIds == null) {
			getLogger().error("can't find warehouseProductItemmappingIds with warehouseId: " + warehouseId);
			return;
		}

		warehouseModel.updateToSolr(warehouseProductItemmappingIds, whStatus, whCityId);

		// If success, update to cache
		WarehouseBean bean = new WarehouseBean();
		bean.setId(warehouseId);
		bean.setProvinceId(whCityId);
		bean.setStatus(whStatus);
		bean.setUpdateTime(updateTime);
		warehouseModel.updateToCache(bean);
		getProfillingLogger().debug(
				"time proccess warehouse " + warehouseId + " is " + (System.currentTimeMillis() - start));
	}

}
