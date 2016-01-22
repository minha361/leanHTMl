package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.WarehouseModel.WH_CITY_ID;
import static com.adr.bigdata.indexingrd.models.impl.WarehouseModel.WH_ID;
import static com.adr.bigdata.indexingrd.models.impl.WarehouseModel.WH_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.WarehouseModel.WH_UPDATE_TIME;

import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.WarehouseModel;
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

		
		getProfillingLogger().debug(
				"time proccess warehouse " + warehouseId + " is " + (System.currentTimeMillis() - start));
	}

}
