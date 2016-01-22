package com.adr.bigdata.indexing.models;

import com.mario.consumer.cache.CacheWrapper;

public abstract class CachedModel extends AbstractModel {

	private CacheWrapper cacheWrapper;

	public CacheWrapper getCacheWrapper() {
		return cacheWrapper;
	}

	public void setCacheWrapper(CacheWrapper cacheWrapper) {
		this.cacheWrapper = cacheWrapper;
	}
}
