package com.adr.bigdata.search.handler.deal;

import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.deal.DealFields;
import com.adr.bigdata.search.deal.DealParams;

public class DealRelatedHandler extends SearchHandler {
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String name = req.getParams().get(DealParams.RL_NAME);
		String[] sCityIds = req.getParams().getParams(DealParams.CITY_ID);
		req.setParams(new SolrParamsBuilder().keyword(name).filter(DealFields.CITY_IDS, sCityIds).getParams());
		super.handleRequest(req, rsp);
	}
}
