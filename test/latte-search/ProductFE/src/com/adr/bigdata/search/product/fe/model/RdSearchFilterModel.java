/**
 * 
 */
package com.adr.bigdata.search.product.fe.model;

import org.apache.solr.util.ConcurrentLFUCache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author minhvv2
 *
 */
public class RdSearchFilterModel {

	private int timeToLive = 600;// in seconds

	private final ConcurrentLFUCache<String, Boolean> cache;

	private JedisPool pool;
	private int maxCacheSize = 500000;

	public RdSearchFilterModel(ConcurrentLFUCache<String, Boolean> cache, JedisPool pool, int maxCacheSize,
			int timeToLive) {
		this.cache = cache;
		this.pool = pool;
		this.maxCacheSize = maxCacheSize;
		this.timeToLive = timeToLive;
	}

	public boolean isCached(String key) {
		return cache.get(key);
	}

	public String _get(String key) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(1);
			return jedis.get(key);
		}
	}

	@Deprecated
	public String get(String key) {
		Boolean cachedVal = cache.get(key);
		if (cachedVal != null) {
			// get cache from redis
			try (Jedis jedis = pool.getResource()) {
				jedis.select(1);
				String val = jedis.get(key);
				if (val != null) {
					return val;
				} else {
					cache.remove(key);
				}
			}
		}
		return null;
	}

	public void _put(String key, String val) {
		try (Jedis jedis = pool.getResource()) {
			jedis.select(1);
			if (jedis.dbSize() > maxCacheSize) {
				return;
			}
			jedis.setex(key, timeToLive, val);
		}
	}

	@Deprecated
	public void put(String key, String val) {
		Boolean cachedVal = cache.get(key);
		if (cachedVal == null) {
			try (Jedis jedis = pool.getResource()) {
				jedis.select(1);
				jedis.setex(key, timeToLive, val);
				cache.put(key, true);
			}
		}
	}
}
