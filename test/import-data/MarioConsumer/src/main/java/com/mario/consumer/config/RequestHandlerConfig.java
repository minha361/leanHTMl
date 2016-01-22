package com.mario.consumer.config;

import java.util.Collection;

public class RequestHandlerConfig extends MarioEventHandler {

	private int numWorkers = 4;
	private int ringBufferSize = 128;
	private Collection<String> bindingGateways;

	public RequestHandlerConfig(String handlerClass) {
		this.setHandler(handlerClass);
	}

	public int getNumWorkers() {
		return numWorkers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public Collection<String> getBindingGateways() {
		return bindingGateways;
	}

	public void setBindingGateways(Collection<String> bindingGateways) {
		this.bindingGateways = bindingGateways;
	}

	public int getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}
}
