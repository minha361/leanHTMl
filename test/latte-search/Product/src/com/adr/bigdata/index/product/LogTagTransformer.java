package com.adr.bigdata.index.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTagTransformer extends Transformer {
	protected Logger log = LoggerFactory.getLogger(getClass());
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		String keyword = row.get("keyword").toString();
		
		int logValue = Integer.valueOf(row.get("count").toString());
		List<String> keyStrings = new ArrayList<String>();
		for(int i= 0; i < logValue; i++){
			keyStrings.add(keyword);
		}
		row.put("log_tags", keyStrings);
		
		row.remove("keyword");
		row.remove("count");
		row.remove("product_item_id");
		return row;
	}
	
	
}
