package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJob;
import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJobFactory;
import com.adr.bigdata.dataimport.utils2.Constants;

public class MessageProcessorV3 {
	static Logger logger = Logger.getLogger(MessageProcessorV3.class.getName());
	
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
		
		logger.setLevel(Level.ALL);
		
		// Set up Spark params.
		String zkConnect = props.getProperty("zookeeper.connect");
		String master = props.getProperty("sparkMaster");
		String appName = props.getProperty("appName");
		String user = props.getProperty("user");
		Map<String, Integer> topics = new HashMap<String, Integer>();
		int numParPerTopic = Integer.valueOf(props
				.getProperty("numParPerTopic"));
		// Init topic map
		topics.put(Constants.BRAND_TOPIC, numParPerTopic);
		topics.put(Constants.MERCHANT_TOPIC, numParPerTopic);
		topics.put(Constants.CATEGORY_TOPIC, numParPerTopic);
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
		JavaPairDStream<String, String> msg = KafkaUtils.createStream(
				jstreamContext, zkConnect, user, topics);
		logger.info("Set up spark context successfully --- info ...");
		logger.error("Set up spark context successfully --- error ...");
		logger.debug("Set up spark context successfully --- debug ...");
		System.out.println("Set up spark context successfully ...");
		
		msg.foreachRDD(new Function<JavaPairRDD<String, String>, Void>() {

			private static final long serialVersionUID = -691459470823965049L;

			@Override
			public Void call(JavaPairRDD<String, String> input)
					throws Exception {
				// TODO Auto-generated method stub
				Map<String, String> mm = input.collectAsMap();
				for (Map.Entry<String, String> me : mm.entrySet()) {

					String key = me.getKey().trim();
					String[] tmp = key.split(com.adr.bigdata.dataimport.utils2.Constants.SEP);
					String topic = tmp[1];
					logger.info(topic + " : " + Constants.TOPIC_TYPE.valueOf(topic).getCode());
					System.out.println(topic + " : " + Constants.TOPIC_TYPE.valueOf(topic).getCode());
					logger.info("Set up spark context successfully --- info ...inside ");
					logger.error("Set up spark context successfully --- error ... inside");
					logger.debug("Set up spark context successfully --- debug ... inside");
					String mess = me.getValue();
					logger.info("key = " + key + "\tmsg = " + mess);
					System.out.println("key = " + key + "\tmsg = " + mess);
					ProcessingJob j = ProcessingJobFactory
							.getProcessingJob(Constants.TOPIC_TYPE.valueOf(
									topic).getCode());
					j.setMsg(mess);
					j.run();

				}
				return null;
			}

		});
//		
//		msg.foreach(new Function<JavaPairRDD<String,String>,Void>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Void call(JavaPairRDD<String, String> arg0) throws Exception {
//				// TODO Auto-generated method stub
//				
//				JavaRDD<JavaRDD<SolrInputDocument>> docs = arg0.map( new Function<Tuple2<String,String>,JavaRDD<SolrInputDocument>>() {
//
//					/**
//					 * 
//					 */
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					public JavaRDD<SolrInputDocument> call(Tuple2<String, String> input)
//							throws Exception {
//						String[] tmp = input._1.split("_");
//						String topic = tmp[1];
//						System.out.println("Check point 2 : "
//								+ Constants.TOPIC_TYPE.valueOf(topic)
//										.getCode());
//						ProcessingJob job = ProcessingJobFactory
//								.getProcessingJob(Constants.TOPIC_TYPE
//										.valueOf(topic).getCode());
//						System.out.println(input._2);
//						job.setMsg(input._2);
//						return job.makeSolrDoc();
//					}
//					
//				});
//				
//				return null;
//			}
//			
//		});;

		jstreamContext.start(); // Start the computation
		jstreamContext.awaitTermination();

	}
}
