package com.adr.bigdata.dataimport.utils2;

public class Test {
	public static void main(String[] args) {
		String topic = "merchant";
		System.out.println(topic + " : "
				+ Constants.TOPIC_TYPE.valueOf(topic).getCode());

	}
}
