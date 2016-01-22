package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class AttributeValueBean extends AbstractBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1742037371799030846L;
	private int id;
	private int attributeId;
	private String value;
	private int status;
	private long updateTime = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
