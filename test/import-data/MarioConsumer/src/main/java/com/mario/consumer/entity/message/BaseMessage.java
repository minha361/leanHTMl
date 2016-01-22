package com.mario.consumer.entity.message;

import com.nhb.common.data.PuObject;
import com.nhb.eventdriven.Callable;

public class BaseMessage implements Message {

	private int type;
	private PuObject data;
	private Callable callback;
	private int retryCount = 0;

	public BaseMessage() {
		// do nothing
	}

	public void clone(Message msg) {
		this.setType(msg.getType());
		this.setData(msg.getData());
		this.setCallback(msg.getCallback());
		this.setRetryCount(msg.getRetryCount());
	}

	public BaseMessage(int type, PuObject data) {
		this();
		this.setType(type);
		this.setData(data);
	}

	public BaseMessage(int type, PuObject data, Callable callback) {
		this(type, data);
		this.setCallback(callback);
	}

	@Override
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

	@Override
	public Callable getCallback() {
		return callback;
	}

	@Override
	public void setCallback(Callable callback) {
		this.callback = callback;
	}

	@Override
	public int getRetryCount() {
		return this.retryCount;
	}

	@Override
	public void increaseRetryCount() {
		this.retryCount++;
	}

	@Override
	public void setRetryCount(int count) {
		this.retryCount = count;
	}

}
