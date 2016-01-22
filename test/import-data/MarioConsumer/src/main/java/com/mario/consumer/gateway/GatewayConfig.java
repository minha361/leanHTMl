package com.mario.consumer.gateway;

import com.nhb.common.data.PuObjectRO;

public class GatewayConfig {

	private GatewayType type;
	private String name;
	private String extensionName;
	private String deserializerClassName;
	private PuObjectRO initParams;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PuObjectRO getInitParams() {
		return initParams;
	}

	public void setInitParams(PuObjectRO initParams) {
		this.initParams = initParams;
	}

	public GatewayType getType() {
		return type;
	}

	public void setType(GatewayType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("{name: %s, type: %s, initParams: %s}", this.getName(), this.getType().name(), this
				.getInitParams().toString());
	}

	public String getExtensionName() {
		return extensionName;
	}

	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}

	public String getDeserializerClassName() {
		return deserializerClassName;
	}

	public void setDeserializerClassName(String deserializerClassName) {
		if (deserializerClassName == null || deserializerClassName.trim().length() == 0)
			return;
		this.deserializerClassName = deserializerClassName;
	}
}
