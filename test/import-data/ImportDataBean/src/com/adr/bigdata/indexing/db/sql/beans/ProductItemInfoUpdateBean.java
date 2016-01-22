package com.adr.bigdata.indexing.db.sql.beans;

public class ProductItemInfoUpdateBean {
	private int productItemId;
	private int whpiId;
	private int provinceId;

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getProductItemId() {
		return productItemId;
	}

	public void setProductItemId(int productItemId) {
		this.productItemId = productItemId;
	}

	public int getWhpiId() {
		return whpiId;
	}

	public void setWhpiId(int whpiId) {
		this.whpiId = whpiId;
	}

}
