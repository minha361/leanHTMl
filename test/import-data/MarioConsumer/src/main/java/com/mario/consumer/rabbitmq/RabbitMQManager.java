package com.mario.consumer.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQManager {
	private Map<String, RabbitMQConfig> name2Config = new HashMap<>();
	private Map<String, Connection> name2Connection = new HashMap<>();
	private Map<String, Channel> name2Chanel = new HashMap<>();
	
	public Channel getChanel(String name, String threadName) throws IOException {
		String key = name + '_' + threadName;
		if (!name2Chanel.containsKey(key)) {
			if (name2Connection.containsKey(name)) {
				Channel channel = name2Connection.get(name).createChannel();
				name2Chanel.put(key, channel);
			} else {
				if (name2Config.containsKey(name)) {
					RabbitMQConfig config = name2Config.get(name);
					if (config.getExchangeName() == null && config.getQueueName() == null) {
						throw new RuntimeException("Either exchange_name or queue_name must not be null");
					}
					ConnectionFactory fac = new ConnectionFactory();
					fac.setHost(config.getHost());
					fac.setPort(config.getPort());
					fac.setUsername(config.getUsername());
					fac.setPassword(config.getPassword());
					
					Connection conn = fac.newConnection();
					name2Connection.put(name, conn);
					Channel channel = conn.createChannel();
					if (config.getExchangeName() != null) {
						channel.exchangeDeclare(config.getExchangeName(), "fanout");
					} else {
						channel.queueDeclare(config.getQueueName(), true, false, false, null);
					}
					name2Chanel.put(key, channel);
				} else {
					return null;
				}
			}			
		}
		
		return name2Chanel.get(key);
	}
	
	public void register(RabbitMQConfig config) {
		name2Config.put(config.getName(), config);
	}
	
	public void close() {
		for (Entry<String, Channel> e : name2Chanel.entrySet()) {
			try {
				if (e.getValue().isOpen()) {
					e.getValue().close();
				}
			} catch (Exception ex) {
				System.err.println("Error while close chanel: " + e.getKey());
				ex.printStackTrace();
			}
		}
		for (Entry<String, Connection> e : name2Connection.entrySet()) {
			try {
				if (e.getValue().isOpen()) {
					e.getValue().close();
				}
			} catch (Exception ex) {
				System.err.println("Error while close connection: " + e.getKey());
				ex.printStackTrace();
			}
		}
	}
	
}
