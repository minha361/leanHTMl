package com.mario.consumer.entity.handler;

import com.mario.consumer.entity.LifeCycle;
import com.nhb.common.data.PuObjectRO;

class AbstractLifeCyclePluggable extends AbstractPluggable implements LifeCycle {

	@Override
	public void init(PuObjectRO initParams) {
		// do nothing
	}

	@Override
	public void destroy() throws Exception {
		// do nothing
	}
}
