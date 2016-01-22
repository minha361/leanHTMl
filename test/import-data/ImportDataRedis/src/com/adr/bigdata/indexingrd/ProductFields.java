package com.adr.bigdata.indexingrd;

/**
 * Lá»›p nÃ y chá»©a táº¥t cáº£ cÃ¡c hÃ m cÅ©ng nhÆ° phÆ°Æ¡ng thá»©c Ä‘á»ƒ láº¥y Ä‘Æ°á»£c tÃªn trÆ°á»�ng trong core product nhÃ©.
 * CÃ¡c trÆ°á»�ng thÆ°á»�ng thÃ¬ láº¥y field lÃ  Ä‘á»§, cÃ²n cÃ¡c trÆ°á»�ng Ä‘á»™ng thÃ¬ dÃ¹ng function
 * @author noind
 *
 */
public class ProductFields {
	public static final String attrTxt(int attId) {
		return String.format(ATTR_TXT_TEMPLATE, attId);
	}

	public static final String attrDouble(int attId) {
		return String.format(ATTR_DOUBLE_TEMPLATE, attId);
	}
	
	public static final String attrInt(int attId) {
		return String.format(ATTR_INT_TEMPLATE, attId);
	}
	
	public static final String attrTxtFacet(int attId) {
		return String.format(ATTR_TXT_FACET_TEMPLATE, attId);
	}

	public static final String attrDoubleFacet(int attId) {
		return String.format(ATTR_DOUBLE_FACET_TEMPLATE, attId);
	}
	
	public static final String attrIntFacet(int attId) {
		return String.format(ATTR_INT_FACET_TEMPLATE, attId);
	}

	public static final String cityScore(int cityId) {
		return String.format(SCORE_BY_CITY_TEMPLATE, cityId);
	}

	public static final String landingPage(int landingPageId) {
		return String.format(LANDING_PAGE_TEMPLATE, landingPageId);
	}

	public static final String landingPageGroup(int landingPageGroupId) {
		return String.format(LANDING_PAGE_GROUP_TEMPLATE, landingPageGroupId);
	}
	
	public static final String collectionGroup(int collectionGroupId) {
		return String.format(COLLECTION_GROUP_TEMPLATE, collectionGroupId);
	}
	
	public static final String random(long seed) {
		return String.format(RANDOM_SORT, seed);
	}

