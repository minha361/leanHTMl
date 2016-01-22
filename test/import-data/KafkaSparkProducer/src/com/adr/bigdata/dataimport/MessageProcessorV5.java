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
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;
import com.lucidworks.spark.SolrSupport;


import com.adr.bigdata.dataimport.kafka.SolrInputDocumentDecoder;

public class MessageProcessorV5 implements Serializable {
	private static final long serialVersionUID = -8762052832103541798L;

	public static void main(final String[] args) throws FileNotFoundException,
			IOException {
		final String propFile = args[0];
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(propFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		final String kafkaZKConnect = props
				.getProperty("kafka.zookeeper.connect");
		final String zookeeperConnectionTimeoutMS = props
				.getProperty("zookeeper.connection.timeout.ms");
		final String master = props.getProperty("sparkMaster");
		final String appName = props.getProperty("appName");
		final String user = props.getProperty("user");
		final String solrZKConnect = props
				.getProperty("solr.zookeeper.connect");
		final String collection = props.getProperty("collection");
		final int solrBatchSize = Integer.valueOf(props
				.getProperty("solrBatchSize"));
		final Map<String, Integer> topics = new HashMap<String, Integer>();
		final int numParPerTopic = Integer.valueOf(props
				.getProperty("numParPerTopic"));
		topics.put(props.getProperty("topic"), numParPerTopic);

		final Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("zookeeper.connect", kafkaZKConnect);
		kafkaParams.put("zookeeper.connection.timeout.ms",
				zookeeperConnectionTimeoutMS);
		kafkaParams.put("group.id", user);
		final Duration batchDuration = new Duration(Long.valueOf(props
				.getProperty("batchDuration")));
		final int numStreams = Integer.valueOf(props.getProperty("numStreams"));
		final SparkConf conf = new SparkConf();
		conf.set("spark.cores.max", String.valueOf(numStreams));
		conf.setAppName(appName);
		conf.setMaster(master);
		JavaStreamingContext jsmc = new JavaStreamingContext(conf,
				batchDuration);
		List<JavaPairDStream<String, SolrInputDocument>> lsmg = new ArrayList<JavaPairDStream<String, SolrInputDocument>>(numStreams);
		for(int i=0;i<numStreams;i++)
			lsmg.add( (JavaPairDStream<String, SolrInputDocument>) KafkaUtils
				.createStream(jsmc, String.class, SolrInputDocument.class,
						StringDecoder.class, SolrInputDocumentDecoder.class,
						kafkaParams, topics, StorageLevel.MEMORY_AND_DISK()));
		
		JavaPairDStream<String,SolrInputDocument> msg = (JavaPairDStream) jsmc.union(lsmg.get(0), lsmg.subList(1, lsmg.size())).repartition(numStreams);

		msg.foreachRDD(new Function<JavaPairRDD<String,SolrInputDocument>,Void>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Void call(JavaPairRDD<String, SolrInputDocument> arg0)
					throws Exception {
				//System.out.println("Docnums : " + arg0.count());
				JavaRDD<SolrInputDocument> docs = arg0.map(new Function<Tuple2<String,SolrInputDocument>,SolrInputDocument>() {

					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(
							Tuple2<String, SolrInputDocument> arg0)
							throws Exception {
						return arg0._2;
					}				
				});
				if (docs.count() > 0)
					SolrSupport.indexDocs2(solrZKConnect, collection, solrBatchSize, docs);
				return null;
			}
		});

		jsmc.start();
		jsmc.awaitTermination();
	}
}
