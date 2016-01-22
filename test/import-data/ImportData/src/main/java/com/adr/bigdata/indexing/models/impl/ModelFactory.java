package com.adr.bigdata.indexing.models.impl;

import com.adr.bigdata.indexing.models.AbstractModel;
import com.adr.bigdata.indexing.models.CachedModel;
import com.adr.bigdata.indexing.models.SolrModel;
import com.mario.consumer.cache.CacheWrapper;
import com.nhb.common.db.sql.DBIAdapter;

public class ModelFactory {

	private int numDocPerRequest;
	private DBIAdapter dbiAdapter;
	private CacheWrapper cacheWrapper;

	private ModelFactory(DBIAdapter dbiAdapter, CacheWrapper cacheWrapper, int numDocPerRequest) {
		this.dbiAdapter = dbiAdapter;
		this.numDocPerRequest = numDocPerRequest;
		this.cacheWrapper = cacheWrapper;
	}

	public <T extends AbstractModel> T getModel(Class<T> modelClass) {
		assert modelClass != null;
		try {
			T result = modelClass.newInstance();
			result.setDbAdapter(this.dbiAdapter);
			if (result instanceof CachedModel) {
				((CachedModel) result).setCacheWrapper(this.cacheWrapper);
			}
			if (result instanceof SolrModel) {
				((SolrModel) result).setNumDocPerRequest(this.numDocPerRequest);
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static ModelFactory newInstance(DBIAdapter adapter, CacheWrapper cacheWrapper, int numDocPerRequest) {
		return new ModelFactory(adapter, cacheWrapper, numDocPerRequest);
	}
}
