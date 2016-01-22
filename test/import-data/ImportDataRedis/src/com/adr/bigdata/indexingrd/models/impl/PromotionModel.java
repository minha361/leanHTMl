package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingPromotionBean;
import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class PromotionModel extends RdCachedModel {
	public static final long HOUR = 3600000;

	public static final String PM_ID = "id";
	public static final String PM_START_DATE = "startDate";
	public static final String PM_FINISH_DATE = "finishDate";
	public static final String PM_STATUS = "status";
	public static final String PM_PROMOTION_PRICE = "promotionPrice";
	public static final String PM_PM_PI_MAPPING = "promotionProductItemMappings";
	public static final String PM_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static final String PM_PM_PI_MAPPING_STATUS = "status";
	public static final String PM_UPDATE_TIME = "updateTime";


	public List<WarehouseProductItemMappingPromotionBean> getWarehouseProductItemmapping(int promotionId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByPromotionId(promotionId);
		}
	}

}
