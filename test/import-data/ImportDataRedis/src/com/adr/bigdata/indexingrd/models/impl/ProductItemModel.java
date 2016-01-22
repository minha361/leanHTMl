package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class ProductItemModel extends RdCachedModel {
	public static final String PI_ID = "productItemId";
	public static final String PI_PRODUCT_ID = "productId";
	public static final String PI_BRAND_ID = "brandId";
	public static final String PI_BRAND_NAME = "brandName";
	public static final String PI_CAT_ID = "categoryId";
	public static final String PI_BARCODE = "barcode";
	public static final String PI_PRODUCT_ITEM_NAME = "productItemName";
	public static final String PI_PRODUCT_ITEM_STATUS = "productItemStatus";
	public static final String PI_FRESH_FOOD_TYPE = "freshFoodType";
	public static final String PI_WEIGHT = "weight";
	public static final String PI_CREATE_TIME = "createTime";
	public static final String PI_SOLR_FE_PRODUCT_ATTRIBUTE = "solrFeProductAttribute";
	public static final String PI_ATT_ID = "attributeId";
	public static final String PI_ATT_NAME = "attributeName";
	public static final String PI_ATT_VALUE_ID = "attributeValueId";
	public static final String PI_ATT_VALUE = "attributeValue";
	public static final String PI_ATT_STATUS = "attributeStatus";
	public static final String PI_WH_PI_MAPPING = "warehouseProductItemMapping";
	public static final String PI_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static final String PI_MC_ID = "merchantId";
	public static final String PI_WH_ID = "warehouseId";
	public static final String PI_MC_NAME = "merchantName";
	public static final String PI_MC_SKU = "merchantSKU";
	public static final String PI_ORI_PRICE = "originalPrice";
	public static final String PI_SELL_PRICE = "sellPrice";
	public static final String PI_QUANTITY = "quantity";
	public static final String PI_SAFETY_STOCK = "safetyStock";
	public static final String PI_MC_PI_STATUS = "merchantProductItemStatus";

	// ADD 07-05-2015
	public static final String PI_BRAND_STATUS = "brandStatus";
	public static final String PI_CATEGORY_NAME = "categoryName";
	public static final String PI_CATEGORY_STATUS = "categoryStatus";
	public static final String PI_PRODUCT_ITEM_TYPE = "productItemType";
	public static final String PI_CATEGORY_PATH = "categoryPath";
	public static final String PI_IMAGE = "image";
	public static final String PI_UPDATE_TIME = "updateTime";
	public static final String PI_MERCHANT_STATUS = "merchantStatus";
	public static final String PI_WAREHOUSE_STATUS = "warehouseStatus";
	public static final String PI_PROVINCE_ID = "provinceId";

	// add 16-05-2015
	public static final String PI_IS_VISIBLE = "isVisible";

	public static final String PI_VAT_STATUS = "vatStatus";
	public static final String PI_PRICE_STATUS = "priceStatus";

	// add 15-07-2015
	public static final String PI_PRODUCT_ITEM_POLICY = "productItemPolicy";
	
	// add 19-09-2015
	public static final String PI_IS_NOT_APPLY_COMMISION = "isNotApplyCommision";
	public static final String PI_COMMISION_FEE = "commisionFee";

	

	

	public List<ProductItemInfoUpdateBean> getWarehouseProductItemMappingOfProductItems(List<Integer> piIds)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class)) {
			return dao.getPIInfoWhenUpdate(piIds);
		}
	}

	
}
