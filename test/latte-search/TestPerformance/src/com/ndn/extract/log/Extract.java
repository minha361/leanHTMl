package com.ndn.extract.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class Extract {
	public static void main(String[] args) throws IOException {
		String sourceFile = args[0];
		String destFile = args[1];

		long now = System.currentTimeMillis();
		String mark = "trying to parse json string: ";
		int i = 1;
		try (PrintWriter fw = new PrintWriter(destFile, "UTF-8")) {
			for (int count = 0; count < 10; count ++) {
				try (BufferedReader br = new BufferedReader(new FileReader(sourceFile), 10000)) {
					String line = null;			
					while ((line = br.readLine()) != null) {
						int index = line.indexOf(mark);
						index += mark.length();
						String jsonString = line.substring(index);
						JSONArray arr = (JSONArray) JSONValue.parse(jsonString);
						Object _info = arr.get(1);
						if (_info instanceof JSONArray) {
							JSONArray info = (JSONArray) _info;
							for (Object o : info) {
								JSONObject _o = (JSONObject) o;
								_o.replace("updateTime", now + i);
							}
						} else if (_info instanceof JSONObject) {
							JSONObject info = (JSONObject) _info;
							info.replace("updateTime", now + i);
						}
						fw.write(arr.toJSONString());
						fw.write("\n");
						i++;
					}
				}
			}
		}
		System.out.println("num: " + i);
	}
}
