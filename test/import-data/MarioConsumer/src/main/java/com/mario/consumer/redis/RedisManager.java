package com.mario.consumer.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
	private Map<String, JedisPool> name2JedisPool = new HashMap<>();
	private Map<String, RedisConfig> name2Config = new HashMap<>();
	
	public void register(RedisConfig config) {
		name2Config.put(config.getName(), config);
	}
	
	public JedisPool getJedisPool(String name) {
		if (!name2JedisPool.containsKey(name)) {
			if (name2Config.containsKey(name)) {
				RedisConfig config = name2Config.get(name);
				JedisPoolConfig poolConfig = new JedisPoolConfig();
				poolConfig.setMaxTotal(256);
				poolConfig.setTestOnBorrow(true);
				poolConfig.setTestOnReturn(true);
				poolConfig.setBlockWhenExhausted(true);
				JedisPool pool = new JedisPool(poolConfig, config.getHost(), config.getPort());
				name2JedisPool.put(name, pool);
			} else {
				return null;
			}
		}
		return name2JedisPool.get(name);
	}
	
	public void close() {
		for (Entry<String, JedisPool> e : name2JedisPool.entrySet()) {
			try {
				e.getValue().destroy();
			} catch (Exception ex) {
				System.err.println("Error when destroy jedis pool: " + e.getKey());
				ex.printStackTrace();
			}
		}
	}
}
