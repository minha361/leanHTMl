package com.adr.bigdata.index.product;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import com.adr.bigdata.search.index.SingleMap;

public class DescriptionTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {		
		String fullDescription = (String) row.get("FullDescription");
		String shortDescription = (String) row.get("ShortDescription");
		
		if(fullDescription != null) {							
			row.put("full_description", new SingleMap("set", this.htmlTrim(fullDescription)));
		}
		
		if(shortDescription != null) {
			row.put("short_description", new SingleMap("set", this.htmlTrim(shortDescription)));			
		}				
		return row;
	}
	
	public String htmlTrim(String inputString) {
		if(!inputString.trim().isEmpty()) {
			return HtmlUtil.trimHtml(inputString.trim());
		}
		return "";
	}
}
