package com.mario.consumer.config;

public class ScheduleHandlerConfig extends MarioEventHandler {

	private int period = 10000; // ms

	public ScheduleHandlerConfig(String handlerClass) {
		this.setHandler(handlerClass);
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
}
