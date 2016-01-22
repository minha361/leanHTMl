package com.adr.bigdata.indexingrd.vos;

import java.util.Collection;

public class ProductItemVO {
	private int id;
	private int productId;
	private int brandId;
	private String brandName;
	private int categoryId;
	private String barcode;
	private String name;
	private int status;
	private int brandStatus;
	private int categoryStatus;
	private Collection<Long> categoryPath;

	private long createTime;
	private int freshFoodType;
	private double weight;
	private int type;
	private String image;

	private Collection<AttributeValueVO> atts;
	private Collection<ProductItemWarehouseProductItemMappingVO> whs;

	private int policy;

	private long updateTime;

	public ProductItemVO(int id, int productId, int brandId, String brandName, int categoryId, String barcode,
			String name, int status, int brandStatus, int categoryStatus, Collection<Long> categoryPath,
			long createTime, int freshFoodType, double weight, int type, String image,
			Collection<AttributeValueVO> atts, Collection<ProductItemWarehouseProductItemMappingVO> whs, int policy,
			long updateTime) {
		super();
		this.id = id;
		this.productId = productId;
		this.brandId = brandId;
		this.brandName = brandName;
		this.categoryId = categoryId;
		this.barcode = barcode;
		this.name = name;
		this.status = status;
		this.brandStatus = brandStatus;
		this.categoryStatus = categoryStatus;
		this.categoryPath = categoryPath;
		this.createTime = createTime;
		this.freshFoodType = freshFoodType;
		this.weight = weight;
		this.type = type;
		this.image = image;
		this.atts = atts;
		this.whs = whs;
		this.updateTime = updateTime;
		this.policy = policy;
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

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
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

	public Collection<Long> getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(Collection<Long> categoryPath) {
		this.categoryPath = categoryPath;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getFreshFoodType() {
		return freshFoodType;
	}

	public void setFreshFoodType(int freshFoodType) {
		this.freshFoodType = freshFoodType;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Collection<AttributeValueVO> getAtts() {
		return atts;
	}

	public void setAtts(Collection<AttributeValueVO> atts) {
		this.atts = atts;
	}

	public Collection<ProductItemWarehouseProductItemMappingVO> getWhs() {
		return whs;
	}

	public void setWhs(Collection<ProductItemWarehouseProductItemMappingVO> whs) {
		this.whs = whs;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

}
