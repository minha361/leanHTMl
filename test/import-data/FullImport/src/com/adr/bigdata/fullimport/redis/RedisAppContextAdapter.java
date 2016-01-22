/**
 * 
 */
package com.adr.bigdata.fullimport.redis;

import com.adr.bigdata.fullimport.Loggable;
import com.adr.bigdata.fullimport.redis.model.RdCacheModel;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author minhvv2
 *
 */
public class RedisAppContextAdapter implements Loggable {

	private static RedisAppContextAdapter instance;
	private JedisPool pool;

	private RedisAppContextAdapter(String redisHost, int redisPort) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(256);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setBlockWhenExhausted(true);
		pool = new JedisPool(poolConfig, redisHost, redisPort);
	}

	public static RedisAppContextAdapter getInstance(RdAppConfig conf) {
		if (instance == null) {
			instance = new RedisAppContextAdapter(conf.getRdHost(), conf.getRdPort());
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
				}
			}
		});

	}

	public <T extends RdCacheModel> T getRdCacheModel(Class<T> clazz, int db) throws InstantiationException, IllegalAccessException {
		T instance = clazz.newInstance();
		instance.setDb(db);
		instance.setPool(pool);
		return instance;
	}
}
