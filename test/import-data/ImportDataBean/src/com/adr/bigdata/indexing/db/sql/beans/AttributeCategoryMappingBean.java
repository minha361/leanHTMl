package com.adr.bigdata.indexing.db.sql.beans;

import java.util.List;

import com.nhb.common.db.sql.beans.AbstractBean;

public class AttributeCategoryMappingBean extends AbstractBean {
	private static final long serialVersionUID = -2748902428643912062L;

	private int categoryId;
	private int attributeId;
	private List<long[]> filterSpan;
	private int baseUnitId;
	private String unitName;
	private int attributeType;
	private String attributeName;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public List<long[]> getFilterSpan() {
		return filterSpan;
	}

	public void setFilterSpan(List<long[]> filterSpan) {
		this.filterSpan = filterSpan;
	}

	public int getBaseUnitId() {
		return baseUnitId;
	}

	public void setBaseUnitId(int baseUnitId) {
		this.baseUnitId = baseUnitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public int getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(int attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeCategoryMappingBean other = (AttributeCategoryMappingBean) obj;
		if (attributeId != other.attributeId)
			return false;
		if (categoryId != other.categoryId)
			return false;
		return true;
	}

}
