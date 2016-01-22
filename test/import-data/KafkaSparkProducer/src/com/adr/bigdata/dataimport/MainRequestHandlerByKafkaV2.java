package com.adr.bigdata.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.utils.FileSystemUtils;

public class MainRequestHandlerByKafkaV2 extends BaseRequestHandler implements
		Serializable {
	private static final long serialVersionUID = -3708134032694560594L;
	private String master;
	private SparkConf conf;
	private JavaSparkContext jsc;
	private boolean debugOption = false;
	private Producer<String, SolrInputDocument> producer = null;
	private String topic;
	private ProducerConfig pConfig;

	@Override
	public void init(PuObjectRO initParams) {
		master = initParams.getString("sparkMaster");
		debugOption = initParams.getBoolean("debugOption");
		topic = initParams.getString("topic");
		Integer numberOfCores = initParams.getInteger("numberOfCores");
		String jarFile = FileSystemUtils.createPathFrom(
				FileSystemUtils.getBasePathForClass(MainRequestHandler.class),
				"lib", "kafka-spark-producer-0.0.1-jar-with-dependencies.jar");
		if (debugOption)
			System.out.println("My jar file : " + jarFile);
		conf = new SparkConf().setMaster(master).setAppName(
				"MainRequestHandler");
		conf.setJars(new String[] { jarFile });
		conf.set("spark.driver.cores", String.valueOf(numberOfCores));
		jsc = new JavaSparkContext(conf);

		Properties prodConfig = new Properties();
		prodConfig.put("metadata.broker.list",
				initParams.getString("metadata.broker.list"));
		prodConfig.put("serializer.class",
				initParams.getString("serializer.class"));

		System.out.println("Serializer class : "
				+ initParams.getString("serializer.class"));
		prodConfig.put("zk.connect", initParams.getString("kafka.zk.connect"));
		pConfig = new ProducerConfig(prodConfig);
		producer = new Producer<String, SolrInputDocument>(pConfig);

		/******************************************************/
		// ClassLoader cl = ClassLoader.getSystemClassLoader();
		//
		// URL[] urls = ((URLClassLoader)cl).getURLs();
		//
		// for(URL url: urls){
		// System.out.println("test classloader : " + url.getFile());
		// }
		//
		/******************************************************/
	}

	public void destroy() throws Exception {
		if (producer != null)
			producer.close();
		jsc.close();
	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {

		DocGenerationJob job = DocGenerationJobFactory
				.getDocGenerationJob(type);
		byte[] bytes = data.toMessagePack();
		job.setByteArray(bytes);

		long startTime = 0, endTime = 0;
		if (debugOption) {
			System.out.println("Started job type : " + type);
			startTime = System.currentTimeMillis();
		}

		JavaRDD<SolrInputDocument> docs = job.genDocuments(jsc);
		if (docs.count() > 0) {
			if (debugOption) {
				endTime = System.currentTimeMillis();
				System.out.println("Docgen : " + startTime + "\t" + endTime
						+ "\t" + (endTime - startTime) + "\t" + docs.count());
			}

			// Send SolInputDocument to Kafka
			List<SolrInputDocument> ldocs = docs.collect();
			List<KeyedMessage<String, SolrInputDocument>> lkm = new ArrayList<KeyedMessage<String, SolrInputDocument>>();
			for (SolrInputDocument s : ldocs)
				//lkm.add(new KeyedMessage<String, SolrInputDocument>(topic, s));
				lkm.add(new KeyedMessage<String, SolrInputDocument>(null, s));
			producer.send(lkm);
			if (debugOption)
				System.out.println("Sent " + lkm.size() + " messages");
		}
		return null;
	}
}
