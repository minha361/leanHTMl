/**
 * 
 */
package com.adr.bigdata.search.product.blog.processor;

import org.apache.solr.common.params.ModifiableSolrParams;

import com.adr.bigdata.search.product.blog.bean.CommonBean;
import com.adr.bigdata.search.product.blog.util.ProductSchema;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;

/**
 * @author minhvv2
 *
 */
public abstract class AbstracProcessor {
	private static final String FINAL_SCORE_TEMPLATE = "sum(product(boost_score,1000000),def(%s,%s))";

	public void process(ModifiableSolrParams solrParams, CommonBean bean) {
		SolrParamsBuilder builder = new SolrParamsBuilder(solrParams);
		builder.filter(ProductSchema.CATEGORY_PATH, bean.getCatIds());
		filterByCity(builder, bean.getCityId());
		filterByDistrict(builder, bean.getCityId(), bean.getDistrictId());
		collapseByProductItemGroup(builder, bean.getCityId());
	}

	static final SolrParamsBuilder filterByCity(SolrParamsBuilder builder, String cityIdString) {
		if (!Strings.isNullOrEmpty(cityIdString)) {
			return builder.filter(ProductSchema.SERVED_PROVINCE_IDS, cityIdString, "0")
					.filter(ProductSchema.RECEIVED_CITY_ID, cityIdString)
					.filter(ProductSchema.cityScore(cityIdString), "[0 TO *]");
		}
		return builder;
	}

	static final SolrParamsBuilder filterByDistrict(SolrParamsBuilder builder, String cityId, String districtId) {
		if (!Strings.isNullOrEmpty(cityId) && !Strings.isNullOrEmpty(districtId)) {
			return builder.filter(ProductSchema.SERVED_DISTRICT_IDS, districtId, "0", cityId + "_0");
		}
		return builder;
	}

	static final SolrParamsBuilder collapseByProductItemGroup(SolrParamsBuilder builder, String cityId) {
		if (Strings.isNullOrEmpty(cityId)) {
			cityId = "0";
		}
		String finalScore = String.format(FINAL_SCORE_TEMPLATE, ProductSchema.cityScore(cityId), "-10000000");
		return builder.collapse(ProductSchema.PRODUCT_ITEM_GROUP, finalScore);
	}

}
