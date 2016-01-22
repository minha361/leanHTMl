package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class MerchantModel extends RdCachedModel {
	public static final String MC_ID = "id";
	public static final String MC_NAME = "name";
	public static final String MC_STATUS = "status";
	public static final String MC_UPDATE_TIME = "updateTime";
	public static final String MC_IMAGE = "image";
	public static final String MC_DESCRIPTION = "description";

	
	public List<Integer> getWarehouseProductItemmappingIdsByMerchant(int merchantId) throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByMerchantId(merchantId);
		}
	}

	
}
