package com.nhb.common.data;

public class KeyValueTuple extends MapTuple<String, Object> {

	private static final long serialVersionUID = 5789620044654361064L;

	public KeyValueTuple() {
		super();
	}

	public KeyValueTuple(Object... keyValues) {
		super(keyValues);
	}
}
