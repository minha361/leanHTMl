package com.adr.bigdata.indexingrd.vos;

public class CommisionVO {
	private int merchantId;
	private int categoryId;
	private int isNotApplyCommision;
	private double commisionFee;
	private long updateTime;

	public CommisionVO(int merchantId, int categoryId, int isNotApplyCommision, double commisionFee, long updateTime) {
		super();
		this.merchantId = merchantId;
		this.categoryId = categoryId;
		this.isNotApplyCommision = isNotApplyCommision;
		this.commisionFee = commisionFee;
		this.updateTime = updateTime;
	}

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

	public int getIsNotApplyCommision() {
		return isNotApplyCommision;
	}

	public void setIsNotApplyCommision(int isNotApplyCommision) {
		this.isNotApplyCommision = isNotApplyCommision;
	}

	public double getCommisionFee() {
		return commisionFee;
	}

	public void setCommisionFee(double commisionFee) {
		this.commisionFee = commisionFee;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
