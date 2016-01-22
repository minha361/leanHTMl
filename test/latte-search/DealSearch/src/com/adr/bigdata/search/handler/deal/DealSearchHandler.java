package com.adr.bigdata.search.handler.deal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adr.bigdata.search.deal.DealFields;
import com.adr.bigdata.search.deal.DealParams;
import com.adr.bigdata.search.deal.DealUtils;
import com.google.common.base.Strings;

/**
 * @author noind
 *
 */
public class DealSearchHandler extends SearchHandler {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		SolrParams params = req.getParams();

		String keyword = params.get(DealParams.KEYWORD);
		String sortBy = params.get(DealParams.SORT_BY);
		String order = params.get(DealParams.ORDER);
		Integer catId = params.getInt(DealParams.CAT_ID);
		Integer offset = params.getInt(DealParams.OFFSET);
		Integer limit = params.getInt(DealParams.LIMIT);
		Integer catIdLevel = params.getInt(DealParams.CAT_ID_LEVEL);

		String[] priceFilters = DealUtils.wrapRange(params.getParams(DealParams.PRICE_FILTER));
		String[] promotionFilters = DealUtils.wrapRange(params.getParams(DealParams.PROMOTION_FILTER));
		String[] desIdFilters = DealUtils.standardize(params.getParams(DealParams.DES_ID_FILTER));
		String[] catIdFilters = DealUtils.standardize(params.getParams(DealParams.CAT_ID_FILTER));
		String[] cityIds = DealUtils.standardize(params.getParams(DealParams.CITY_ID));
		
		String[] merchantIdFilter = DealUtils.standardize(params.getParams(DealParams.MERCHANT_ID_FILTER));
		String[] merchantNameFilter = DealUtils.standardize(params.getParams(DealParams.MERCHANT_NAME_FILTER));

		if (catId != null && catIdLevel == null) {
			throw new IllegalArgumentException("Đã truyền cat_id thì phải truyền cat_id_level anh ơi -_-");
		}

		String userCatFilterField = null;
		if (catIdLevel != null) {
			if (catIdLevel == 1) {
				userCatFilterField = DealFields.CAT_2_FACET;
			} else if (catIdLevel == 2) {
				userCatFilterField = DealFields.CAT_3_FACET;
			} else if (catIdLevel == 3) {
				userCatFilterField = DealFields.CAT_FACET;
			}
		}

		SimpleOrderedMap userAction = getUserAction(priceFilters, promotionFilters, desIdFilters, catIdFilters,
				userCatFilterField, req.getCore());

		SolrParamsBuilder reqParamsBuilder = new SolrParamsBuilder().keyword(keyword).offset(offset).limit(limit)
				.sort(sortBy, order).filterWithTag(DealFields.SELL_PRICE, "price", priceFilters)
				.filterWithTag(DealFields.DISCOUNT_PERCENT, "promo", promotionFilters)
				.filterWithTag(DealFields.DESTINATION_IDS, "des", desIdFilters)
				.filterWithTag(DealFields.CAT_PATH, "cat", catIdFilters).filter(DealFields.CITY_IDS, cityIds).facet(1)
				.facetField(DealFields.DESTINATION_FACETS, "des")
				.facetRange(DealFields.DISCOUNT_PERCENT, 0, 100, 10, "promo").stats()
				.statsField(DealFields.SELL_PRICE, "cat", "des", "promo", "price");
		
		if(merchantIdFilter != null && merchantIdFilter.length > 0) {
			reqParamsBuilder.filter(DealFields.MERCHANT_ID, merchantIdFilter);
		}
		if(merchantNameFilter != null && merchantNameFilter.length > 0 ) {
			reqParamsBuilder.filter(DealFields.MERCHANT_NAME, merchantNameFilter);
		}
		
		if (catId != null) {
			reqParamsBuilder.filterWithTag(DealFields.CAT_PATH, "cat_ori", catId.toString());
		}
		if (userCatFilterField != null) {
			reqParamsBuilder.facetField(userCatFilterField, "cat");
		}
		if (Strings.isNullOrEmpty(keyword)) {
			reqParamsBuilder.facetField(DealFields.CAT_3_FACET, "cat");
		} else {
			reqParamsBuilder.facetField(DealFields.CAT_3_FACET, "cat", "cat_ori");
		}
				
		SolrParams reqParams = reqParamsBuilder.getParams();
		if (this.defaults != null) {
			reqParams = SolrParams.wrapDefaults(reqParams, defaults);
		}
		if (this.appends != null) {
			reqParams = SolrParams.wrapAppended(reqParams, appends);
		}
		getLogger().debug("reqParams: {}", reqParams.toString());
		req.setParams(reqParams);
		req.getCore().getRequestHandler("/select").handleRequest(req, rsp);
		processResponse(rsp, userCatFilterField);

