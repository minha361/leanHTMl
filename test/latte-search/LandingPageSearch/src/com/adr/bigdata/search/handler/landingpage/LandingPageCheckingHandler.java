/**
 * 
 */
package com.adr.bigdata.search.handler.landingpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;

import com.adr.bigdata.search.landingpage.ITEM_STATUS;
import com.adr.bigdata.search.landingpage.LandingPageField;
import com.adr.bigdata.search.landingpage.LandingPageParams;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.common.Loggable;

/**
 * @author minhvv2
 *
 */
public class LandingPageCheckingHandler extends SearchHandler implements Loggable {
	private static final String LANDING_PAGE_FIELD = "landing_page_%s_order";
	private static final String LANDING_PAGEQUERY = "[0 TO *]";

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		SolrParams params = req.getParams();
		String[] productItemIds = params.getParams(LandingPageParams.PRODUCT_ITEM_ID);
		String landingPageField = String.format(LANDING_PAGE_FIELD, params.get(LandingPageParams.LANDING_PAGE_ID));

		SolrParamsBuilder queryParamsBuilder = new SolrParamsBuilder().keyword("*:*")
				.filter(LandingPageField.PRODUCT_ITEM_ID, productItemIds).filter(landingPageField, LANDING_PAGEQUERY)
				.collapse(LandingPageField.PRODUCT_ITEM_GROUP, LandingPageField.SELL_PRICE);

		SolrParams queryParams = queryParamsBuilder.getParams();
		getLogger().debug("params to get result: {}", queryParams);
		req.setParams(queryParams);

		super.handleRequest(req, rsp);

		try {
			writeCheckingResponse(req, rsp, productItemIds);
		} catch (IOException e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeCheckingResponse(SolrQueryRequest req, SolrQueryResponse rsp, String[] productItemIds) throws IOException {	
		Set<String> setfilteredProductItemId = getPIs(req, rsp);
		List<NamedList> result = new ArrayList<NamedList>();
		for (String id : productItemIds) {
			NamedList bean = new SimpleOrderedMap();
			bean.add("productitemid", id);

			if (setfilteredProductItemId.contains(id)) {
				bean.add("status", ITEM_STATUS.ON_PAGE);
			} else {
				bean.add("status", ITEM_STATUS.NOT_ON_PAGE);
			}
			result.add(bean);
		}

		rsp.add("result", result);
		rsp.getValues().remove("response");
	}

	private Set<String> getPIs(SolrQueryRequest req, SolrQueryResponse rsp) throws IOException {
		Object _response = rsp.getValues().get("response");
		if (_response == null) {
			return Collections.emptySet();
		}
		
		if (_response instanceof ResultContext) {
			Set<String> result = new HashSet<>();
			DocList docList = ((ResultContext) _response).docs;
			DocIterator it = docList.iterator();
			SolrIndexSearcher searcher = req.getSearcher();
			while (it.hasNext()) {
				int id = it.nextDoc();
				Document doc = searcher.doc(id);
				IndexableField field = doc.getField(LandingPageField.PRODUCT_ITEM_ID);
				if (field != null) {
					result.add(field.stringValue());
				}
			}
			return result;
		}
		
		if (_response instanceof SolrDocumentList) {
			Set<String> result = new HashSet<>();
			for (SolrDocument doc : ((SolrDocumentList) _response)) {
				Object fieldValue = doc.get(LandingPageField.PRODUCT_ITEM_ID);
				if (fieldValue != null) {
					result.add(fieldValue.toString());
				}
			}
			return result;
		}
		
		return Collections.emptySet();
	}
}
