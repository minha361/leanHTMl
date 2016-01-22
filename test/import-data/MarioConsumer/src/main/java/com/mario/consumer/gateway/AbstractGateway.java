package com.mario.consumer.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mario.consumer.entity.message.BaseMessageDeserializer;
import com.mario.consumer.entity.message.MessageDeserializer;
import com.nhb.common.data.PuObjectRO;
import com.nhb.eventdriven.impl.BaseEventDispatcher;

abstract class AbstractGateway extends BaseEventDispatcher implements Gateway {

	private String name;
	private String extensionName;
	private boolean initialized = false;
	private MessageDeserializer deserializer = new BaseMessageDeserializer();

	private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

	@Override
	public final void init(String name, PuObjectRO initParams) {
		if (this.initialized) {
			throw new RuntimeException("cannot re-init gateway which has been initialized");
		}
		this.name = name;
		this.init(initParams);
		this.initialized = true;
	}

	protected abstract void init(PuObjectRO initParams);

	public String getName() {
		return name;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}

	public MessageDeserializer getDeserializer() {
		return deserializer;
	}

	public void setDeserializer(MessageDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	public Logger getErrorLogger() {
		return errorLogger;
	}
}
