package com.adr.extractlog.util;

import java.io.IOException;
import java.util.List;


public class JoinStringUtil {
	public static String joinStrings(List<String> strings, String conjunction) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : strings) {
			if (first) {
				first = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
	public static String joinLongs(List<Long> longs, String conjunction) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Long item : longs) {
			if (first) {
				first = false;
			} else {
				sb.append(conjunction);
			}
			sb.append(item);
		}
		return sb.toString();
	}
}
