package com.adr.bigdata.search.product.fe.logic;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.nhb.common.Loggable;
import com.nhb.eventdriven.Callable;

public interface Logic extends Loggable	 {
	void execute(SolrQueryRequest req, SolrQueryResponse rsp, Callable callBack) throws Exception;
	void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object ... others) throws Exception;
}
