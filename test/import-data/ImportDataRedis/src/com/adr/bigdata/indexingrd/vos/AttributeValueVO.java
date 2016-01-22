package com.adr.bigdata.indexingrd.vos;

public class AttributeValueVO {
	private int attributeId;
	private String attributeName;
	private int attributeStatus;
	private int attributeValueId;
	private String attributeValue;

	public AttributeValueVO(int attributeId, String attributeName, int attributeStatus, int attributeValueId,
			String attributeValue) {
		super();
		this.attributeId = attributeId;
		this.attributeName = attributeName;
		this.attributeStatus = attributeStatus;
		this.attributeValueId = attributeValueId;
		this.attributeValue = attributeValue;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public int getAttributeStatus() {
		return attributeStatus;
	}

	public void setAttributeStatus(int attributeStatus) {
		this.attributeStatus = attributeStatus;
	}

	public int getAttributeValueId() {
		return attributeValueId;
	}

	public void setAttributeValueId(int attributeValueId) {
		this.attributeValueId = attributeValueId;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

}
