package com.adr.bigdata.dataimport;

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

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
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

import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;
import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJob;
import com.adr.bigdata.dataimport.logic.impl.procjob.ProcessingJobFactory;
import com.adr.bigdata.dataimport.utils2.Constants;

public class MessageProcessorV2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8762052832103541798L;

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
		
		JavaSparkContext jscontext = new JavaSparkContext(master, appName);
		JavaStreamingContext jsmc = new JavaStreamingContext(jscontext, batchDuration);
		List<JavaPairDStream<String, String>> msg = new ArrayList<JavaPairDStream<String, String>>();
		for(int i = 0;i<numStreams;i++) {
			msg.add((JavaPairDStream)KafkaUtils.createStream(jsmc,zkConnect , user, topics));
		}
		
		JavaPairDStream<String,String> umsg = jsmc.union(msg.get(0), msg.subList(1, msg.size()));
		JavaPairDStream<String,String> pmsg = umsg.repartition(numStreams);
		pmsg.foreachRDD(new Function<JavaPairRDD<String,String>,Void>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Void call(JavaPairRDD<String, String> arg0) throws Exception {
				
				JavaRDD<DocGenerationJob> jobs = arg0.map(new Function<Tuple2<String,String>,DocGenerationJob>() {
					private static final long serialVersionUID = 1L;
					@Override
					public DocGenerationJob call(Tuple2<String, String> input)
							throws Exception {
						String key = input._1;
						String[] tmp = key.split(com.adr.bigdata.dataimport.utils2.Constants.SEP);
						if (tmp.length != 2)
							return null;
						String topic = tmp[1];
						int type = Constants.TOPIC_TYPE.valueOf(topic)
								.getCode();
						String msg = input._2;
						DocGenerationJob job = DocGenerationJobFactory
								.getDocGenerationJob(type);
						return job;
					}			
				});		
				return null;
			}
		});
		
		jsmc.start();              // Start the computation
		jsmc.awaitTermination(); 

	}
}
