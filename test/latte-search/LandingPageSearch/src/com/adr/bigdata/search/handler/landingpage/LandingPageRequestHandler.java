package com.adr.bigdata.search.handler.landingpage;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.handler.CommonSearchHandler;
import com.google.common.base.Strings;

public class LandingPageRequestHandler extends CommonSearchHandler {
	private static final String LANDING_PAGE_ORDER = "landing_page_%s_order";
	private static final String LANDING_PAGE_GROUP_ORDER = "landing_page_group_%s_order";
	
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String landingPageId = req.getParams().get(LANDING_PAGE_ID);
		String groupId = req.getParams().get(GROUP_ID);
		
		ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());
		buildLandingPageFilter(landingPageId, groupId, params);
		
		req.setParams(params);
		
		super.handleRequest(req, rsp);
	}
	
	private void buildLandingPageFilter(String landingPageId, String groupId, ModifiableSolrParams solrParams) {
		assert (!Strings.isNullOrEmpty(landingPageId));
		String landingOrderField = String.format(LANDING_PAGE_ORDER, landingPageId);
		solrParams.add(CommonParams.FQ, landingOrderField + ":*");
		if (Strings.isNullOrEmpty(groupId)) {
			solrParams.add(CommonParams.SORT, landingOrderField + " asc");
		} else {
			String groupOrderField = String.format(LANDING_PAGE_GROUP_ORDER, groupId);
			solrParams.add(CommonParams.FQ, groupOrderField + ":*");
			solrParams.add(CommonParams.SORT, groupOrderField + " asc");
		}
	}
	
	public static final String LANDING_PAGE_ID = "ldpageid";
	public static final String GROUP_ID = "groupid";
}
