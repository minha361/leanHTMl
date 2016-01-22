package com.ndn.expression.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Luật hiện tại sẽ support lấy các giá trị thông tin từ các sản phẩm thỏa mãn điều kiện
 * Các biến sẽ bắt đầu bằng kí tự $, tên biến thì có quy tắc đặt như các ngôn ngữ lập trình
 * @author noind
 *
 */
public class ResultTools {
	private static boolean[] acceptChars = new boolean[256];
	
	static {
		for (char c = 'a'; c <= 'z'; c++) {
			acceptChars[c] = true;
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			acceptChars[c] = true;
		}
		for (char c = '0'; c <= '0'; c++) {
			acceptChars[c] = true;
		}
		acceptChars['_'] = true;
	}
	
	public static String mapName(String criteria, Map<String, Object> data) {
		String result = criteria;
		Collection<String> variables = getVariables(criteria);
		for (String var: variables) {
			if (data.containsKey(var.substring(1))) {
				result = result.replaceAll('\\' + var, data.get(var.substring(1)).toString());
			}
		}
		return result;
	}
	
	private static Collection<String> getVariables(String criteria) {
		Collection<String> variables = new ArrayList<String>();
		int start = -1;
		for (int i = 0; i < criteria.length(); i++) {
			if (start != -1) {
				char c = criteria.charAt(i);
				if (c < 0 || c > 255 || !acceptChars[c]) {
					variables.add(criteria.substring(start, i));
					start = -1;
				}
			} else {
				if (criteria.charAt(i) == '$') {
					start = i;					
				}
			}			
		}
		if (start != -1) {
			variables.add(criteria.substring(start));
		}

		return variables;
	}
	
	public static void main(String[] args) {
		String criteria  = "$a:abc AND $b:cde ô";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("$a", "hkl"); map.put("$b", "456");
		System.out.println(mapName(criteria, map));
	}
}
