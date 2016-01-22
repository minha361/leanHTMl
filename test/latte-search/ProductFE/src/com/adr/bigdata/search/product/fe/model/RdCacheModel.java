/**
 * 
 */
package com.adr.bigdata.search.product.fe.model;

import com.nhb.common.Loggable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author minhvv2 Redis cache model
 */
public class RdCacheModel implements Loggable {

	private int timeToLive;
	private JedisPool pool;

	public RdCacheModel(int timeToLive, JedisPool pool) {
		super();
		this.timeToLive = timeToLive;
		this.pool = pool;
	}

	public String get(String key) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(0);
			return jedis.get(key);
		}
	}

	public void put(String key, String val) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(0);
			jedis.setex(key, timeToLive, val);
		}
	}

	public long dbSize() {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(0);
			return jedis.dbSize();
		}
	}
}