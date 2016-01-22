package com.adr.bigdata.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.serializer.StringDecoder;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJob;
import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJobFactory;
import com.adr.bigdata.dataimport.utils2.Constants;

public class SolrSparkTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2626278093032479864L;

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

		Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("zookeeper.connect", zkConnect);
		kafkaParams.put("zookeeper.connection.timeout.ms", "10000");
		kafkaParams.put("group.id", user);

		Duration batchDuration = new Duration(Long.valueOf(props
				.getProperty("batchDuration")));
		int numStreams = Integer.valueOf(props.getProperty("numStreams"));

		// int maxNumDStream =
		// Integer.valueOf(props.getProperty("maxNumDStream"));

		JavaSparkContext jscontext = new JavaSparkContext(master, appName);
		JavaStreamingContext jstreamContext = new JavaStreamingContext(
				jscontext, batchDuration);

		List<JavaPairReceiverInputDStream<String, String>> messages = new ArrayList<JavaPairReceiverInputDStream<String, String>>(
				numStreams);
		for (int i = 0; i < numStreams; i++) {
			messages.add(KafkaUtils.createStream(jstreamContext, String.class,
					String.class, StringDecoder.class, StringDecoder.class,
					kafkaParams, topics, StorageLevel.MEMORY_AND_DISK()));
		}

		System.out.println("Check point 1 : ");
		System.out.println("Number of Streamings : " + messages.size());


		for (JavaPairReceiverInputDStream<String, String> stream: messages) {
		}
	
			
		for (JavaPairReceiverInputDStream<String, String> stream : messages) {
			JavaDStream<Integer> jobs = stream
					.map(new Function<Tuple2<String, String>, Integer>() {

						private static final long serialVersionUID = -691459470823965049L;

						@Override
						public Integer call(Tuple2<String, String> input)
								throws Exception {
							// TODO Auto-generated method stub
							String[] tmp = input._1.split("_");
							String topic = tmp[1];
							System.out.println("Check point 2 : "
									+ Constants.TOPIC_TYPE.valueOf(topic)
											.getCode());
							ProcessingJob job = ProcessingJobFactory
									.getProcessingJob(Constants.TOPIC_TYPE
											.valueOf(topic).getCode());
							System.out.println(input._2);
							job.setMsg(input._2);
							// job.run();
							return job.run();
						}
					});
			
			JavaPairDStream<Integer, Long> op = jobs.countByValue();

		}

		/*
		 * for (JavaPairReceiverInputDStream<String, String> stream : messages)
		 * { JavaDStream<ProcessingJob> jobs = stream.map( new
		 * Function<Tuple2<String, String>, ProcessingJob>() {
		 * 
		 * private static final long serialVersionUID = -691459470823965049L;
		 * 
		 * @Override public ProcessingJob call(Tuple2<String, String> input)
		 * throws Exception { // TODO Auto-generated method stub String[] tmp =
		 * input._1.split("_"); String topic = tmp[1];
		 * System.out.println("Check point 2 : " +
		 * Constants.TOPIC_TYPE.valueOf(topic) .getCode()); ProcessingJob job =
		 * ProcessingJobFactory .getProcessingJob(Constants.TOPIC_TYPE
		 * .valueOf(topic).getCode()); System.out.println(input._2);
		 * job.setMsg(input._2); job.run(); return job; } });
		 */
		/*
		 * jobs.foreach(new Function<JavaRDD<ProcessingJob>, Void>() {
		 * 
		 * private static final long serialVersionUID = 1L;
		 * 
		 * public Void call(JavaRDD<ProcessingJob> input) throws Exception { //
		 * TODO Auto-generated method stub Properties prop = new Properties();
		 * for (ProcessingJob j : input.collect()) {
		 * System.out.println("Running jobs ..."); //j.run(prop); j.run(); }
		 * return null; } });
		 */

		jstreamContext.start(); // Start the computation
		jstreamContext.awaitTermination();

		System.out.println("Number of jobs : ");
		System.out.println("Check point 3 : ");
	}
}
