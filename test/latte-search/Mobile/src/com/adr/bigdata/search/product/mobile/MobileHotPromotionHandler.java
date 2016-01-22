package com.adr.bigdata.search.product.mobile;

import java.util.Arrays;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class MobileHotPromotionHandler extends SearchHandler {
	private static final String PROMOTION_PERCENT = "product(div(sub(sell_price,def(promotion_price,sell_price)),sell_price),100)";
	private static final String PROMOTION_QUERY = "is_promotion_mapping:true AND is_promotion:true AND start_time_discount:[ * TO %d] AND finish_time_discount:[%d TO *]";

	@Override
	public void handleRequest(SolrQueryRequest request, SolrQueryResponse rsp) {
		ModifiableSolrParams solrParams = new ModifiableSolrParams(request.getParams());
		String listCatIds = request.getParams().get("catIds", "");
		if (!Strings.isNullOrEmpty(listCatIds)) {
			filterCatIds(listCatIds.split(","), solrParams);
		}

		String fromDiscount = request.getParams().get("fromDiscount", "");
		String toDiscount = request.getParams().get("toDiscount", "");
		solrParams.set("discountPercent", PROMOTION_PERCENT);
		if (!Strings.isNullOrEmpty(fromDiscount) & !Strings.isNullOrEmpty(toDiscount)) {
			String promotionFilter = String.format("{!frange l=%s u=%s}$discountPercent", fromDiscount, toDiscount);
			long now = System.currentTimeMillis();
			String isPromotionQuery = String.format(PROMOTION_QUERY, now, now);
			solrParams.add(CommonParams.FQ, promotionFilter);
			solrParams.add(CommonParams.FQ, isPromotionQuery);
		}
		String start = request.getParams().get("offset", "0");
		String rows = request.getParams().get("limit", "24");
		solrParams.set(CommonParams.ROWS, rows);
		solrParams.set(CommonParams.START, start);
		solrParams.set(CommonParams.SORT, "$discountPercent desc");

		request.setParams(solrParams);
		
		super.handleRequest(request, rsp);
	}

	private void filterCatIds(String[] catIdsArr, ModifiableSolrParams solrParams) {
		String fqQuery = Joiner.on(" OR category_path: ").join(Arrays.asList(catIdsArr));
		solrParams.add(CommonParams.FQ, "category_path:" + fqQuery);
	}
}
