package com.mario.consumer.cache;

import java.util.Map;
import java.util.Set;

public interface CacheMapQuery<E> {

	Set<E> execute(Map<?, E> cacheMap, String query);
}
