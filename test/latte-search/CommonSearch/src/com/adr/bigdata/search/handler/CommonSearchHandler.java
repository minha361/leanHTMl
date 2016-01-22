package com.adr.bigdata.search.handler;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class CommonSearchHandler extends SearchHandler {
	@Override
    public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());
		String offset = req.getParams().get("offset", "0");
		String limit = req.getParams().get("limit", "24");
		params.add(CommonParams.ROWS, limit);
		params.add(CommonParams.START, offset);
		req.setParams(params);
		super.handleRequest(req, rsp);
	}
}
