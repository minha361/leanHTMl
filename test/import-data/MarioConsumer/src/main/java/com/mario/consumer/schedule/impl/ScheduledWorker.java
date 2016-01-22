package com.mario.consumer.schedule.impl;

import com.lmax.disruptor.WorkHandler;
import com.nhb.common.Loggable;

class ScheduledWorker implements Loggable, WorkHandler<ScheduledEvent> {

	@Override
	public void onEvent(ScheduledEvent event) throws Exception {
		if (event.getCallback() != null) {
			event.getCallback().call();
		}
	}
}
