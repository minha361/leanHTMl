package com.mario.producer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Test {

	public static void main(String[] args) throws IOException {
		String usage = "Cac tham so lan luot theo thu tu sau\n"
				+ "\t[host] [port] [user] [pass] [queue name] [path to .dat file]\n" + "port <= 0 co nghia la mac dinh";

		String host;
		Integer port;
		String user;
		String pass;
		String queueName;
		String dataFile;

		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
			user = args[2];
			pass = args[3];
			queueName = args[4];
			dataFile = args[5];

			System.out.println(String.format(
					"Push data to queue: %s:%d with user: %s, pass: %s, queue name: %s and data file: %s", host, port,
					user, pass, queueName, dataFile));
		} catch (Exception e) {
			System.err.println("error with parameters, see usage bellow:\n" + usage);
			e.printStackTrace();
			return;
		}

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(pass);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		FileReader reader = new FileReader(dataFile);
		BufferedReader br = new BufferedReader(reader);
		String curLine;

		try {
			channel.queueDeclare(queueName, false, false, false, null);
			int i = 0;
			while ((curLine = br.readLine()) != null) {
				i++;
				channel.basicPublish("", queueName, MessageProperties.BASIC, curLine.getBytes());
				System.out.println(i + "\t [x] Sent '" + curLine + "'");
			}
		} finally {
			if (channel.isOpen()) {
				channel.close();
			}
			if (connection.isOpen()) {
				connection.close();
			}

			reader.close();
			br.close();
		}
	}

}
