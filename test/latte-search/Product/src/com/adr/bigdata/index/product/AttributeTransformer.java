package com.adr.bigdata.index.product;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeTransformer extends Transformer {
	Logger log = LoggerFactory.getLogger(getClass());
	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		row.put("attr_" + row.get("AttributeId") + "_txt", row.get("Value"));
		row.put("attr_" + row.get("AttributeId") + "_i", row.get("AttributeValueId"));
		row.put("attr_" + row.get("AttributeId") + "_i_facet", row.get("AttributeValueId"));
		row.put("attr_name_" + row.get("AttributeId") + "_txt", row.get("AttributeName"));

		try {
			row.put("attr_" + row.get("AttributeId") + "_d", Double.parseDouble(row.get("Value").toString()));
		} catch (Exception e) {
			if (!(e instanceof NumberFormatException)) {
				log.warn("", e);
			}
		}
		
		row.remove("Value");
		row.remove("AttributeValueId");
		row.remove("AttributeId");
		row.remove("AttributeName");
		return row;
	}

}
