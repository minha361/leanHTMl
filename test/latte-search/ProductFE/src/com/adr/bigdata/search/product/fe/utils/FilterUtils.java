package com.adr.bigdata.search.product.fe.utils;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;

public class FilterUtils {
	private static final String FINAL_SCORE_TEMPLATE = "sum(product(boost_score,1000000),def(%s,%s))";
	private static final String FEATURE_TAG = "{!tag=" + FacetUtils.EX_FEATURE + "}";
	private static final String FILTER_PROMO_TEMPLATE = "(is_promotion_mapping:true AND is_promotion:true AND start_time_discount:[ * TO %d] AND finish_time_discount:[%d TO *]) OR (is_not_apply_commision:true) ";
	private static final String FILTER_NEW_TEMPLATE = "create_time:[ %s TO *]";

	public static final SolrParamsBuilder filterByCity(SolrParamsBuilder builder, Integer cityId) {
		if (cityId != null) {
			String cityIdString = cityId.toString();
			return builder.filter(ProductFields.SERVED_PROVINCE_IDS, cityIdString, "0")
					.filter(ProductFields.RECEIVED_CITY_ID, cityIdString)
					.filter(ProductFields.cityScore(cityId), "[0 TO *]");
		}
		return builder;
	}

	public static final SolrParamsBuilder filterByDistrict(SolrParamsBuilder builder, Integer cityId,
			Integer districtId) {
		if (cityId != null && districtId != null) {
			return builder.filter(ProductFields.SERVED_DISTRICT_IDS, districtId.toString(), "0", cityId + "_0");
		}
		return builder;
	}

	public static final SolrParamsBuilder collapseByProductItemGroup(SolrParamsBuilder builder, Integer cityId) {
		int city = cityId == null ? 0 : cityId;
		String finalScore = String.format(FINAL_SCORE_TEMPLATE, ProductFields.cityScore(city), "-10000000");
		return builder.collapse(ProductFields.PRODUCT_ITEM_GROUP, finalScore);
	}

	public static final SolrParamsBuilder filterByCategory(SolrParamsBuilder builder, Integer catId) {
		if (catId != null) {
			return builder.filterWithTag(ProductFields.CATEGORY_PATH, FacetUtils.EX_CAT, catId.toString());
		}
		return builder;
	}
	
	public static final SolrParamsBuilder filterByCategory(SolrParamsBuilder builder, String... catIds) {
		if (catIds != null && catIds.length != 0) {
			return builder.filterWithTag(ProductFields.CATEGORY_PATH, FacetUtils.EX_CAT, catIds);
		}
		return builder;
	}

	public static final SolrParamsBuilder filterByBrand(SolrParamsBuilder builder, String... brandIds) {
		if (brandIds != null && brandIds.length != 0) {
			return builder.filterWithTag(ProductFields.BRAND_ID, FacetUtils.EX_BRAND, brandIds);
		}
		return builder;
	}

	public static final SolrParamsBuilder filterByMerchant(SolrParamsBuilder builder, String... merchantIds) {
		if (merchantIds != null && merchantIds.length != 0) {
			return builder.filterWithTag(ProductFields.MERCHANT_ID, FacetUtils.EX_MERCHANT, merchantIds);
		}
		return builder;
	}

	public static final SolrParamsBuilder filterByAttributeInt(SolrParamsBuilder builder, Integer attributeId,
			String... attributeValueIds) {
		if (attributeId != null && attributeValueIds != null && attributeValueIds.length != 0) {
			return builder.filterWithTag(ProductFields.attrInt(attributeId), FacetUtils.excludeAttribute(attributeId),
					attributeValueIds);
		}
		return builder;
	}

	// TODO make $finalPrice in solrconfig.xml
	public static final SolrParamsBuilder filterByPrice(SolrParamsBuilder builder, Integer min, Integer max) {
		if (min != null && max != null) {
			return builder.filterWithExistField("{!frange l=" + min + " u=" + max + "}$finalPrice");
		}
		return builder;
	}

	public static SolrParamsBuilder filterByFeatured(SolrParamsBuilder builder, Boolean _isNew, Boolean _isPromotion, String extra) {
		boolean isNew = _isNew == null ? false : _isNew;
		boolean isPromotion = _isPromotion == null ? false : _isPromotion;
		if (isNew && isPromotion) {
			long now = System.currentTimeMillis();
			long deltaTime = now - FacetUtils.IS_NEW_TIME_WINDOWS;
			String query = FEATURE_TAG + String.format(FILTER_NEW_TEMPLATE, deltaTime) + " OR "
					+ String.format(FILTER_PROMO_TEMPLATE, now, now) + extra;
			return builder.filterWithExistField(query);
		} else {
			if (isNew) {
				long deltaTime = System.currentTimeMillis() - FacetUtils.IS_NEW_TIME_WINDOWS;
				String query = FEATURE_TAG + String.format(FILTER_NEW_TEMPLATE, deltaTime);
				return builder.filterWithExistField(query);
			} else if (isPromotion) {
				long now = System.currentTimeMillis();
				String query = FEATURE_TAG + String.format(FILTER_PROMO_TEMPLATE, now, now) + extra;
				return builder.filterWithExistField(query);
			} else {
				return builder;
			}
		}
	}
}
