package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import java.io.Serializable;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingIdPromotionStatusBean;

public class SerializableWarehouseProductItemMappingIdPromotionStatusBean
		implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private WarehouseProductItemMappingIdPromotionStatusBean bean;

	public WarehouseProductItemMappingIdPromotionStatusBean getBean() {
		return bean;
	}

	public void setBean(WarehouseProductItemMappingIdPromotionStatusBean bean) {
		this.bean = bean;
	}
	public SerializableWarehouseProductItemMappingIdPromotionStatusBean(WarehouseProductItemMappingIdPromotionStatusBean b) {
		bean = b;
	}
}
