package com.mario.consumer.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.hazelcast.core.HazelcastInstance;
import com.mario.consumer.entity.LifeCycle;
import com.mario.consumer.schedule.Scheduler;
import com.nhb.common.db.sql.DBIAdapter;
import com.rabbitmq.client.Channel;

import redis.clients.jedis.JedisPool;

public interface MarioApi {

	DBIAdapter getDatabaseAdapter(String dataSourceName);

	Scheduler getScheduler();

	<K, V> Map<K, V> getCacheMap(String name);

	<E> List<E> getCacheList(String name);

	HazelcastInstance getHazelcastInstance();

	Lock getLock(String name);

	long getCacheLong(String name);

	void setCacheLong(String name, long value);

	LifeCycle getLifeCycle(String name);

	ConcurrentUpdateSolrClient getSolrClient(String name);

	HttpSolrClient getSolrQueryClient(String name);

	JedisPool getJedisPool(String name);

	JedisPool getOuputJedisPool(String name);	
}
