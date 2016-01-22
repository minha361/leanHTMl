package com.adr.bigdata.search.product.fe.utils;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;

public class StatisticUtils {
	public static SolrParamsBuilder byMaxPrice(SolrParamsBuilder builder) {
		return builder.stats().add("stats.field", "{!max=true}" + ProductFields.SELL_PRICE);
	}
}
