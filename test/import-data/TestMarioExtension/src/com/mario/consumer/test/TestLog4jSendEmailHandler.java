package com.mario.consumer.test;

import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;

public class TestLog4jSendEmailHandler extends BaseRequestHandler {

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		getLogger("sendEmailToWatchmans").error("error", new Exception("test"));
		return "done";
	}
}
