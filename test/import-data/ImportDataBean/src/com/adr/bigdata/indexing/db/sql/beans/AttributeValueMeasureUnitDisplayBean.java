/**
 * 
 */
package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

/**
 * @author ndn
 *
 */
public class AttributeValueMeasureUnitDisplayBean extends AbstractBean {
	private static final long serialVersionUID = 1209161942892799779L;

	private int attributeId;
	private int valueId;
	private String value;
	private double displayRatio;
	private String displayUnitName;

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public int getValueId() {
		return valueId;
	}

	public void setValueId(int valueId) {
		this.valueId = valueId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public double getDisplayRatio() {
		return displayRatio;
	}

	public void setDisplayRatio(double displayRatio) {
		this.displayRatio = displayRatio;
	}

	public String getDisplayUnitName() {
		return displayUnitName;
	}

	public void setDisplayUnitName(String displayUnitName) {
		this.displayUnitName = displayUnitName;
	}

}
