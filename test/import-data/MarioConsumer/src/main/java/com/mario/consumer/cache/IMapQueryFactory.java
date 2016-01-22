package com.mario.consumer.cache;

public class IMapQueryFactory {

	public static <E> CacheMapQuery<E> newIMapQuery() {
		return new IMapQuery<E>();
	}
}
