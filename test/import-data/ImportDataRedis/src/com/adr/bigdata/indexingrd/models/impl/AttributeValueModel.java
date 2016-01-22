package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class AttributeValueModel extends RdCachedModel {
	public static final String ATTV_ID = "id";
	public static final String ATTV_ATTRIBUTE_ID = "attributeId";
	public static final String ATTV_VALUE = "value";
	public static final String ATTV_UPDATE_TIME = "updateTime";
	public static final String ATTV_STATUS = "status";

	public List<Integer> getWarehouseProductItemmappingIdsByAttributeValueId(int attributeId, int attributeValueId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByAttributeIdAndAttributeValueId(attributeId, attributeValueId);
		}
	}
}
