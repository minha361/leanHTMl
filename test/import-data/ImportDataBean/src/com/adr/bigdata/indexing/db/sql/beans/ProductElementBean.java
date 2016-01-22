package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class ProductElementBean extends AbstractBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1118048446298601028L;
	private int productItemWarehouseId;
	private int productId;
	private int productItemId;
	private int brandId;
	private String brandname;
	private int catId;
	private int warehouseId;
	private int mcId;
	private String mcName;
	private String merchantProductItemSKU;
	private double discountPercent;
	private long startTimeDiscount;
	private long finishTimeDiscount;
	private String barcode;
	private int countSell;
	private int countView;
	private double originalPrice;
	private double sellPrice;
	private String productItemName;
	private long createTime;
	private boolean isHot;
	private boolean isNew;
	private boolean isPromotion;
	private int quantity;
	private int catStatus;
	private int brandStatus;
	private int mcStatus;
	private int warehouseStatus;
	private int productItemStatus;
	private int mcProductItemStatus;

	public int getProductItemWarehouseId() {
		return productItemWarehouseId;
	}

	public void setProductItemWarehouseId(int productItemWarehouseId) {
		this.productItemWarehouseId = productItemWarehouseId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getProductItemId() {
		return productItemId;
	}

	public void setProductItemId(int productItemId) {
		this.productItemId = productItemId;
	}

	public int getBrandId() {
		return brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public int getMcId() {
		return mcId;
	}

	public void setMcId(int mcId) {
		this.mcId = mcId;
	}

	public String getMcName() {
		return mcName;
	}

	public void setMcName(String mcName) {
		this.mcName = mcName;
	}

	public String getMerchantProductItemSKU() {
		return merchantProductItemSKU;
	}

	public void setMerchantProductItemSKU(String merchantProductItemSKU) {
		this.merchantProductItemSKU = merchantProductItemSKU;
	}

	public double getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(double discountPercent) {
		this.discountPercent = discountPercent;
	}

	public long getStartTimeDiscount() {
		return startTimeDiscount;
	}

	public void setStartTimeDiscount(long startTimeDiscount) {
		this.startTimeDiscount = startTimeDiscount;
	}

	public long getFinishTimeDiscount() {
		return finishTimeDiscount;
	}

	public void setFinishTimeDiscount(long finishTimeDiscount) {
		this.finishTimeDiscount = finishTimeDiscount;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getCountSell() {
		return countSell;
	}

	public void setCountSell(int countSell) {
		this.countSell = countSell;
	}

	public int getCountView() {
		return countView;
	}

	public void setCountView(int countView) {
		this.countView = countView;
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

	public String getProductItemName() {
		return productItemName;
	}

	public void setProductItemName(String productItemName) {
		this.productItemName = productItemName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isPromotion() {
		return isPromotion;
	}

	public void setPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getCatStatus() {
		return catStatus;
	}

	public void setCatStatus(int catStatus) {
		this.catStatus = catStatus;
	}

	public int getBrandStatus() {
		return brandStatus;
	}

	public void setBrandStatus(int brandStatus) {
		this.brandStatus = brandStatus;
	}

	public int getMcStatus() {
		return mcStatus;
	}

	public void setMcStatus(int mcStatus) {
		this.mcStatus = mcStatus;
	}

	public int getWarehouseStatus() {
		return warehouseStatus;
	}

	public void setWarehouseStatus(int warehouseStatus) {
		this.warehouseStatus = warehouseStatus;
	}

	public int getProductItemStatus() {
		return productItemStatus;
	}

	public void setProductItemStatus(int productItemStatus) {
		this.productItemStatus = productItemStatus;
	}

	public int getMcProductItemStatus() {
		return mcProductItemStatus;
	}

	public void setMcProductItemStatus(int mcProductItemStatus) {
		this.mcProductItemStatus = mcProductItemStatus;
	}

}
