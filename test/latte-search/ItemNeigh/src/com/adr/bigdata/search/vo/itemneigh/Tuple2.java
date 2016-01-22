package com.adr.bigdata.search.vo.itemneigh;

public class Tuple2<K, V> {
	public K _1;
	public V _2;

	public Tuple2(K _1, V _2) {
		super();
		this._1 = _1;
		this._2 = _2;
	}

	@Override
	public String toString() {
		return "Tuple2 [_1=" + _1 + ", _2=" + _2 + "]";
	}

}
