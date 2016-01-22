package com.mario.consumer.entity;

import com.mario.consumer.api.MarioApi;

public interface Pluggable {

	void setApi(MarioApi api);

	MarioApi getApi();
}
