package com.adr.bigdata.dataimport.utils2;

import com.google.gson.JsonObject;

import kafka.serializer.Decoder;

public class JsonObjectDecoder implements Decoder<JsonObject> {

	@Override
	public JsonObject fromBytes(byte[] arg0) {
		// TODO Auto-generated method stub
		String str = arg0.toString();
		return null;
	}

}
