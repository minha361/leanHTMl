package com.adr.bigdata.dataimport;

import java.io.IOException;

import com.nhb.common.data.PuObject;

public class TestPuObject {

	public static void main(String[] args) throws IOException {
		PuObject puo = new PuObject();
		puo.setString("name", "ThangVT");
		byte[] bytes = puo.toMessagePack();
		PuObject puo2 = PuObject.fromMessagePack(bytes);
		System.out.println("deserialized puo: " + puo2);
	}

}
