package com.mario.consumer.config;

public class LifecycleHandlerConfig extends MarioEventHandler {

	public LifecycleHandlerConfig(String startupHandler) {
		this.setHandler(startupHandler);
	}

}
