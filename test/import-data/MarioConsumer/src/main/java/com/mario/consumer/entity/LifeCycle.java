package com.mario.consumer.entity;

import com.nhb.common.data.PuObjectRO;

public interface LifeCycle {

	public void init(PuObjectRO initParams);

	public void destroy() throws Exception;
}
