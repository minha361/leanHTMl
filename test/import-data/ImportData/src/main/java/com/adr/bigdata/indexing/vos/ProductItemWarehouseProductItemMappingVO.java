package com.adr.bigdata.indexing.vos;

public class ProductItemWarehouseProductItemMappingVO {
	private int warehouseProductItemMappingId;
	private int merchantId;
	private int warehouseId;
	private String merchantName;
	private String merchantSKU;
	private double originalPrice;
	private double sellPrice;
	private int quantity;
	private int safetyStock;
	private int merchantProductItemStatus;
	private int merchantStatus;
	private int warehouseStatus;
	private int provinceId;
	private int isVisible;
	private long updateTime;
	private int priceStatus;
	private int vatStatus;

	// add 21_09_2015
	private int isNotApplyCommision;
	private double commisionFee;

	public ProductItemWarehouseProductItemMappingVO(int warehouseProductItemMappingId, int merchantId, int warehouseId,
			String merchantName, String merchantSKU, double originalPrice, double sellPrice, int quantity,
			int safetyStock, int merchantProductItemStatus, int merchantStatus, int warehouseStatus, int provinceId,
			int isVisible, long updateTime, Integer priceStatus, Integer vatStatus, int isNotApplyCommision,
			double commisionFee) {
		super();
		this.warehouseProductItemMappingId = warehouseProductItemMappingId;
		this.merchantId = merchantId;
		this.warehouseId = warehouseId;
		this.merchantName = merchantName;
		this.merchantSKU = merchantSKU;
		this.originalPrice = originalPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.safetyStock = safetyStock;
		this.merchantProductItemStatus = merchantProductItemStatus;
		this.merchantStatus = merchantStatus;
		this.warehouseStatus = warehouseStatus;
		this.provinceId = provinceId;
		this.isVisible = isVisible;
		this.updateTime = updateTime;
		this.priceStatus = priceStatus;
		this.vatStatus = vatStatus;

		this.isNotApplyCommision = isNotApplyCommision;
		this.commisionFee = commisionFee;
	}

	public int getIsNotApplyCommision() {
		return isNotApplyCommision;
	}

	public void setIsNotApplyCommision(int isNotApplyCommision) {
		this.isNotApplyCommision = isNotApplyCommision;
	}

	public double getCommisionFee() {
		return commisionFee;
	}

	public void setCommisionFee(double commisionFee) {
		this.commisionFee = commisionFee;
	}

	public int getWarehouseProductItemMappingId() {
		return warehouseProductItemMappingId;
	}

	public void setWarehouseProductItemMappingId(int warehouseProductItemMappingId) {
		this.warehouseProductItemMappingId = warehouseProductItemMappingId;
	}

	public int getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(int merchantId) {
		this.merchantId = merchantId;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantSKU() {
		return merchantSKU;
	}

	public void setMerchantSKU(String merchantSKU) {
		this.merchantSKU = merchantSKU;
	}

	public double getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = originalPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getSafetyStock() {
		return safetyStock;
	}

	public void setSafetyStock(int safetyStock) {
		this.safetyStock = safetyStock;
	}

	public int getMerchantProductItemStatus() {
		return merchantProductItemStatus;
	}

	public void setMerchantProductItemStatus(int merchantProductItemStatus) {
		this.merchantProductItemStatus = merchantProductItemStatus;
	}

	public int getMerchantStatus() {
		return merchantStatus;
	}

	public void setMerchantStatus(int merchantStatus) {
		this.merchantStatus = merchantStatus;
	}

	public int getWarehouseStatus() {
		return warehouseStatus;
	}

	public void setWarehouseStatus(int warehouseStatus) {
		this.warehouseStatus = warehouseStatus;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getPriceStatus() {
		return priceStatus;
	}

	public void setPriceStatus(int priceStatus) {
		this.priceStatus = priceStatus;
	}

	public int getVatStatus() {
		return vatStatus;
	}

	public void setVatStatus(int vatStatus) {
		this.vatStatus = vatStatus;
	}

}
