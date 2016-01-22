package com.mario.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class MarioProducerV2 {

	private static String QUEUE_NAME = "kafka_spark_queue";

	public static void main(String[] args) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		//factory.setHost("10.220.75.84");
		factory.setHost("10.220.75.133");
		factory.setUsername("admin");
		factory.setPassword("admin");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		FileInputStream is = new FileInputStream(new File("data/msg.dat"));
		StringWriter sw = new StringWriter();
		IOUtils.copy(is, sw);
		String data = sw.toString();

		String[] arr = data.split("\n");

		try {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			for (int i = 0; i < arr.length; i++) {
				String message = arr[i];
				channel.basicPublish("", QUEUE_NAME, MessageProperties.BASIC,
						message.getBytes());
				System.out.println(i + "\t [x] Sent '" + message + "'");
			}
		} finally {
			if (channel.isOpen()) {
				channel.close();
			}
			if (connection.isOpen()) {
				connection.close();
			}
		}
	}

}