package com.mario.consumer.config;

import com.nhb.common.Loggable;
import com.nhb.common.data.PuObject;

public class MarioEventHandler implements Loggable {

	private String extensionName;
	private String handler;
	private PuObject initParams;

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public PuObject getInitParams() {
		return initParams;
	}

	public void setInitParams(PuObject initParams) {
		this.initParams = initParams;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}
}
