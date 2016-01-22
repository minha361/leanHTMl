package com.adr.bigdata.search.product.fe;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.fe.logic.impl.filter.BrandFilterLogic;

public class BrandFilterHandler extends VinHandler {
	private BrandFilterLogic brandLogic;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			brandLogic = new BrandFilterLogic(modelFactory);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		try {
			brandLogic.execute(req, rsp, null);
			super.handleRequest(req, rsp);
			brandLogic.writeRsp(req, rsp);
		} catch (Exception e) {
			error(req, rsp);
			getLogger().error("", e);
		}
	}
}