		rsp.getValues().add("userAction", userAction);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> splitToGetInfo(NamedList userResponse, int limit, int iKey, int iValue,
			String... path) {
		NamedList facet = (NamedList) userResponse.findRecursive(path);
		Map<String, String> facetMap = new HashMap<>();
		for (int i = 0; i < facet.size(); i++) {
			String key = facet.getName(i);
			String[] elements = key.split("_", limit);
			facetMap.put(elements[iKey], elements[iValue]);
		}
		return facetMap;
	}

	/**
	 * Cái này nó rất chi là đặc biệt đó, người dùng chọn filter nào, FE2 gửi mã
	 * sự lựa chọn nào mình sẽ phải trả về full sự lựa chọn đó
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SimpleOrderedMap getUserAction(String[] priceFilters, String[] promotionFilters, String[] desIdFilters,
			String[] catIdFilters, String userCatFilterField, SolrCore core) {
		SimpleOrderedMap userAction = new SimpleOrderedMap<>();

		SolrParamsBuilder userFilterParamsBuilder = new SolrParamsBuilder().keyword("*:*").limit(0).facet(1)
				.facetField(DealFields.DESTINATION_FACETS);
		if (userCatFilterField != null) {
			userFilterParamsBuilder.facetField(userCatFilterField);
		}
		SolrParams userFilterParams = userFilterParamsBuilder.getParams();
		SolrQueryRequestBase req = new SolrQueryRequestBase(core, userFilterParams) {
		};
		SolrQueryResponse rsp = new SolrQueryResponse();
		try {
			core.getRequestHandler("/select").handleRequest(req, rsp);
		} finally {
			req.close();
		}		
		NamedList userResponse = rsp.getValues();
		Map<String, String> catFacetMap = null;
		if (userCatFilterField != null) {
			catFacetMap = splitToGetInfo(userResponse, 4, 0, 3, "facet_counts", "facet_fields", userCatFilterField);
		}
		Map<String, String> desFacetMap = splitToGetInfo(userResponse, 4, 0, 3, "facet_counts", "facet_fields",
				DealFields.DESTINATION_FACETS);

		// promotion
		List<SimpleOrderedMap> listPromotionFilter = new ArrayList<>();
		if (promotionFilters != null)
			for (String promotionFilter : promotionFilters) {
				String[] minMax = promotionFilter.replaceAll("\\[|\\]", "").split(" TO ", 2);
				SimpleOrderedMap som = new SimpleOrderedMap<>();
				som.add("minpromotion", minMax[0]);
				som.add("maxpromotion", minMax[1]);
				listPromotionFilter.add(som);
			}
		// price
		List<SimpleOrderedMap> listPriceFilter = new ArrayList<>();
		if (priceFilters != null)
			for (String priceFilter : priceFilters) {
				String[] minMax = priceFilter.replaceAll("\\[|\\]", "").split(" TO ", 2);
				SimpleOrderedMap som = new SimpleOrderedMap<>();
				som.add("min", minMax[0]);
				som.add("max", minMax[1]);
				listPriceFilter.add(som);
			}
		// destination
		List<SimpleOrderedMap> listDestinationFilter = new ArrayList<>();
		if (desIdFilters != null)
			for (String desId : desIdFilters) {
				if (desFacetMap.containsKey(desId)) {
					SimpleOrderedMap som = new SimpleOrderedMap<>();
					som.add("destinationid", desId);
					som.add("destinationname", desFacetMap.get(desId));
					som.add("numfound", 0);
					listDestinationFilter.add(som);
				}
			}
		// categoryFilter
		List<SimpleOrderedMap> listCategoryFilter = new ArrayList<>();
		if (catIdFilters != null && catFacetMap != null) {
			for (String catId : catIdFilters)
				if (catFacetMap.containsKey(catId.toString())) {
					SimpleOrderedMap som = new SimpleOrderedMap<>();
					som.add("categoryid", catId);
					som.add("categoryname", catFacetMap.get(catId.toString()));
					som.add("numfound", 0);
					som.add("sort", 0);
					listCategoryFilter.add(som);
				}
		}

		userAction.add("listDestinationFilter", listDestinationFilter);
		userAction.add("listPriceFilter", listPriceFilter);
		userAction.add("listPromotionFilter", listPromotionFilter);
		userAction.add("listCategoryFilter", listCategoryFilter);
		return userAction;
	}

	@SuppressWarnings("rawtypes")
	private void processResponse(SolrQueryResponse rsp, String userCatFilterField) {
		NamedList facetCounts = (NamedList) rsp.getValues().get("facet_counts");
		if (facetCounts != null) {
			NamedList facetFields = (NamedList) facetCounts.get("facet_fields");
			if (facetFields != null) {
				DealUtils.extractCategoryFilter(rsp, facetFields, userCatFilterField);
				DealUtils.extractCategory3(rsp, facetFields);
				DealUtils.extractDestination(rsp, facetFields);
			}

			NamedList facetRanges = (NamedList) facetCounts.get("facet_ranges");
			DealUtils.extractPromotion(rsp, facetRanges);
		}
		DealUtils.extractPrice(rsp, rsp.getValues());
	}
	
	private Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}
}
