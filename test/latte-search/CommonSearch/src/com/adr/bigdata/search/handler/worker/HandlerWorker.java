package com.adr.bigdata.search.handler.worker;

import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.handler.BaseAdrSearchHandler;
import com.lmax.disruptor.WorkHandler;
import com.nhb.common.Loggable;

public class HandlerWorker implements WorkHandler<SolrParamsEvent>, Loggable {
	private BaseAdrSearchHandler searchHandler;

	public HandlerWorker(BaseAdrSearchHandler searchHandler) {
		if (searchHandler == null) {
			throw new IllegalArgumentException("searchHandler can not be null");
		}
		this.searchHandler = searchHandler;
	}

	@Override
	public void onEvent(SolrParamsEvent evt) throws Exception {
		SolrQueryResponse rsp = null;
		try {
			rsp = searchHandler.execute(evt.getCore(), evt.getHandler(), evt.getParams());
		} catch (Exception e) {
			getLogger().error("", e);
		}
		evt.getCallBack().call(rsp);
	}

}
