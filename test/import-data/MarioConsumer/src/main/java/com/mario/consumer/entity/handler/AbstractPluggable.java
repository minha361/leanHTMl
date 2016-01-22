package com.mario.consumer.entity.handler;

import com.mario.consumer.api.MarioApi;
import com.mario.consumer.entity.Pluggable;

class AbstractPluggable implements Pluggable {

	private MarioApi api;

	@Override
	public void setApi(MarioApi api) {
		this.api = api;
	}

	@Override
	public MarioApi getApi() {
		return this.api;
	}

}