	public static final String PRODUCT_ITEM_ID_WAREHOUSE_ID = "product_item_id_warehouse_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_ITEM_GROUP = "product_item_group";
	public static final String PRODUCT_ITEM_ID = "product_item_id";
	public static final String ATTR_TXT_TEMPLATE = "attr_%d_txt";
	public static final String ATTR_DOUBLE_TEMPLATE = "attr_%d_d";
	public static final String ATTR_INT_TEMPLATE = "attr_%d_i";
	public static final String ATTR_TXT_FACET_TEMPLATE = "attr_%d_txt_facet";
	public static final String ATTR_DOUBLE_FACET_TEMPLATE = "attr_%d_d_facet";
	public static final String ATTR_INT_FACET_TEMPLATE = "attr_%d_i_facet";
	public static final String SCORE_BY_CITY_TEMPLATE = "city_%d_score";
	public static final String LANDING_PAGE_TEMPLATE = "landing_page_%d_order";
	public static final String LANDING_PAGE_GROUP_TEMPLATE = "landing_page_group_%d_order";
	public static final String BRAND_ID = "brand_id";
	public static final String BRAND_ID_FACET = "brand_id_facet";
	public static final String BRAND_NAME = "brand_name";
	public static final String CATEGORY_ID = "category_id";
	public static final String CATEGORY_ID_FACET = "category_id_facet";
	public static final String CATEGORY_PATH = "category_path";
	public static final String WAREHOUSE_ID = "warehouse_id";
	public static final String MERCHANT_ID = "merchant_id";
	public static final String MERCHANT_ID_FACET = "merchant_id_facet";
	public static final String MERCHANT_NAME = "merchant_name";
	public static final String MERCHANT_PRODUCT_ITEM_SKU = "merchant_product_item_sku";
	public static final String MERCHANT_PRODUCT_ITEM_SKU_CM = "merchant_product_item_sku_cm";
	public static final String DISCOUNT_PERCENT = "discount_percent";
	public static final String START_TIME_DISCOUNT = "start_time_discount";
	public static final String FINISH_TIME_DISCOUNT = "finish_time_discount";
	public static final String BARCODE = "barcode";
	public static final String COUNT_SELL = "count_sell";
	public static final String COUNT_VIEW = "count_view";
	public static final String VIEWED_TOTAL = "viewed_total";
	public static final String VIEWED_YEAR = "viewed_year";
	public static final String VIEWED_MONTH = "viewed_month";
	public static final String VIEWED_WEEK = "viewed_week";
	public static final String VIEWED_DAY = "viewed_day";
	public static final String SELL_PRICE = "sell_price";
	public static final String PRODUCT_ITEM_NAME = "product_item_name";
	public static final String UNTOKENIZED_PRODUCT_ITEM_NAME = "untokenized_product_item_name";
	public static final String SPELL = "spell";
	public static final String CREATE_TIME = "create_time";
	public static final String IS_PROMOTION = "is_promotion";
	public static final String IS_PROMOTION_MAPPING = "is_promotion_mapping";
	public static final String QUANTITY = "quantity";
	public static final String IS_CATEGORY_ACTIVE = "is_category_active";
	public static final String IS_BRAND_ACTIVE = "is_brand_active";
	public static final String IS_MERCHANT_ACTIVE = "is_merchant_active";
	public static final String WAREHOUSE_STATUS = "warehouse_status";
	public static final String PRODUCT_ITEM_STATUS = "product_item_status";
	public static final String MERCHANT_PRODUCT_ITEM_STATUS = "merchant_product_item_status";
	public static final String RECEIVED_CITY_ID = "received_city_id";
	public static final String RECEIVED_CITY_ID_FACET = "received_city_id_facet";
	public static final String WEIGHT = "weight";
	public static final String PRODUCT_ITEM_TYPE = "product_item_type";
	public static final String IMAGE = "image";
	public static final String VISIBLE = "visible";
	public static final String ON_SITE = "on_site";
	public static final String PRODUCT_ITEM_ID_MERCHANT_ID = "product_item_id_merchant_id";
	public static final String VAT_STATUS = "vat_status";
	public static final String PRICE_STATUS = "price_status";
	public static final String CATEGORY_FACET = "category_facet";
	public static final String MERCHANT_FACET = "merchant_facet";
	public static final String BRAND_FACET = "brand_facet";
	public static final String PROMOTION_PRICE = "promotion_price";
	public static final String BOOST_SCORE = "boost_score";
	public static final String PRODUCT_ITEM_POLICY = "product_item_policy";
	public static final String PRODUCT_ITEM_POLICY_BITS = "product_item_policy_bits";
	public static final String SERVED_PROVINCE_IDS = "served_province_ids";
	public static final String SERVED_DISTRICT_IDS = "served_district_ids";
	public static final String SERVED_WARD_IDS = "served_ward_ids";
	public static final String CATEGORY_TREE = "category_tree";
	public static final String ATTRIBUTE_SEARCH = "attribute_search";
	public static final String IMAGE_HISTOGRAM = "image_histogram";
	public static final String PRODUCT_ITEM_NAME_CM = "product_item_name_cm";
	public static final String PRICE_FLAG = "price_flag";
	public static final String TAGS = "tags";
	public static final String IS_NOT_APPLY_COMMISION = "is_not_apply_commision";
	public static final String COMMISION_FEE = "commision_fee";
	public static final String RANDOM_SORT = "random_%d";
	public static final String COLLECTION_GROUP_TEMPLATE = "collection_group_%d_order";
}
