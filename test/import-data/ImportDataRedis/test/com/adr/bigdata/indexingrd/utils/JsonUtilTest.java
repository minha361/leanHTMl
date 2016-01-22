package com.adr.bigdata.indexingrd.utils;

import java.util.ArrayList;

import net.minidev.json.JSONObject;

public class JsonUtilTest {
	public static void main(String[] agrs){
		ArrayList<String> xxx = new ArrayList<String>();
		xxx.add("hello");
		xxx.add("bac");
		
		JSONObject obj = new JSONObject();
        obj.put("name", "foo");
        obj.put("num", 100);
        obj.put("balance", 1000.21);
        obj.put("is_vip", true);
        obj.put("nickname",null);
		System.out.println(JsonUtil.objectToJson(obj));
		
		JSONObject jsonObject = JsonUtil.fromString(JsonUtil.objectToJson(obj));
		System.out.println(Double.valueOf((String) jsonObject.getAsString("balance")));
	}
	

}
