package com.mario.consumer.statics;

public abstract class Fields {

	private Fields() {
		// do nothing...
	}

	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String QUEUE_NAME = "queueName";
	public static final String RING_BUFFER_SIZE = "ringBufferSize";
	public static final String HANDLER = "handler";
	public static final String USERNAME = "userName";
	public static final String PASSWORD = "password";
	public static final String AUTO_ACK = "autoAck";
	public static final String ON_STARTUP = "startUp";
	public static final String ON_INTERVAL = "interval";
	public static final String PERIOD = "period";
	public static final String URL = "url";
	public static final String AUTO_RECONNECT = "autoReconnect";
	public static final String ASYNC = "async";
	public static final String DESERIALIZER = "deserializer";
	public static final String GATEWAY = "gateway";
	public static final String MESSAGE = "message";
	public static final String MAX_RETRY_COUNT = "maxRetryCount";
	public static final String COMMAND = "command";
	public static final String EXCHANGE = "exchange";
}
