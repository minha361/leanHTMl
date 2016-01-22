package com.adr.bigdata.index.product;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardingProductDocIDTransformer extends Transformer {
	private Logger logger = LoggerFactory.getLogger(ShardingProductDocIDTransformer.class);
	
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		try {
			String docIdKey = context.getEntityAttribute("docIdKey");
			String onsiteField = context.getEntityAttribute("onsiteField");
			String originalDocIdField = context.getEntityAttribute("originalDocIdField");
			int onsiteValue = Integer.parseInt(context.getEntityAttribute("onsiteValue"));
			String onsite = row.get(onsiteField).equals(onsiteValue) ? "XXXX" : "YYYY";
			Object originalDocId = row.get(originalDocIdField);
			row.put(docIdKey, onsite + "!" + originalDocId);
		} catch (Exception e) {
			logger.warn("", e);
		}
		return row;
	}
	
}
