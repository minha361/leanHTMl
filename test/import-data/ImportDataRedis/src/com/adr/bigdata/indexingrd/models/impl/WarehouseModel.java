package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemTypeBean;
import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class WarehouseModel extends RdCachedModel {
	public static final String WH_ID = "id";
	public static final String WH_STATUS = "status";
	public static final String WH_CITY_ID = "cityId";
	public static final String WH_UPDATE_TIME = "updateTime";

	

	public List<WarehouseProductItemTypeBean> getWarehouseProductItemmappingIdsByWarehouse(int warehouseId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByWarehouseId(warehouseId);
		}
	}

	
}
