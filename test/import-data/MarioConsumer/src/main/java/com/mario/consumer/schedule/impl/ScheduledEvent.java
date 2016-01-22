package com.mario.consumer.schedule.impl;

import com.mario.consumer.schedule.ScheduledCallback;

class ScheduledEvent {
	private ScheduledCallback callback;

	public ScheduledCallback getCallback() {
		return callback;
	}

	public void setCallback(ScheduledCallback callback) {
		this.callback = callback;
	}
}
