/**
 * 
 */
package com.adr.bigdata.search.product.fe.model;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author minhvv2
 *
 */
public class RdSuggestModel {
	private int timeToLive = 3600;// in seconds
	private JedisPool pool;
	private int maxCacheSize = 500000;
	private int dbNo = 2; // will make this configurable later

	public RdSuggestModel(JedisPool pool) {
		super();
		this.pool = pool;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

	public int getDbNo() {
		return dbNo;
	}

	public void setDbNo(int dbNo) {
		this.dbNo = dbNo;
	}

	public String get(String key) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(dbNo);
			return jedis.get(key);
		}
	}

	public void put(String key, String val) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(dbNo);
			if (jedis.dbSize() > maxCacheSize) {
				return;
			}
			jedis.setex(key, timeToLive, val);
		}
	}

}
