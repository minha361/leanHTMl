package com.adr.bigdata.indexingrd.models.impl;

import com.adr.bigdata.indexingrd.models.AbstractModel;
import com.adr.bigdata.indexingrd.models.RdCachedModel;
import com.nhb.common.db.sql.DBIAdapter;
import com.rabbitmq.client.Channel;

import redis.clients.jedis.JedisPool;

public class ModelFactory {

	private DBIAdapter dbiAdapter;
	private JedisPool jedisPool;
	private JedisPool outputJedisPool;

	private ModelFactory(DBIAdapter dbiAdapter, JedisPool jedisPool, JedisPool outputJedisPool) {
		super();
		this.dbiAdapter = dbiAdapter;
		this.jedisPool = jedisPool;
		this.outputJedisPool = outputJedisPool;
	}

	public <T extends AbstractModel> T getModel(Class<T> modelClass) {
		assert modelClass != null;
		try {
			T result = modelClass.newInstance();
			result.setDbAdapter(this.dbiAdapter);
			if (result instanceof RdCachedModel) {
				//				((RdCachedModel) result).setChannel(channel);
				((RdCachedModel) result).setJedisPool(jedisPool);
				((RdCachedModel) result).setOutputJedisPool(outputJedisPool);
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static ModelFactory newInstance(DBIAdapter dbiAdapter, Channel channel, JedisPool jedisPool,
			JedisPool outputJedisPool) {
		return new ModelFactory(dbiAdapter, jedisPool, outputJedisPool);
	}
}
