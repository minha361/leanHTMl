/**
 * 
 */
package com.adr.bigdata.index.product;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @author ndn
 *
 */
public class ScoreTransformer extends Transformer {
	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {

		try {
			String s = (String) row.get("Score");
			JSONObject obj = (JSONObject) JSONValue.parse(s);
			for (Entry<String, Object> e : obj.entrySet()) {
				row.put("city_" + e.getKey() + "_score", e.getValue());
			}
			row.remove("Score");
		} catch (Exception e) {
			System.err.println("error " + e.getMessage() + ", value: " + row.get("Score"));
		}
		return row;
	}

}
