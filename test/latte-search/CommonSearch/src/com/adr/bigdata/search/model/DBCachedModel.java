package com.adr.bigdata.search.model;

import com.adr.bigdata.search.db.DBIAdapter;
import com.adr.bigdata.search.db.dao.AbstractDAO;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;

public class DBCachedModel implements Model {
	private DBIAdapter dbiAdapter;
	private HazelcastInstance hazelcast;

	public <T extends AbstractDAO> T openDAO(Class<T> clazz) {
		return getDbiAdapter().openDAO(clazz);
	}

	public DBIAdapter getDbiAdapter() {
		return dbiAdapter;
	}

	public void setDbiAdapter(DBIAdapter dbiAdapter) {
		this.dbiAdapter = dbiAdapter;
	}

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
