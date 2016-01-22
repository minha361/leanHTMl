package com.adr.bigdata.search.product.qparser;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import com.nhb.common.Loggable;

public class VinLogTagQParserPlugin extends QParserPlugin implements Loggable {

	private int _tagBoost = 50;
	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList args) {
		if(args.get("tagBoost") != null) {
			this._tagBoost = (int) args.get("tagBoost");
		}	
	}

	@Override
	public QParser createParser(String qStr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		qStr = getInternalTag(qStr);
		double tagBoost = localParams.getDouble("tagBoost", _tagBoost);
		tagBoost = params.getDouble("tagBoost", tagBoost);
		String queryString = "\"" + qStr + "\"^" + tagBoost; //^
		return req.getCore().getQueryPlugin("lucene").createParser(queryString, localParams, params, req);
//		qStr = getInternalTag(qStr);
//		String queryString = "{!boost b=termfreq(log_tags,'" + qStr + "')}" + qStr;
//		
////		String queryString = "{!boost b=termfreq(log_tags,'" + qStr + "')}\"" + qStr + "\"";
//		return req.getCore().getQueryPlugin("lucene").createParser(queryString, localParams, params, req);
	}

	private static String getInternalTag(String searchKeyword) {
		if (searchKeyword != null) {
			searchKeyword = searchKeyword.replaceAll("\\+", " ");
			searchKeyword = searchKeyword.replaceAll("-", " ");
			searchKeyword = searchKeyword.replaceAll("/", " ");
			searchKeyword = searchKeyword.toLowerCase();
			searchKeyword = searchKeyword.trim();
			while (searchKeyword.contains("  ")) {
				searchKeyword = searchKeyword.replace("  ", " ");
			}
			searchKeyword = unAccent(searchKeyword);
		}
		return searchKeyword;
	}

	private static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
	}

}
