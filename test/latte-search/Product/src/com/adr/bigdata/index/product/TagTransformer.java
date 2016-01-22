package com.adr.bigdata.index.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import com.adr.bigdata.search.index.SingleMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class TagTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {
		List<String> tags = new ArrayList<String>();
		String s = (String) row.get("TagJson");
		if (s != null && !s.trim().isEmpty()) {
			try {
				JSONArray arr = (JSONArray) JSONValue.parse(s);
				for (Object o : arr) {
					JSONObject obj = (JSONObject) o;
					String tag = (String) obj.get("Name");
					tags.add(tag);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		row.put("tags", new SingleMap("set", tags));
		return row;
	}

}
