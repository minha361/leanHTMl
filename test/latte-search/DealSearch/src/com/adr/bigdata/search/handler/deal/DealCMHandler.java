package com.adr.bigdata.search.handler.deal;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.deal.DealFields;
import com.adr.bigdata.search.deal.DealParams;
import com.adr.bigdata.search.deal.DealUtils;
import com.google.common.base.Strings;

public class DealCMHandler extends SearchHandler {
	private static final String QUERY = String.format("_query_:{!df=%s v=$keyword} "
			+ "_query_:{!q.op=AND df=%s v=$keyword} "
			+ "_query_:{!q.op=AND df=%s v=$keyword} "
			+ "_query_:{!df=%s v=$mcName}", DealFields.NAME, DealFields.ID, DealFields.VIEW_ID, DealFields.MERCHANT_NAME);
	
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		SolrParams params = req.getParams();

		String keyword = params.get(DealParams.KEYWORD);
		if (Strings.isNullOrEmpty(keyword)) {
			keyword = "*";
		}
		String sortBy = params.get(DealParams.SORT_BY);
		String order = params.get(DealParams.ORDER);
		Integer offset = params.getInt(DealParams.OFFSET);
		Integer limit = params.getInt(DealParams.LIMIT);

		String[] priceFilters = DealUtils.wrapRange(params.getParams(DealParams.PRICE_FILTER));
		String[] promotionFilters = DealUtils.wrapRange(params.getParams(DealParams.PROMOTION_FILTER));
		String[] desIdFilters = DealUtils.standardize(params.getParams(DealParams.DES_ID_FILTER));
		String[] catIds = DealUtils.standardize(params.getParams(DealParams.CAT_ID));
		String[] cityIds = DealUtils.standardize(params.getParams(DealParams.CITY_ID));
		
		String[] merchantIds = DealUtils.standardize(params.getParams(DealParams.MERCHANT_ID_FILTER));
		String merchantName = params.get(DealParams.MERCHANT_NAME_FILTER);
		
		SolrParamsBuilder reqParamsBuilder = new SolrParamsBuilder()
				.keyword(QUERY)
				.add("keyword", keyword)
				.add("mcName", merchantName)
				.offset(offset).limit(limit)
				.sort(sortBy, order)
				.filter(DealFields.SELL_PRICE, priceFilters)
				.filter(DealFields.DISCOUNT_PERCENT, promotionFilters)
				.filter(DealFields.DESTINATION_IDS, desIdFilters)
				.filter(DealFields.CAT_PATH, catIds)
				.filter(DealFields.CITY_IDS, cityIds)
				.filter(DealFields.MERCHANT_ID, merchantIds);
		
		req.setParams(reqParamsBuilder.getParams());
		super.handleRequest(req, rsp);
	}
}
