package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

public class MessagePerTopicProcessor {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
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
		int numParPerTopic = Integer.valueOf(props
				.getProperty("numParPerTopic"));
		
		Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("zookeeper.connect", zkConnect);
		kafkaParams.put("zookeeper.connection.timeout.ms", "10000");
		kafkaParams.put("group.id", user);

		Duration batchDuration = new Duration(Long.valueOf(props
				.getProperty("batchDuration")));
		int numStreams = Integer.valueOf(props.getProperty("numStreams"));
		JavaSparkContext jscontext = new JavaSparkContext(master, appName);
		JavaStreamingContext jstreamContext = new JavaStreamingContext(
				jscontext, batchDuration);
		
		JavaPairDStream<String,String> msg = KafkaUtils.createStream(jstreamContext, zkConnect, user, topics);
		

	}
}
