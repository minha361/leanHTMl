package com.adr.bigdata.indexing.db.sql.beans;

import java.util.Date;

import com.nhb.common.db.sql.beans.AbstractBean;

public class WarehouseProductItemMappingPromotionBean extends AbstractBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5291334361540545204L;
	private int warehouseProductItemMappingId;
	private boolean promotionStatus;
	private boolean promotionMappingStatus;

	private Date startDateDiscount;
	private Date finishDateDiscount;
	private double promotionPrice;

	public boolean isPromotionMappingStatus() {
		return promotionMappingStatus;
	}

	public void setPromotionMappingStatus(boolean promotionMappingStatus) {
		this.promotionMappingStatus = promotionMappingStatus;
	}

	public Date getStartDateDiscount() {
		return startDateDiscount;
	}

	public void setStartDateDiscount(Date startDateDiscount) {
		this.startDateDiscount = startDateDiscount;
	}

	public Date getFinishDateDiscount() {
		return finishDateDiscount;
	}

	public void setFinishDateDiscount(Date finishDateDiscount) {
		this.finishDateDiscount = finishDateDiscount;
	}

	public double getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(double promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public int getWarehouseProductItemMappingId() {
		return warehouseProductItemMappingId;
	}

	public void setWarehouseProductItemMappingId(int warehouseProductItemMappingId) {
		this.warehouseProductItemMappingId = warehouseProductItemMappingId;
	}

	public boolean isPromotionStatus() {
		return promotionStatus;
	}

	public void setPromotionStatus(boolean promotionStatus) {
		this.promotionStatus = promotionStatus;
	}

}
