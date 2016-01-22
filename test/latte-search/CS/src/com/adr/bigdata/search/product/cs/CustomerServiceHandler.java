package com.adr.bigdata.search.product.cs;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.handler.CommonSearchHandler;
import com.adr.bigdata.search.product.ProductFields;
import com.google.common.base.Strings;

public class CustomerServiceHandler extends CommonSearchHandler {
	private final static String CS_PATTERN_ID = "^(id:)(.+)$";
	private final static String CS_PATTERN_SKU = "^(sku:)(.+)$";
	private final static String CS_PATTERN_BARCODE = "^(bar:)(.+)$";
	
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String keyword = req.getParams().get("keyword");
		ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());
		if (isPatternQuery(keyword)) {
			params.add("q", "*:*");
			builFilterFromKeyword(keyword, params);
		} else {
			params.add("defType", "edismax");
			params.add("qf", ProductFields.PRODUCT_ITEM_NAME);
			params.add("q", keyword);
		}
		
		req.setParams(params);
		
		super.handleRequest(req, rsp);
	}
	
	private boolean isPatternQuery(String rawKeyword) {
		return (!Strings.isNullOrEmpty(rawKeyword))
				&& (rawKeyword.matches(CS_PATTERN_BARCODE) || rawKeyword.matches(CS_PATTERN_SKU) || rawKeyword
						.matches(CS_PATTERN_ID));
	}
	
	private void builFilterFromKeyword(String rawKeyword, ModifiableSolrParams params) {
		rawKeyword = rawKeyword.trim();
		if (rawKeyword.matches(CS_PATTERN_BARCODE)) {
			String barcode = rawKeyword.replaceAll(CS_PATTERN_BARCODE, "$2");
			params.add("fq", ProductFields.BARCODE + ":" + barcode);
		} else if (rawKeyword.matches(CS_PATTERN_SKU)) {
			String sku = rawKeyword.replaceAll(CS_PATTERN_SKU, "$2");
			params.add("fq", ProductFields.MERCHANT_PRODUCT_ITEM_SKU + ":" + sku);
		} else if (rawKeyword.matches(CS_PATTERN_ID)) {
			String id = rawKeyword.replaceAll(CS_PATTERN_ID, "$2");
			String fq = ProductFields.PRODUCT_ID + ":" + id + " OR " + ProductFields.PRODUCT_ITEM_ID + ":" + id;
			params.add("fq", fq);
		}
	}
}
