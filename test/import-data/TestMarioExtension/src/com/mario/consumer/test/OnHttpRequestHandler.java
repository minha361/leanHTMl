package com.mario.consumer.test;

import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;

public class OnHttpRequestHandler extends BaseRequestHandler {

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		return data;
	}
}
