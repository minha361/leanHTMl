/**
 * 
 */
package com.adr.bigdata.search.product.blog.util;

/**
 * @author minhvv2
 *
 */
public class ProductSchema {
	public static final String CATEGORY_PATH = "category_path";
	public static final String CITY_ID = "city_id";
	public static final String DISTRICT_ID = "district_id";
	public static final String SERVED_PROVINCE_IDS = "served_province_ids";
	public static final String RECEIVED_CITY_ID = "received_city_id";

	private static final String CITY_SCORE_FORMAT = "city_%s_score";
	public static final String SERVED_DISTRICT_IDS = "served_district_ids";
	public static final String PRODUCT_ITEM_GROUP = "product_item_group";
	public static final String VIEWED_DAY = "viewed_day";

	/**
	 * @param cityId
	 * @return
	 */
	public static String cityScore(String cityId) {
		return String.format(CITY_SCORE_FORMAT, cityId);
	}
}
