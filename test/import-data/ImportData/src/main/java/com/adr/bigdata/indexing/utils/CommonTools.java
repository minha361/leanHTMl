package com.adr.bigdata.indexing.utils;

import java.util.ArrayList;
import java.util.List;

public class CommonTools {
	public static List<Long> getLongsFromIntegers(List<Integer> list) {
		List<Long> result = new ArrayList<Long>();
		for (Integer i : list) {
			result.add((long) i);
		}
		return result;
	}
	
	public static List<Integer> getListIntFromString(String s, String d) {
		String[] ss = s.split(d);
		List<Integer> list = new ArrayList<Integer>();
		for (String _s : ss) {
			list.add(Integer.parseInt(_s));
		}
		return list;
	}
}
