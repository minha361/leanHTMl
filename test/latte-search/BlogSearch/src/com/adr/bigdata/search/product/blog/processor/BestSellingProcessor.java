/**
 * 
 */
package com.adr.bigdata.search.product.blog.processor;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.adr.bigdata.search.product.blog.bean.CommonBean;
import com.adr.bigdata.search.product.blog.util.ProductSchema;

/**
 * @author minhvv2
 *
 */
public class BestSellingProcessor extends AbstracProcessor {
	@Override
	public void process(ModifiableSolrParams solrParams, CommonBean bean) {
		super.process(solrParams, bean);
		solrParams.set(CommonParams.SORT, ProductSchema.VIEWED_DAY + " desc");
	}
}
