package com.adr.bigdata.search.product.qparser;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import com.nhb.common.Loggable;

public class VinCollapseParserPlugin extends QParserPlugin implements Loggable {
	public static final String FINAL_SCORE_TEMPLATE = "sum(product(boost_score,1000000),def(%s,-10000000))";
	
	public static final String PRODUCT_ITEM_GROUP = "product_item_group";

	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList arg0) {		
	}

	@Override
	public QParser createParser(String qStr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		getLogger().debug("localParams: {}", localParams);
		getLogger().debug("params: {}", params);
		String city = params.get(VinQueryParserPlugin.P_CITY, "0");
		getLogger().debug("city: {}", city);
		String boostCityField = "city_" + city + "_score";
		String finalScore = String.format(FINAL_SCORE_TEMPLATE, boostCityField);
		ModifiableSolrParams modLocalParams = new ModifiableSolrParams(localParams);
		modLocalParams.add("field", PRODUCT_ITEM_GROUP);
		modLocalParams.add("max", finalScore);
		return req.getCore().getQueryPlugin("collapse").createParser(qStr, modLocalParams, params, req);
	}

}
