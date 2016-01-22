package com.adr.bigdata.search.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class WSTools {
	public static String getContent(String _url, int timeout) throws IOException {
		URL url = new URL(_url);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	public static JSONArray getJSONArrayContent(String _url, int timeout) throws IOException {
		URL url = new URL(_url);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		return (JSONArray) JSONValue.parse(conn.getInputStream());
	}
	
	public static JSONObject getJSONObjectContent(String _url, int timeout) throws IOException {
		URL url = new URL(_url);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		return (JSONObject) JSONValue.parse(conn.getInputStream());
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(getJSONArrayContent("http://10.220.83.24:8080/items4UserV2?itemId=65522444&contextId=1&userId=\"123232\"", 100));
	}
}
