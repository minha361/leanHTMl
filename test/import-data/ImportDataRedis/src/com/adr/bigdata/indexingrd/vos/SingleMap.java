package com.adr.bigdata.indexingrd.vos;

import java.util.HashMap;

public class SingleMap extends HashMap<String, Object> {
	private static final long serialVersionUID = -49607320071054541L;

	public SingleMap(String key, Object value) {
		super();
		this.put(key, value);
	}
}
