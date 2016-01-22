package com.mario.consumer.entity.handler;

import com.nhb.common.Loggable;
import com.mario.consumer.entity.LifeCycle;
import com.mario.consumer.entity.Pluggable;

public interface OnStartupHandler extends LifeCycle, Pluggable, Loggable {

	public void onStart() throws Exception;
}
