/**
 * 
 */
package com.adr.bigdata.search.product.blog;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.blog.bean.CommonBean;
import com.adr.bigdata.search.product.blog.processor.AbstracProcessor;
import com.adr.bigdata.search.product.blog.processor.BestSellingProcessor;
import com.adr.bigdata.search.product.blog.processor.HotPromProcessor;
import com.adr.bigdata.search.product.blog.processor.NewArrvialProcessor;
import com.adr.bigdata.search.product.blog.util.BlogParams;

/**
 * @author minhvv2
 *
 */
public class BlogSearchHandler extends SearchHandler {

	private Map<String, AbstracProcessor> processors;

	@Override
	public void inform(SolrCore arg0) {
		super.inform(arg0);
		registerProcessor();
	}

	private void registerProcessor() {
		processors = new HashMap<String, AbstracProcessor>();
		processors.put(BlogParams.PROMOTION, new HotPromProcessor());
		processors.put(BlogParams.BEST_SELLING, new BestSellingProcessor());
		processors.put(BlogParams.NEW_ARRIVAL, new NewArrvialProcessor());

	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		ModifiableSolrParams solrParam = new ModifiableSolrParams();
		CommonBean bean = CommonBean.createFromSolrRequest(req);
		switch (bean.getHighlightType()) {
		case BlogParams.PROMOTION:
			processors.get(BlogParams.PROMOTION).process(solrParam, bean);
			break;
		case BlogParams.BEST_SELLING:
			processors.get(BlogParams.BEST_SELLING).process(solrParam, bean);
			break;
		case BlogParams.NEW_ARRIVAL:
			processors.get(BlogParams.NEW_ARRIVAL).process(solrParam, bean);
			break;
		default:
			break;
		}
		solrParam.add(req.getParams());
		req.setParams(solrParam);
		super.handleRequest(req, rsp);
	}

}