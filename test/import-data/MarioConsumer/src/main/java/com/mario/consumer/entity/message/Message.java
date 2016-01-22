package com.mario.consumer.entity.message;

import com.nhb.common.data.PuObject;
import com.nhb.eventdriven.Callable;

public interface Message {

	int getType();

	int getRetryCount();

	void setRetryCount(int count);

	void increaseRetryCount();

	void setType(int type);

	PuObject getData();

	void setData(PuObject data);

	Callable getCallback();

	void setCallback(Callable callback);
}
