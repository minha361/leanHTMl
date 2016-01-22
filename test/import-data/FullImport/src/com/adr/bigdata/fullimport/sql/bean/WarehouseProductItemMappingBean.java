package com.adr.bigdata.fullimport.sql.bean;

import java.util.Map;

public class WarehouseProductItemMappingBean {
	private int productItemWarehouseId;
	private String barcode;
	private Integer warehouseId;
	private Integer productId;
	private Integer categoryId;
	private Integer productItemId;
	private Double sellPrice;
	private String brandName;
	private Integer brandId;
	private String productItemName;
	private Long createTime;
	private Integer merchantId;
	private String merchantName;
	private String merchantProductItemSKU;
	private Boolean isPromotionMapping;
	private Boolean isPromotion;
	private Double promotionPrice;
	private Long startDateDiscount;
	private Long finishDateDiscount;
	private int[] cityIds;
	private Double weight;
	private Integer productItemType;
	private Integer onsite;
	private Map<Integer, Double> score;
	private Double boostScore;
	private String administrativeUnit;
	private Integer productItemPolicy;
	private Boolean priceFlag;

	public int getProductItemWarehouseId() {
		return productItemWarehouseId;
	}

	public void setProductItemWarehouseId(int productItemWarehouseId) {
		this.productItemWarehouseId = productItemWarehouseId;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getProductItemId() {
		return productItemId;
	}

	public void setProductItemId(Integer productItemId) {
		this.productItemId = productItemId;
	}

	public Double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getProductItemName() {
		return productItemName;
	}

	public void setProductItemName(String productItemName) {
		this.productItemName = productItemName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantProductItemSKU() {
		return merchantProductItemSKU;
	}

	public void setMerchantProductItemSKU(String merchantProductItemSKU) {
		this.merchantProductItemSKU = merchantProductItemSKU;
	}

	public Boolean getIsPromotionMapping() {
		return isPromotionMapping;
	}

	public void setIsPromotionMapping(Boolean isPromotionMapping) {
		this.isPromotionMapping = isPromotionMapping;
	}

	public Boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public Double getPromotionPrice() {
		return promotionPrice;
	}

	public void setPromotionPrice(Double promotionPrice) {
		this.promotionPrice = promotionPrice;
	}

	public Long getStartDateDiscount() {
		return startDateDiscount;
	}

	public void setStartDateDiscount(Long startDateDiscount) {
		this.startDateDiscount = startDateDiscount;
	}

	public Long getFinishDateDiscount() {
		return finishDateDiscount;
	}

	public void setFinishDateDiscount(Long finishDateDiscount) {
		this.finishDateDiscount = finishDateDiscount;
	}

	public int[] getCityIds() {
		return cityIds;
	}

	public void setCityIds(int[] cityIds) {
		this.cityIds = cityIds;
	}

	public Integer getProductItemType() {
		return productItemType;
	}

	public void setProductItemType(Integer productItemType) {
		this.productItemType = productItemType;
	}

	public Integer getOnsite() {
		return onsite;
	}

	public void setOnsite(Integer onsite) {
		this.onsite = onsite;
	}

	public Map<Integer, Double> getScore() {
		return score;
	}

	public void setScore(Map<Integer, Double> score) {
		this.score = score;
	}

	public Double getBoostScore() {
		return boostScore;
	}

	public void setBoostScore(Double boostScore) {
		this.boostScore = boostScore;
	}

	public String getAdministrativeUnit() {
		return administrativeUnit;
	}

	public void setAdministrativeUnit(String administrativeUnit) {
		this.administrativeUnit = administrativeUnit;
	}

	public Integer getProductItemPolicy() {
		return productItemPolicy;
	}

	public void setProductItemPolicy(Integer productItemPolicy) {
		this.productItemPolicy = productItemPolicy;
	}

	public Boolean getPriceFlag() {
		return priceFlag;
	}

	public void setPriceFlag(Boolean priceFlag) {
		this.priceFlag = priceFlag;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

}
