package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class WarehouseProductItemTypeBean extends AbstractBean {
	private static final long serialVersionUID = -8462422467113646597L;

	private int whpimId;
	private int productItemType;
	private int productItemPolicy;

	public int getProductItemPolicy() {
		return productItemPolicy;
	}

	public void setProductItemPolicy(int productItemPolicy) {
		this.productItemPolicy = productItemPolicy;
	}

	public int getWhpimId() {
		return whpimId;
	}

	public void setWhpimId(int whpimId) {
		this.whpimId = whpimId;
	}

	public int getProductItemType() {
		return productItemType;
	}

	public void setProductItemType(int productItemType) {
		this.productItemType = productItemType;
	}

}
