package com.adr.bigdata.search.product.fe.utils;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;

public class FacetUtils {
	public static final String EX_CAT = "cat";
	public static final String EX_MERCHANT = "mc";
	public static final String EX_BRAND = "brand";
	private static final String EX_ATTR_TEMPLATE = "att_";
	public static final String EX_FEATURE = "featured";
	
	private static final String PROMOTION_QUERY = "{!ex=%s key=%s}"
			+ "(is_promotion_mapping:true AND is_promotion:true AND start_time_discount:[ * TO %d] AND finish_time_discount:[%d TO *]) "
			+ "OR (is_not_apply_commision:true) ";
	public static final long IS_NEW_TIME_WINDOWS = 604800000;// 7 days
	
	public static final String IS_PROMOTION_KEY = "promotion";
	public static final String IS_NEW_KEY = "new";
	
	public static String excludeAttribute(int attId) {
		return EX_ATTR_TEMPLATE + attId;
	}
	
	public static SolrParamsBuilder facetByCategory(SolrParamsBuilder builder) {
		return builder.facetField(ProductFields.CATEGORY_ID, EX_CAT)
				.add("f." + ProductFields.CATEGORY_ID + ".facet.limit", "15")
				.add("f." + ProductFields.CATEGORY_ID + ".facet.mincount", "1");
	}
	
	public static SolrParamsBuilder facetByMerchant(SolrParamsBuilder builder) {
		return builder.facetField(ProductFields.MERCHANT_ID, EX_MERCHANT)
				.add("f." + ProductFields.MERCHANT_ID + ".facet.limit", "100")
				.add("f." + ProductFields.MERCHANT_ID + ".facet.mincount", "1");
	}
	
	public static SolrParamsBuilder facetByBrand(SolrParamsBuilder builder) {
		return builder.facetField(ProductFields.BRAND_ID, EX_BRAND)
				.add("f." + ProductFields.BRAND_ID + ".facet.limit", "100")
				.add("f." + ProductFields.BRAND_ID + ".facet.mincount", "1");
	}
	
	public static SolrParamsBuilder facetByAttributeInt(SolrParamsBuilder builder, int attId) {
		return builder.facetField(ProductFields.attrInt(attId), excludeAttribute(attId))
				.add("f." + ProductFields.attrInt(attId) + ".facet.limit", "100")
				.add("f." + ProductFields.attrInt(attId) + ".facet.mincount", "1");
	}
	
	public static SolrParamsBuilder facetPromotion(SolrParamsBuilder builder, String extra) {
		long now = System.currentTimeMillis();
		String promotionFacetQuery = String.format(PROMOTION_QUERY, EX_FEATURE, IS_PROMOTION_KEY, now, now) + extra;
		return builder.add("facet.query", promotionFacetQuery);
	}
	
	public static SolrParamsBuilder facetNew(SolrParamsBuilder builder) {
		long now = System.currentTimeMillis();
		String isNewFacetQuery = "{!ex=" + EX_FEATURE + " key=" + IS_NEW_KEY + "}create_time:[ " + (now - IS_NEW_TIME_WINDOWS) + " TO *]";
		return builder.add("facet.query", isNewFacetQuery);
	}
}
