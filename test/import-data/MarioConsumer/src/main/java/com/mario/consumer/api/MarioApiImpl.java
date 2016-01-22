package com.mario.consumer.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.hazelcast.core.HazelcastInstance;
import com.mario.consumer.entity.LifeCycle;
import com.mario.consumer.schedule.Scheduler;
import com.mario.consumer.solr.SolrClientManager;
import com.nhb.common.db.sql.DBIAdapter;
import com.nhb.common.db.sql.SQLDataSourceManager;

import redis.clients.jedis.JedisPool;

class MarioApiImpl implements MarioApi {

	private SQLDataSourceManager dataSourceManager;
	private Scheduler scheduler;
	private HazelcastInstance hazelcast;
	private SolrClientManager solrClientManager;

	MarioApiImpl(SQLDataSourceManager dataSourceManager, Scheduler scheduler, HazelcastInstance hazelcast,
			SolrClientManager solrClientManager) {
		this.dataSourceManager = dataSourceManager;
		this.scheduler = scheduler;
		this.hazelcast = hazelcast;
		this.solrClientManager = solrClientManager;
	}

	@Override
	public DBIAdapter getDatabaseAdapter(String dataSourceName) {
		DBIAdapter result = new DBIAdapter(dataSourceManager, dataSourceName);
		return result;
	}

	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	@Override
	public <K, V> Map<K, V> getCacheMap(String name) {
		if (this.hazelcast == null) {
			return null;
		}
		return this.hazelcast.getMap(name);
	}

	@Override
	public <E> List<E> getCacheList(String name) {
		if (this.hazelcast == null) {
			return null;
		}
		return this.hazelcast.getList(name);
	}

	@Override
	public Lock getLock(String name) {
		if (this.hazelcast == null) {
			return null;
		}
		return this.hazelcast.getLock(name);
	}

	@Override
	public long getCacheLong(String name) {
		if (this.hazelcast == null) {
			return 0;
		}
		return this.hazelcast.getAtomicLong(name).get();
	}

	@Override
	public void setCacheLong(String name, long value) {
		if (this.hazelcast == null) {
			return;
		}
		this.hazelcast.getAtomicLong(name).set(value);
	}

	@Override
	public LifeCycle getLifeCycle(String name) {
		// TODO return LifeCycle by name
		return null;
	}

	@Override
	public HazelcastInstance getHazelcastInstance() {
		return this.hazelcast;
	}

	@Override
	public ConcurrentUpdateSolrClient getSolrClient(String name) {
		return this.solrClientManager.getSolrClient(name);
	}

	@Override
	public HttpSolrClient getSolrQueryClient(String name) {
		return this.solrClientManager.getSolrQueryClient(name);
	}

	@Override
	public JedisPool getJedisPool(String name) {
		// TODO Nho phai code ham nay
		return null;
	}

	@Override
	public JedisPool getOuputJedisPool(String name) {
		// TODO Implement this
		return null;
	}
}
