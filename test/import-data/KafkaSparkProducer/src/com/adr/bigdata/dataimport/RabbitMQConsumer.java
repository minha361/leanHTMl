package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.stratio.receiver.RabbitMQUtils;

public class RabbitMQConsumer {
	public static void main(String[] args) {
		final String propFile = args[0];
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String rabbitMQHost = props.getProperty("rabbitMQHost");
		Integer rabbitMQPort = Integer.valueOf(props
				.getProperty("rabbitMQPort"));
		String rabbitMQQueueName = props.getProperty("rabbitMQQueueName");

		final String master = props.getProperty("sparkMaster");
		final String appName = props.getProperty("rbAppName");
		final String solrZKConnect = props
				.getProperty("solr.zookeeper.connect");
		final String collection = props.getProperty("collection");
		final int solrBatchSize = Integer.valueOf(props
				.getProperty("solrBatchSize"));

		final Duration batchDuration = new Duration(Long.valueOf(props
				.getProperty("batchDuration")));
		final int numStreams = Integer.valueOf(props.getProperty("numStreams"));

		final SparkConf conf = new SparkConf();
		conf.set("spark.cores.max", String.valueOf(numStreams));
		conf.setAppName(appName);
		conf.setMaster(master);
		JavaStreamingContext jsmc = new JavaStreamingContext(conf,
				batchDuration);
		JavaReceiverInputDStream<String> rabbitMQMsg = RabbitMQUtils
				.createJavaStreamFromAQueue(jsmc, rabbitMQHost, rabbitMQPort,
						rabbitMQQueueName, StorageLevel.MEMORY_AND_DISK());
		rabbitMQMsg.print();
		jsmc.start();
		jsmc.awaitTermination();
		
	}
}
