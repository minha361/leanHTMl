package com.adr.bigdata.search.model;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;

public abstract class CachedModel implements Model {
	private HazelcastInstance hazelcast;
	
	protected final <K, V> IMap<K, V> getCachedMap(String name) {
		return hazelcast.getMap(name);
	}
	
	protected final <E> IList<E> getCachedList(String name) {
		return hazelcast.getList(name);
	}
	
	protected final IAtomicLong getCachedLong(String name) {
		return hazelcast.getAtomicLong(name);
	}


	public final void setHazelcast(HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
	}
	
}
