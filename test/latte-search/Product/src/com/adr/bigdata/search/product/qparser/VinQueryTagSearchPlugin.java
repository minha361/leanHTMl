package com.adr.bigdata.search.product.qparser;


import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import com.nhb.common.Loggable;

public class VinQueryTagSearchPlugin extends QParserPlugin implements Loggable {	
	
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
		double tagBoost = localParams.getDouble("tagBoost", _tagBoost);
		tagBoost = params.getDouble("tagBoost", tagBoost);
		String queryString = "\"" + qStr + "\"^" + tagBoost; //^
		return req.getCore().getQueryPlugin("lucene").createParser(queryString, localParams, params, req);
	}
}