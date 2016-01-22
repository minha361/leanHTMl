package com.adr.bigdata.search.product.fe;

import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.fe.logic.impl.CategoryListingLogic;
import com.adr.bigdata.search.product.fe.logic.impl.RedirectKeywordLogic;
import com.nhb.eventdriven.Callable;

public class SearchListingHandler extends VinHandler {
	private CategoryListingLogic logic = null;
	private RedirectKeywordLogic keywordLogic = null;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			logic = new CategoryListingLogic(modelFactory);
			keywordLogic = new RedirectKeywordLogic(modelFactory);
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
			final Object[] data = new Object[2];
			keywordLogic.execute(req, rsp, new Callable() {				
				@Override
				public void call(Object... args) {
					data[0] = args[0];
					data[1] = args[1];
				}
			});
			boolean redirect = (boolean) data[0];
			if (redirect) {
				String url = (String) data[1];
				super.handleRequest(req, rsp);
				keywordLogic.writeRsp(req, rsp, url);
			} else {
				logic.execute(req, rsp, null);
				super.handleRequest(req, rsp);
			}
		} catch (Exception e) {
			error(req, rsp);
			getLogger().error("", e);
		}
	}
}
