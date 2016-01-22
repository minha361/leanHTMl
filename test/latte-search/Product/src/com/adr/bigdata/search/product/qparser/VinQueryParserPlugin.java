package com.adr.bigdata.search.product.qparser;

import java.text.MessageFormat;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import com.nhb.common.Loggable;

public class VinQueryParserPlugin extends QParserPlugin implements Loggable {
	public static final String P_CITY = "cityid";
	public static final String P_DISTRICT = "districtid";
	
	public static final String TEMPlATE = "(served_province_ids:{0} OR served_province_ids:0) "
			+ "AND (served_district_ids:{1} OR served_district_ids:{0}_0 OR served_district_ids:0) "
			+ "AND received_city_id:{0} "
			+ "AND city_{0}_score:[0 TO *]";

	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList arg0) {
		
	}

	@Override
	public QParser createParser(String qStr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		getLogger().debug("localParams: {}", localParams);
		getLogger().debug("params: {}", params);
		int city = params.getInt(P_CITY, 0);
		int district = params.getInt(P_DISTRICT, 0);
		getLogger().debug("city: {}, district: {}", city, district);
		String queryString = MessageFormat.format(TEMPlATE, city, district);				
		return req.getCore().getQueryPlugin("lucene").createParser(queryString, localParams, params, req);
	}
}
