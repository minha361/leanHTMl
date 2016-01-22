package com.adr.bigdata.fullimport.sql.bean;

public class LandingPageBean {
	private int warehouseProductItemMappingId;
	private int landingPageId;
	private int landingPageGroupId;
	private int priority;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getWarehouseProductItemMappingId() {
		return warehouseProductItemMappingId;
	}

	public void setWarehouseProductItemMappingId(int warehouseProductItemMappingId) {
		this.warehouseProductItemMappingId = warehouseProductItemMappingId;
	}

	public int getLandingPageId() {
		return landingPageId;
	}

	public void setLandingPageId(int landingPageId) {
		this.landingPageId = landingPageId;
	}

	public int getLandingPageGroupId() {
		return landingPageGroupId;
	}

	public void setLandingPageGroupId(int landingPageGroupId) {
		this.landingPageGroupId = landingPageGroupId;
	}

}
