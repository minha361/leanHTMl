package com.adr.bigdata.search.product.fe;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.fe.logic.impl.filter.MerchantFilterLogic;

public class MerchantFilterHandler extends VinHandler {
	private MerchantFilterLogic mcLogic;
	
	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			mcLogic = new MerchantFilterLogic(modelFactory);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
	
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {	
		try {
			mcLogic.execute(req, rsp, null);
			super.handleRequest(req, rsp);
			mcLogic.writeRsp(req, rsp);
		}	catch (Exception e) {
			error(req, rsp);
			getLogger().error("", e);
		}
	}
}
