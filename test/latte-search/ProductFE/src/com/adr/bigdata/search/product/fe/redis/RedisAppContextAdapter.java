/**
 * 
 */
package com.adr.bigdata.search.product.fe.redis;

import java.util.Map;

import org.apache.solr.util.ConcurrentLFUCache;
import org.apache.solr.util.ConcurrentLFUCache.EvictionListener;

import com.adr.bigdata.search.product.fe.model.RdCacheModel;
import com.adr.bigdata.search.product.fe.model.RdSearchFilterModel;
import com.adr.bigdata.search.product.fe.model.RdSuggestModel;
import com.nhb.common.Loggable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author minhvv2
 *
 */
public class RedisAppContextAdapter implements Loggable {

	private static RedisAppContextAdapter instance;
	private JedisPool pool;
	private int timeToLive;
	private ConcurrentLFUCache<String, Boolean> cache;
	private Map<String, Integer> cacheSizeContainers;

	private RedisAppContextAdapter(int timeToLive, String redisHost, int redisPort) {
		this.timeToLive = timeToLive;

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(256);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setBlockWhenExhausted(true);
		pool = new JedisPool(poolConfig, redisHost, redisPort);
	}

	public static RedisAppContextAdapter getInstance(RdAppConfig conf) {
		if (instance == null) {
			instance = new RedisAppContextAdapter(conf.getTimeToLive(), conf.getRdHost(), conf.getRdPort());
			//we don't use SOLR LRU cache when Sysadmins sets initSize=-1
			//meaning at that time Redis LRU cache were used instead
			if (instance.cache == null & conf.getInitialSize() > 0) {
				EvictionListener<String, Boolean> listener = new EvictionListener<String, Boolean>() {

					@Override
					public void evictedEntry(String key, Boolean value) {
						try (Jedis jedis = instance.pool.getResource()) {
							jedis.select(1);
							jedis.del(key);
						}
					}
				};
				instance.cache = new ConcurrentLFUCache<String, Boolean>(conf.getUpperWaterMark(),
						conf.getLowerWaterMark(), conf.getAcceptableSize(), conf.getInitialSize(), true, true, listener,
						false);
				instance.cache.setAlive(true);
			}
			instance.cacheSizeContainers = conf.getMaxCacheSizes();
			instance.addShutDownHook();
		}

		return instance;

	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			{
				this.setPriority(MAX_PRIORITY);
			}

			@Override
			public void run() {
				if (instance != null) {
					instance.pool.destroy();
					if (instance.cache != null) {
						instance.cache.destroy();
					}
				}
			}
		});

	}

	public RdCacheModel getRdCacheModel() {
		return new RdCacheModel(timeToLive, pool);
	}

	public RdSearchFilterModel getSearchFilterModel() {
		return new RdSearchFilterModel(cache, pool, cacheSizeContainers.get(RdConstant.RD_SEARCH_CACHE_SIZE),
				timeToLive);
	}

	public RdSuggestModel getSuggestModel() {
		RdSuggestModel instance = new RdSuggestModel(pool);
		instance.setMaxCacheSize(cacheSizeContainers.get(RdConstant.RD_SUGG_CACHE_SIZE));
		return instance;
	}
}
