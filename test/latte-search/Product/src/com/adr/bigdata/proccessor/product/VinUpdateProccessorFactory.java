package com.adr.bigdata.proccessor.product;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;

public class VinUpdateProccessorFactory extends UpdateRequestProcessorFactory {
	private SolrParams params;
	private final Map<Object, Object> sharedObjectCache = new HashMap<Object, Object>();

	@Override
	public void init(@SuppressWarnings("rawtypes") final NamedList args) {
		if (args != null) {
			this.params = SolrParams.toSolrParams(args);
		}
	}

	@Override
	public UpdateRequestProcessor getInstance(SolrQueryRequest request, SolrQueryResponse response,
			UpdateRequestProcessor nextProcessor) {

		return new VinUpdateProcessor(this.params, request, response, nextProcessor, this.sharedObjectCache);
	}
}
