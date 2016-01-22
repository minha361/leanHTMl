package com.adr.bigdata.search.handler.collectiongroup;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;
import com.nhb.common.Loggable;

public class CollectionGroupRequestHandler extends SearchHandler implements Loggable  {
	private static final String COLLECTION_GROUP_ORDER = "collection_group_%s_order";
	
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String collectionGroupId = req.getParams().get(COLLECTION_GROUP_ID);
		String offset = req.getParams().get("offset");
		String limit = req.getParams().get("limit");
		String[] merchantIds = req.getParams().getParams("mcid");
		ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());
		buildCollectionGroupFilter(collectionGroupId, params);
//		String offset = param.get("offset", "0");
//		String limit = param.get("limit", "24");
		
		params.add(CommonParams.ROWS, limit);
		params.add(CommonParams.START, offset);
		
		SolrParamsBuilder builder = new SolrParamsBuilder(params);

		builder
		.filter(ProductFields.MERCHANT_ID, merchantIds);
		
		req.setParams(params);
		
		super.handleRequest(req, rsp);
	}
	
	private void buildCollectionGroupFilter(String collectionId, ModifiableSolrParams solrParams) {
		assert (!Strings.isNullOrEmpty(collectionId));
		String collectionGroupId = String.format(COLLECTION_GROUP_ORDER, collectionId);
		solrParams.add(CommonParams.FQ, collectionGroupId + ":*");
		
		
		solrParams.add(CommonParams.SORT, collectionGroupId + " asc");
//		if (Strings.isNullOrEmpty(groupId)) {
//			solrParams.add(CommonParams.SORT, landingOrderField + " asc");
//		} else {
//			String groupOrderField = String.format(LANDING_PAGE_GROUP_ORDER, groupId);
//			solrParams.add(CommonParams.FQ, groupOrderField + ":*");
//			solrParams.add(CommonParams.SORT, groupOrderField + " asc");
//		}
	}
	
	public static final String COLLECTION_GROUP_ID = "section";
	
}
