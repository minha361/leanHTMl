package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kafka.serializer.StringDecoder;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.adr.bigdata.dataimport.utils2.PuObjectDecoder;
import com.nhb.common.data.PuObject;

public class DefaultMessageConsumer {
	public static void main(String[] args) {
		// Set up Kafka params.
		String propFile = args[0];
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Set up Spark params.
		String brokerList = props.getProperty("brokerList");
		String zkConnect = props.getProperty("zkConnect");
		String master = props.getProperty("sparkMaster");
		String appName = props.getProperty("appName");
		String user = props.getProperty("user");
		Map<String,Integer> topics = new HashMap<String,Integer>();
		topics.put("Brand", 10);
		topics.put("Merchant", 10);
		
		Map<String,String> kafkaParams = new HashMap<String,String>();
		kafkaParams.put("zookeeper.connect", zkConnect );
		kafkaParams.put("zookeeper.connection.timeout.ms", "10000");
		kafkaParams.put("group.id", user);
      
	      
		Duration batchDuration =  new Duration(Long.valueOf(props.getProperty("batchDuration")));
		JavaSparkContext jscontext= new JavaSparkContext(master,appName);
		JavaStreamingContext jstreamContext = new JavaStreamingContext(jscontext, batchDuration);
		
		// Set up Solr Connection
		
		JavaPairReceiverInputDStream<String,PuObject> messages = KafkaUtils.createStream(
				jstreamContext,
				String.class, PuObject.class, StringDecoder.class, PuObjectDecoder.class,
				kafkaParams, topics, StorageLevel.MEMORY_ONLY());
		

		// Process
		
	}
	
}
