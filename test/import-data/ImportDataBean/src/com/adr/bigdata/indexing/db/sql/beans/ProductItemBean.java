package com.adr.bigdata.indexing.db.sql.beans;

import java.util.List;

import com.nhb.common.db.sql.beans.AbstractBean;

public class ProductItemBean extends AbstractBean {
	private static final long serialVersionUID = 226973189880860612L;

	private int id;
	private int productId;

	private int brandId;
	private String brandName;
	private int catId;
	private String barcode;
	private String name;
	private int status;
	private int brandStatus;
	private int categoryStatus;
	private List<Long> catgoryPath;
	private long updateTime = 0;

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getBrandId() {
		return brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getBrandStatus() {
		return brandStatus;
	}

	public void setBrandStatus(int brandStatus) {
		this.brandStatus = brandStatus;
	}

	public int getCategoryStatus() {
		return categoryStatus;
	}

	public void setCategoryStatus(int categoryStatus) {
		this.categoryStatus = categoryStatus;
	}

	public List<Long> getCatgoryPath() {
		return catgoryPath;
	}

	public void setCatgoryPath(List<Long> catgoryPath) {
		this.catgoryPath = catgoryPath;
	}

}
