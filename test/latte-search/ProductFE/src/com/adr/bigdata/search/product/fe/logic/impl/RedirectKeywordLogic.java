package com.adr.bigdata.search.product.fe.logic.impl;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.logic.ModeledLogic;
import com.adr.bigdata.search.product.fe.model.RedirectKeywordModel;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;
import com.nhb.eventdriven.Callable;

public class RedirectKeywordLogic extends ModeledLogic {
	private RedirectKeywordModel model = null;

	public RedirectKeywordLogic(ModelFactory modelFactory) {
		super(modelFactory);
	}
	
	private void init(SolrQueryRequest req) throws Exception {
		try {
			if (model == null) {
				model = modelFactory.getModel(RedirectKeywordModel.class, req.getCore());
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@Override
	public void execute(SolrQueryRequest req, SolrQueryResponse rsp, Callable callBack) throws Exception {
		init(req);
		
		boolean redirect = false;
		String url = null;
		
		String keyword = req.getParams().get(CommonFilterLogic.P_KEYWORD);
		if (!Strings.isNullOrEmpty(keyword)) {
			url = model.getLink(keyword);
			if (!Strings.isNullOrEmpty(url)) {
				redirect = true;
				SolrParams params = new SolrParamsBuilder().limit(0).keyword("*:*").getParams();
				req.setParams(params);
			}
		}
		
		if (callBack != null) {
			callBack.call(redirect, url);
		}
	}

	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object... others) throws Exception {
		rsp.add("redirectLink", others[0]);		
	}

}
