package com.mario.consumer.cache;

import java.util.Map;

public interface CacheWrapper {

	<K, V> Map<K, V> getCacheMap(String name);
}
