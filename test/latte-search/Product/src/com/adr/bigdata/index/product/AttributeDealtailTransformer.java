/**
 * 
 */
package com.adr.bigdata.index.product;

import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

/**
 * @author ndn
 *
 */
public class AttributeDealtailTransformer extends Transformer {
	public static final String COLUMN_NAME = "AttributeDetail";

	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {
		if (row.containsKey(COLUMN_NAME)) {
			String jsonString = (String) row.get(COLUMN_NAME);
			try {
				JSONArray attributeDeatail = (JSONArray) JSONValue.parse(jsonString);
				for (Object o : attributeDeatail) {
					JSONObject attribute = (JSONObject) o;
					int attId = (int) attribute.get("AttributeId");
					String attValue = (String) attribute.get("Value");

					row.put("attr_" + attId + "_txt_ignore", attValue);
				}

				row.remove(COLUMN_NAME);
			} catch (java.lang.ClassCastException e) {
				System.err.println("can not cast string to json array: " + jsonString);
			}
		}

		return row;
	}
}
