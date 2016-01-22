package com.adr.bigdata.search.product.qparser;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import com.nhb.common.Loggable;

public class VinQueryDescriptionPlugin extends QParserPlugin implements Loggable{
	private int _boost = 1;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList args) {		
		if(args.get("boost") != null) {
			this._boost = (int) args.get("boost");
		}		
	}

	@Override
	public QParser createParser(String qStr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {			
		String queryString = qStr + "^" + this._boost; //^
		return req.getCore().getQueryPlugin("lucene").createParser(queryString, localParams, params, req);
	}
}
