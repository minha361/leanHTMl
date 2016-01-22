package com.mario.consumer.entity.handler;

import com.mario.consumer.entity.LifeCycle;
import com.mario.consumer.entity.Pluggable;
import com.nhb.common.Loggable;
import com.nhb.common.data.PuObject;

public interface OnRequestHandler extends LifeCycle, Pluggable, Loggable {

	public Object onRequest(int type, PuObject data) throws Exception;
}
