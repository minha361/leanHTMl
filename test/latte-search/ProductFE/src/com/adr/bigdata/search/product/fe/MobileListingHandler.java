package com.adr.bigdata.search.product.fe;

import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.fe.logic.impl.CategoryListingLogic;

public class MobileListingHandler extends VinHandler {
	private CategoryListingLogic logic = null;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			logic = new CategoryListingLogic(modelFactory);
		} catch (Exception e) {
			getLogger().error("", e);
		}
		core.addCloseHook(new CloseHook() {

			@Override
			public void preClose(SolrCore arg0) {
				if (logic != null) {
					logic.close();
				}
			}

			@Override
			public void postClose(SolrCore arg0) {
			}
		});
	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		try {

			logic.execute(req, rsp, null);
			super.handleRequest(req, rsp);
		} catch (Exception e) {
			error(req, rsp);
			getLogger().error("", e);
		}
	}
}
