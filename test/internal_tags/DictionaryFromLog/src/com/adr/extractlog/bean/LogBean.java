package com.adr.extractlog.bean;

public class LogBean {
	private String keyWord;
	private long productItemId;
	private String productItemName;
	private String tagJson;
	private String categoryName; //Id, path
	private long categoryId;
	private String categoryPath;
	private String refUrl;
	private String brandName;
	
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public long getProductItemId() {
		return productItemId;
	}
	public void setProductItemId(long productItemId) {
		this.productItemId = productItemId;
	}
	public String getProductItemName() {
		return productItemName;
	}
	public void setProductItemName(String productItemName) {
		this.productItemName = productItemName;
	}
	public String getTagJson() {
		return tagJson;
	}
	public void setTagJson(String tagJson) {
		this.tagJson = tagJson;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryPath() {
		return categoryPath;
	}
	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}
	public String getRefUrl() {
		return refUrl;
	}
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	
}
