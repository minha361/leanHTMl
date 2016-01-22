package com.adr.bigdata.dataimport.model.sender;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.nhb.common.data.PuObject;

public class MessageSenderModel extends CachedModel {
	// Set up Spark Streaming and Kafka connection
	private String propFile;
	private Producer<String,PuObject> producer;
	
	public void init() {
		//
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties prodConfig = new Properties();
		prodConfig.put("metadata.broker.list", prop.getProperty("metadata.broker.list"));
		prodConfig.put("serializer.class", prop.getProperty("serializer.class"));
		prodConfig.put("partitioner.class", prop.getProperty("partitioner.class"));
		prodConfig.put("request.required.acks", prop.getProperty("request.required.acks"));
		
		ProducerConfig pConfig = new ProducerConfig(prodConfig);
		producer = new Producer<String,PuObject>(pConfig);
		
	}
	
	public MessageSenderModel() {
		init();
	}
	
	public void process(PuObject input) {
		// This is where the messages are sent to Kafka.
		// Send message here using Kafka Producer
		String topic = "brand";
		KeyedMessage<String,PuObject> msg  = new KeyedMessage<String,PuObject>(topic,input);
		producer.send(msg);
	}
	
	
}
