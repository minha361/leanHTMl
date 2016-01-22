/**
 * 
 */
package com.adr.bigdata.search.product.blog.processor;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.adr.bigdata.search.product.blog.bean.CommonBean;

/**
 * @author minhvv2
 *
 */
public class NewArrvialProcessor extends AbstracProcessor {
	static final long IS_NEW_TIME_WINDOWS = 604800000;// 7 days
	public static final String FILTER_NEW_TEMPLATE = "create_time:[ %d TO *]";

	@Override
	public void process(ModifiableSolrParams solrParams, CommonBean bean) {
		super.process(solrParams, bean);
		long deltaTime = System.currentTimeMillis() - IS_NEW_TIME_WINDOWS;
		String query = String.format(FILTER_NEW_TEMPLATE, deltaTime);
		solrParams.add(CommonParams.FQ, query);
	}
}
