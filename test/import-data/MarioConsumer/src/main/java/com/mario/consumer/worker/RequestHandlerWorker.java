package com.mario.consumer.worker;

import com.lmax.disruptor.WorkHandler;
import com.mario.consumer.entity.handler.OnRequestHandler;
import com.mario.consumer.entity.message.Message;
import com.nhb.common.Loggable;

class RequestHandlerWorker implements WorkHandler<Message>, Loggable {

	private OnRequestHandler concreteHandler;

	RequestHandlerWorker(OnRequestHandler concreteHandler) {
		if (concreteHandler == null) {
			throw new IllegalArgumentException("concrete handler cannot be null");
		}
		this.concreteHandler = concreteHandler;
	}

	@Override
	public void onEvent(Message event) throws Exception {
		Object result = this.concreteHandler.onRequest(event.getType(), event.getData());
		if (event.getCallback() != null) {
			event.getCallback().call(true, result);
		}
	}
}
