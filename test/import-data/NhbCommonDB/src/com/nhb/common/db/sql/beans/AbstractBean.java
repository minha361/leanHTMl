package com.nhb.common.db.sql.beans;

import java.io.Serializable;

import com.google.gson.Gson;
import com.nhb.common.Loggable;

public class AbstractBean implements Serializable, Loggable {

	private static final long serialVersionUID = -3242520191301712269L;
	protected static final Gson gson = new Gson();

	@Override
	public String toString() {
		return gson.toJson(this);
	}
}
