/**
 * 
 */
package com.adr.bigdata.proccessor.product;

/**
 * @author ndn
 *
 */
public class CategoryAttributeConfig {
	private int catId;
	private int attributeId;
	private String attributeName;
	private float weight;
	private boolean isSearch;
	private boolean isInternalTag;

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attributeId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryAttributeConfig other = (CategoryAttributeConfig) obj;
		if (attributeId != other.attributeId)
			return false;
		return true;
	}

	public CategoryAttributeConfig() {
		super();
	}

	public CategoryAttributeConfig(int catId, int attributeId, String attributeName, float weight, boolean isSearch, boolean isInternalTag) {
		super();
		this.catId = catId;
		this.attributeId = attributeId;
		this.attributeName = attributeName;
		this.weight = weight;
		this.isSearch = isSearch;
		this.isInternalTag = isInternalTag;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
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

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public boolean isSearch() {
		return isSearch;
	}

	public boolean isInternalTag() {
		return isInternalTag;
	}

	@Override
	public String toString() {
		return "CategoryAttributeConfig [catId=" + catId + ", attributeId=" + attributeId + ", attributeName="
				+ attributeName + ", weight=" + weight + "]";
	}

	
	
}
