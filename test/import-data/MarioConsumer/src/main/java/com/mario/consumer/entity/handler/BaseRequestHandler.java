package com.mario.consumer.entity.handler;

import com.nhb.common.data.PuObject;

public abstract class BaseRequestHandler extends AbstractLifeCyclePluggable implements OnRequestHandler {

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		getLogger().debug("handling data type {}: {}", type, data);
		return null;
	}
}
