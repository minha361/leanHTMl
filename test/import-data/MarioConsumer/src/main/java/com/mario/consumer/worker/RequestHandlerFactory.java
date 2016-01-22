package com.mario.consumer.worker;

import com.mario.consumer.entity.handler.OnRequestHandler;

public interface RequestHandlerFactory {

	public OnRequestHandler newInstance();
}
