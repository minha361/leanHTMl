package com.mario.consumer.entity.handler;

import com.nhb.common.Loggable;
import com.mario.consumer.entity.LifeCycle;

public interface OnScheduleHandler extends LifeCycle, Loggable {

	public void onSchedule() throws Exception;
}
