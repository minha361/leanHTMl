package com.nhb.common.data;

import com.nhb.eventdriven.Callable;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventDispatcher;

public class PuObjectEvent implements Event {

	public static final String CHANGE = "change";

	private String type;
	private Callable callable;

	private EventDispatcher target;

	public PuObjectEvent(String type) {
		this.type = type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setCallback(Callable callback) {
		this.callable = callback;
	}

	@Override
	public Callable getCallback() {
		return this.callable;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends EventDispatcher> T getTarget() {
		return (T) this.target;
	}

	@Override
	public void setTarget(EventDispatcher target) {
		this.target = target;
	}

}
