package com.nhb.common.db.sql;

import java.util.Map.Entry;
import java.util.Properties;

import com.nhb.common.Loggable;
import com.nhb.common.data.PuDataWrapper;
import com.nhb.common.data.PuObject;

public class SQLDataSourceConfig implements Loggable {

	private String name;
	private PuObject initParams;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PuObject getInitParams() {
		return initParams;
	}

	public void setInitParams(PuObject initParams) {
		this.initParams = initParams;
	}

	public Properties getProperties() {
		if (this.initParams != null) {
			Properties props = new Properties();
			for (Entry<String, PuDataWrapper> entry : this.initParams) {
				props.setProperty(entry.getKey(), entry.getValue().getData().toString());
			}
			return props;
		}
		return null;
	}
}
