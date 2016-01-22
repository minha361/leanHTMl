package com.vinecom.solr.dih;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

public class AttributeTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		row.put("attr_" + row.get("AttributeId") + "_txt", row.get("Value"));
		
		try {
			row.put("attr_" + row.get("AttributeId") + "_d", Double.parseDouble((String) row.get("Value")));
		} catch (Exception e) {
			
		}
		
		row.remove("AttributeId");
		row.remove("Value");
		return row;
	}

}
