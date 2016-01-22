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
public class HotPromProcessor extends AbstracProcessor {
	public static final String FILTER_PROMO_TEMPLATE = "(is_promotion_mapping:true AND is_promotion:true AND start_time_discount:[ * TO %d] AND finish_time_discount:[%d TO *]) OR (is_not_apply_commision:true) ";

	@Override
	public void process(ModifiableSolrParams solrParams, CommonBean bean) {
		super.process(solrParams, bean);
		long deltaTime = System.currentTimeMillis() - NewArrvialProcessor.IS_NEW_TIME_WINDOWS;
		String query = String.format(FILTER_PROMO_TEMPLATE, deltaTime, deltaTime);
		solrParams.add(CommonParams.FQ, query);
	}
}
