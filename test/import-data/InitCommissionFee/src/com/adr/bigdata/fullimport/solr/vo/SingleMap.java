package com.adr.bigdata.fullimport.solr.vo;

import java.util.HashMap;

public class SingleMap extends HashMap<String, Object> {
	private static final long serialVersionUID = 4521466037889096217L;

	public SingleMap(Object value) {
		super();
		this.put("set", value);
	}
}
