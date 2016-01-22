package com.adr.bigdata.fullimport.sql.bean;

public class NotApplyCommisionFeeBean {
	private int merchantId;
	private int categoryId;
	private boolean isNotApplyCommissionFee;

	public int getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(int merchantId) {
		this.merchantId = merchantId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isNotApplyCommissionFee() {
		return isNotApplyCommissionFee;
	}

	public void setNotApplyCommissionFee(boolean isNotApplyCommissionFee) {
		this.isNotApplyCommissionFee = isNotApplyCommissionFee;
	}

}
