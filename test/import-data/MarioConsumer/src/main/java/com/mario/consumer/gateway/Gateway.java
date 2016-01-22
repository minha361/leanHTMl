package com.mario.consumer.gateway;

import com.nhb.common.data.PuObjectRO;
import com.nhb.common.Loggable;
import com.nhb.eventdriven.EventDispatcher;

public interface Gateway extends Loggable, EventDispatcher {

	void init(String name, PuObjectRO initParams);

	void start() throws Exception;

	void stop() throws Exception;

	String getName();
}
