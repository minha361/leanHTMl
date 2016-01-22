/**
 * 
 */
package com.adr.bigdata.fullimport.redis.model;

import java.io.IOException;
import java.util.Map;

import com.adr.bigdata.fullimport.Loggable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * @author minhvv2 Redis cache model
 */
public class RdCacheModel implements Loggable {

	private JedisPool pool;
	private int db;
	private Jedis jedis = null;
	private Pipeline pipeline = null;

	public void put(String key, Map<String, String> data) {
		if (jedis == null) {
			jedis = pool.getResource();
			pipeline = jedis.pipelined();
		}
		pipeline.select(db);
		pipeline.hmset(key, data);
	}
	
	public void sync() {
		pipeline.sync();
	}

	public void close() throws IOException {
		if (jedis != null) {
			pipeline.close();
			pipeline = null;
			jedis.close();
			jedis = null;
			pool.destroy();
		}
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public int getDb() {
		return db;
	}

	public void setDb(int db) {
		this.db = db;
	}

}