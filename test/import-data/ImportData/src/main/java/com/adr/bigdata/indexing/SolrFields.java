package com.adr.bigdata.indexing;

public class SolrFields {
	public static final String PRODUCT_ITEM_ID_WAREHOUSE_ID = "product_item_id_warehouse_id";
	public static final String WAREHOUSE_ID = "warehouse_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_ITEM_GROUP = "product_item_group";
	public static final String PRODUCT_ITEM_ID = "product_item_id";
	public static final String ATT_NAME_S = "attr_name_%d_txt";
	public static final String ATT_S = "attr_%d_txt";
	public static final String ATT_I = "attr_%d_i";
	public static final String ATT_I_FACET = "attr_%d_i_facet";
	public static final String ATT_D = "attr_%d_d";
//	public static final String ATT_B = "attr_active_%d_b";
	public static final String BRAND_ID = "brand_id";
//	public static final String BRAND_ID_FACET = "brand_id_facet";
	public static final String BRAND_NAME = "brand_name";
	public static final String CATEGORY_ID = "category_id";
//	public static final String CATEGORY_ID_FACET = "category_id_facet";
	public static final String CATEGORY_PATH = "category_path";
	public static final String MERCHANT_ID = "merchant_id";
	public static final String MERCHANT_NAME = "merchant_name";
	public static final String MERCHANT_PRODUCT_ITEM_SKU = "merchant_product_item_sku";
//	public static final String DISCOUNT_PERCENT = "discount_percent";
	public static final String PROMOTION_PRICE = "promotion_price";
	public static final String START_TIME_DISCOUNT = "start_time_discount";
	public static final String FINISH_TIME_DISCOUNT = "finish_time_discount";
	public static final String BARCODE = "barcode";
//	public static final String COUNT_SELL = "count_sell";
//	public static final String COUNT_VIEW = "count_view";
	public static final String PRICE_FLAG = "price_flag";
	public static final String SELL_PRICE = "sell_price";
	public static final String PRODUCT_ITEM_NAME = "product_item_name";
	public static final String CREATE_TIME = "create_time";
//	public static final String IS_HOT = "is_hot";
//	public static final String IS_NEW = "is_new";
	public static final String IS_PROMOTION = "is_promotion";
	public static final String IS_PROMOTION_MAPPING = "is_promotion_mapping";
	public static final String QUANTITY = "quantity";
	public static final String IS_BRAND_ACTIVE = "is_brand_active";
	public static final String IS_CATEGORY_ACTIVE = "is_category_active";
	public static final String IS_MERCHANT_ACTIVE = "is_merchant_active";
	public static final String WAREHOUSE_STATUS = "warehouse_status";
	public static final String PRODUCT_ITEM_STATUS = "product_item_status";
	public static final String MERCHANT_PRODUCT_ITEM_STATUS = "merchant_product_item_status";
	
//	public static final String SAFETY_STOCK = "safety_stock";
//	public static final String IS_SAFETY_STOCK = "is_safety_stock";
	public static final String RECEIVED_CITY_ID = "received_city_id";
//	public static final String CITY_ID_FACET = "city_id_facet";
//	public static final String FRESH_FOOD_TYPE = "fresh_food_type";
	public static final String WEIGHT = "weight";
	
	public static final String PRODUCT_ITEM_TYPE = "product_item_type";
	public static final String IMAGE = "image";
			
	//add visible
	public static final String VISIBLE = "visible";
	public static final String ON_SITE = "on_site";
	public static final String PRODUCT_ITEM_ID_MERCHANT_ID = "product_item_id_merchant_id";
	
	public static final String VAT_STATUS = "vat_status";
	public static final String PRICE_STATUS = "price_status";
	
	public static final String PRODUCT_ITEM_POLICY = "product_item_policy";
	public static final String PRODUCT_ITEM_POLICY_BITS = "product_item_policy_bits";
	
	public static final String SERVED_PROVINCE_IDS = "served_province_ids";
	public static final String SERVED_DISTRICT_IDS = "served_district_ids";
	public static final String SERVED_WARD_IDS = "served_ward_ids";
	public static final String BOOTS_SCORE = "boost_score";
	public static final String CITY_SCORE = "city_%s_score";
	
	//add 31/07/2015
	public static final String LANDING_PAGE_ORDER = "landing_page_%d_order";
	public static final String LANDING_PAGE_GROUP_ORDER = "landing_page_group_%d_order";
	
	// add 19-09-2015
	public static final String IS_NOT_APPLY_COMMISION = "is_not_apply_commision";
	public static final String COMMISION_FEE = "commision_fee";
	
	// add 23-12-2015
	public static final String COLLECTION_GROUP_ORDER = "collection_group_%d_order";
}
