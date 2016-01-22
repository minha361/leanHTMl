/**
 * 
 */
package com.adr.bigdata.search.product.fe.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;

import com.google.common.collect.Lists;

/**
 * @author minhvv2
 *
 */
public class SolrParamUtils {
	public static String transform(SolrParams solrParams) {
		StringBuilder sb = new StringBuilder();
		List<String> lstParamNames = Lists.newArrayList(solrParams.getParameterNamesIterator());
		Collections.sort(lstParamNames);
		for (String name : lstParamNames) {
			String[] vals = solrParams.getParams(name);
			Arrays.sort(vals);
			for (String val : vals) {
				sb.append(name).append("=").append(val.replace(":", "\\:").trim()).append("&");
			}
		}
		if (sb.length() == 0) {
			return "NONE";
		} else {
			return sb.deleteCharAt(sb.length() - 1).toString().toLowerCase();
		}
	}

	public static void main(String[] args) {
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.add("catid", "1");
		solrParams.add("catid", "2");
		solrParams.add("limit", "20");
		solrParams.add("catid", "3");
		SolrQueryRequest req = new SolrQueryRequestBase(null, solrParams) {
		};

		Iterator<String> it = req.getParams().getParameterNamesIterator();
		List<String> lstParamNames = Lists.newArrayList(it);
		Collections.sort(lstParamNames);

		System.out.println(transform(solrParams));

		ModifiableSolrParams solrParams2 = new ModifiableSolrParams();
		solrParams2.add("catid", "2");
		solrParams2.add("catid", "1");
		solrParams2.add("limit", "20");
		solrParams2.add("catid", "3");
		System.out.println(transform(solrParams2));

	}
}