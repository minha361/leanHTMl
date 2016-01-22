package com.adr.bigdata.search.product.utils;

import org.apache.solr.common.util.SimpleOrderedMap;

public class SimpleOrderedMapUtils {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SimpleOrderedMap create(Object ... args) {
		if (args == null || args.length == 0 || ((args.length & 1) == 1)) {
			return null;
		}
		
		SimpleOrderedMap som = new SimpleOrderedMap();
		int sz = args.length >> 1;
		for (int i = 0; i < sz; i++) {
			int iKey = i << 1;
			String key = (String) args[iKey];
			Object value = args[iKey | 1];
			som.add(key, value);
		}
		return som;
	}
}
