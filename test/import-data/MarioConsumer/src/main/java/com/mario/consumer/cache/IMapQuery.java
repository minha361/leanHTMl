package com.mario.consumer.cache;

import java.util.Map;
import java.util.Set;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

class IMapQuery<E> implements CacheMapQuery<E> {

	@Override
	public Set<E> execute(Map<?, E> cacheMap, String query) {
		if (cacheMap != null) {
			if (cacheMap instanceof IMap) {
				return (Set<E>) ((IMap<?, E>) cacheMap)
						.values(new SqlPredicate(query));
			} else {
				throw new IllegalArgumentException("cache map must be IMap");
			}
		}
		return null;
	}
}
