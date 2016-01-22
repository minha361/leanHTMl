package com.adr.bigdata.indexing.db.sql.beans;

import java.io.Serializable;

import com.nhb.common.db.sql.beans.AbstractBean;

public class AttributeBean extends AbstractBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 430965680228426848L;
	private int productItemId;
	private int id;
	private String value;
	private int valueId;
	private boolean isActive;

	public int getProductItemId() {
		return productItemId;
	}

	public void setProductItemId(int productItemId) {
		this.productItemId = productItemId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getValueId() {
		return valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
