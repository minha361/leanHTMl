package com.adr.bigdata.indexingrd.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonSerializer;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;

public class JsonUtil {
	@SuppressWarnings("unchecked")
	public static String collectionToJsonString(Collection<?> obj) {
	    if (obj instanceof Map) {
	        return JSONObject.toJSONString((Map<String, ?>) obj, JSONStyle.LT_COMPRESS);
	    } else if (obj instanceof List) {
	        return JSONArray.toJSONString((List<?>) obj, JSONStyle.LT_COMPRESS);
	    } else {
	        throw new UnsupportedOperationException(obj.getClass().getName() + " can not be converted to JSON");
	    }
	}
	
	public static String objectToJson(JSONObject  obj) {
		String com_json = JSONValue.toJSONString(obj, JSONStyle.MAX_COMPRESS);
		return com_json;
	}
	
	public static JSONObject fromString(String jsonString){
		Object object = JSONValue.parse(jsonString);
		if(object instanceof JSONObject){
			return (JSONObject) object;
		} else {
			 throw new UnsupportedOperationException("can not be converted to JSONObject: " + jsonString);
		}
	}
}
