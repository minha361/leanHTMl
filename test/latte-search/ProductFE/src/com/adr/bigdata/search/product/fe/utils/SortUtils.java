package com.adr.bigdata.search.product.fe.utils;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;

public class SortUtils {
	public static final long seed = System.currentTimeMillis();
	
	public static SolrParamsBuilder sort(SolrParamsBuilder builder, String sortType, String order) {
		if (!Strings.isNullOrEmpty(sortType) && ("asc".equals(order) || "desc".equals(order))) {
			switch (sortType.trim()) {
			case SortType.SORT_BY_BUY:
				return builder.sort(ProductFields.COUNT_SELL, order);
			case SortType.SORT_BY_NEW:
				return builder.sort(ProductFields.CREATE_TIME, order);
			case SortType.SORT_BY_PRICE:
				// TODO $finalPrice in solrconfig.xml
				return builder.sort("$finalPrice", order);
			case SortType.SORT_BY_QUANTITY:
				return builder.sort(ProductFields.QUANTITY, order);
			case SortType.SORT_BY_VIEW:
				return builder.sort(ProductFields.COUNT_VIEW, order);
			case SortType.SORT_BY_VIEW_DAY:
				return builder.sort(ProductFields.VIEWED_DAY, order);
			case SortType.SORT_BY_VIEW_MONTH:
				return builder.sort(ProductFields.VIEWED_MONTH, order);
			case SortType.SORT_BY_VIEW_TOTAL:
				return builder.sort(ProductFields.VIEWED_TOTAL, order);
			case SortType.SORT_BY_VIEW_WEEK:
				return builder.sort(ProductFields.VIEWED_WEEK, order);
			case SortType.SORT_BY_VIEW_YEAR:
				return builder.sort(ProductFields.VIEWED_YEAR, order);
			default:
				break;
			}
		} else {
			builder.add("sort", "score desc, " + ProductFields.random(seed) + " desc");
		}
		return builder;
	}
}
