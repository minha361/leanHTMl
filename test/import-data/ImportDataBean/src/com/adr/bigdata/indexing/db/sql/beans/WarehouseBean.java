package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class WarehouseBean extends AbstractBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1259815990259588511L;
	private int id;
	private int status;
	private int provinceId;
	private long updateTime = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
