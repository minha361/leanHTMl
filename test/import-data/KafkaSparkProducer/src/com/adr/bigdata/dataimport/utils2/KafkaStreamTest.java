package com.adr.bigdata.dataimport.utils2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import kafka.serializer.StringDecoder;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

public class KafkaStreamTest {
	public static void main(String[] args) {
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
		String zkConnect = props.getProperty("zkConnect");
		String master = props.getProperty("sparkMaster");
		String appName = props.getProperty("appName");
		String user = props.getProperty("user");
		Map<String, Integer> topics = new HashMap<String, Integer>();
		int numParPerTopic = Integer.valueOf(props.getProperty("numParPerTopic"));
		// Init topic map
		topics.put(Constants.BRAND_TOPIC, numParPerTopic);
		topics.put(Constants.MERCHANT_TOPIC, numParPerTopic);
		topics.put(Constants.WAREHOUSE_PRODUCTITEM_TOPIC, numParPerTopic);
		topics.put(Constants.WAREHOUSE_TOPIC, numParPerTopic);
		topics.put(Constants.PRODUCT_ITEM_TOPIC, numParPerTopic);
		topics.put(Constants.PROMOTION_PRODUCTITEM_TOPIC, numParPerTopic);
		topics.put(Constants.PROMOTION_TOPIC, numParPerTopic);
		topics.put(Constants.ATT_TOPIC, numParPerTopic);
		topics.put(Constants.ATT_VALUE_TOPIC, numParPerTopic);
		
		Set<String> stopics = new HashSet<String>();
		stopics.add(Constants.BRAND_TOPIC);
		stopics.add(Constants.MERCHANT_TOPIC);
		stopics.add(Constants.WAREHOUSE_PRODUCTITEM_TOPIC);
		stopics.add(Constants.WAREHOUSE_TOPIC);
		stopics.add(Constants.PRODUCT_ITEM_TOPIC);
		stopics.add(Constants.PROMOTION_PRODUCTITEM_TOPIC);
		stopics.add(Constants.PROMOTION_TOPIC);
		stopics.add(Constants.ATT_TOPIC);
		stopics.add(Constants.ATT_VALUE_TOPIC);

		Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("zookeeper.connect", zkConnect);
		kafkaParams.put("zookeeper.connection.timeout.ms", "10000");
		kafkaParams.put("group.id", user);

		Duration batchDuration = new Duration(Long.valueOf(props
				.getProperty("batchDuration")));
		int numStreams = Integer.valueOf(props.getProperty("numStreams"));
		
		//int maxNumDStream = Integer.valueOf(props.getProperty("maxNumDStream"));
		
		SparkConf sparkConf = new SparkConf();
		JavaSparkContext jscontext = new JavaSparkContext(master, appName);
		JavaStreamingContext jstreamContext = new JavaStreamingContext(
				jscontext, batchDuration);

		
		JavaPairReceiverInputDStream<String, String> directKafkaStream = 
			     KafkaUtils.createStream(jstreamContext,
			        String.class,String.class,StringDecoder.class,StringDecoder.class,
			         kafkaParams, topics,StorageLevel.MEMORY_AND_DISK());
		
		
		
	}
}
