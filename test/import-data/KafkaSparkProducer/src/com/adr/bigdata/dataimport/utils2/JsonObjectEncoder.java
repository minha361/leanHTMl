package com.adr.bigdata.dataimport.utils2;

import java.io.IOException;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.protobuf.Parser;
import com.nhb.common.data.PuObject;

import kafka.serializer.Encoder;

public class JsonObjectEncoder implements Encoder<JsonObject> {

	private static final JSONParser parser = new JSONParser(1);

	public byte[] encode(String str) throws ParseException, IOException {

		PuObject puo = PuObject.fromJSON(str);
		byte[] bytes = puo.toMessagePack();
		return bytes;
	}

	@Override
	public byte[] toBytes(JsonObject arg0) {

		return null;
	}
}
