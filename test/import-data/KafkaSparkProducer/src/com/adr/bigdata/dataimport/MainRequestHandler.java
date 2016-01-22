package com.adr.bigdata.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.lucidworks.spark.SolrSupport;
import com.mario.consumer.cache.CacheWrapper;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.utils.FileSystemUtils;
import com.adr.bigdata.dataimport.system.ExternalSources;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;

public class MainRequestHandler extends BaseRequestHandler implements
		Serializable {
	static Logger logger = org.apache.log4j.Logger.getLogger(MainRequestHandler.class);
	
	private static final long serialVersionUID = -3708134032694560594L;
	private String zkHost;
	private String collection;
	private int batchSize;
	private int threshold;
	private String master;
	private SparkConf conf;
	private JavaSparkContext jsc;
	private boolean debugOption;
	private Producer<String, SolrInputDocument> producer;
	private String topic;
	private ProducerConfig pConfig;
	private String dataSourceName;
	private ExternalSources externalSources;
	private boolean checkUpdateTime = true;

	public MainRequestHandler() {
		this.debugOption = false;
		this.producer = null;
	}

	@Override
	public void init(final PuObjectRO initParams) {
		this.dataSourceName = initParams.getString("dataSourceName");
		this.externalSources = ExternalSources.newInstance(this.getApi()
				.getDatabaseAdapter(this.dataSourceName), new CacheWrapper() {
			@Override
			public <K, V> Map<K, V> getCacheMap(String name) {
				return getApi().getCacheMap(name);
			}
		});
		
		this.zkHost = initParams.getString("solrZKHost");
		this.collection = initParams.getString("collection");
		this.batchSize = initParams.getInteger("batchSize");
		this.threshold = initParams.getInteger("threshold");
		this.master = initParams.getString("sparkMaster");
		this.debugOption = initParams.getBoolean("debugOption");
		this.checkUpdateTime = initParams.getBoolean("checkUpdateTime");
		this.topic = initParams.getString("topic");
		final Integer numberOfCores = initParams.getInteger("numberOfCores");
		final String jarFile = FileSystemUtils.createPathFrom(
				FileSystemUtils.getBasePathForClass(MainRequestHandler.class),
				"lib", "kafka-spark-producer-0.0.1-jar-with-dependencies.jar");
		if (this.debugOption) {
			System.out.println("My jar file : " + jarFile);
			logger.info("My jar file : " + jarFile);
		}
		(this.conf = new SparkConf().setMaster(this.master).setAppName(
				"MainRequestHandler")).setJars(new String[] { jarFile });
		this.conf.set("spark.cores.max", String.valueOf(numberOfCores));
		this.jsc = new JavaSparkContext(this.conf);
		final Properties prodConfig = new Properties();
		prodConfig.put("metadata.broker.list",
				initParams.getString("metadata.broker.list"));
		prodConfig.put("serializer.class",
				initParams.getString("serializer.class"));
		if (debugOption) {
		System.out.println("Serializer class : "
				+ initParams.getString("serializer.class"));
		logger.info("Serializer class : "
				+ initParams.getString("serializer.class"));
		}
/*		prodConfig.put("zookeeper.connect",
				initParams.getString("kafka.zk.connect"));*/
		this.pConfig = new ProducerConfig(prodConfig);
		this.producer = new Producer<String, SolrInputDocument>(this.pConfig);
	}

	@Override
	public void destroy() throws Exception {
		if (this.producer != null) {
			this.producer.close();
		}
		this.jsc.close();
	}

	@Override
	public Object onRequest(final int type, final PuObject data)
			throws Exception {
		long checkPoint1 = 0L;
		long checkPoint2 = 0L;
		long checkPoint3 = 0L;
		long checkPoint4 = 0L;
		long checkPoint5 = 0L;
		long checkPoint6 = 0L;
		if (this.debugOption) {
			checkPoint1 = System.currentTimeMillis();
			
		}
		final DocGenerationJob job = DocGenerationJobFactory
				.getDocGenerationJob(type);
		job.setExternalSources(this.externalSources);
		job.setDebug(this.debugOption);
		job.setCheckingUpdateTime(checkUpdateTime);
		
		if (this.debugOption) {
			checkPoint2 = System.currentTimeMillis();
			System.out.println("Setup\t" + type + "\t" + checkPoint1 + "\t"
					+ checkPoint2 + "\t" + (checkPoint2 - checkPoint1));
			logger.info("Setup\t" + type + "\t" + checkPoint1 + "\t"
					+ checkPoint2 + "\t" + (checkPoint2 - checkPoint1));
		}
		final byte[] bytes = data.toMessagePack();
		job.setByteArray(bytes);
		
		if (this.debugOption) {
			System.out.println("Started job type : " + type);
			checkPoint3 = System.currentTimeMillis();
			logger.info("Started job type : " + type);
		}
		final JavaRDD<SolrInputDocument> docs = job.genDocuments(this.jsc)
				.cache();
		if (docs == null) {
			System.out.println("There are no SolrInputDoc to process ...");
			logger.info("There are no SolrInputDoc to process ...");
			return null;
		}
		long docNums = docs.count();
		if (docNums > 0L) {
			if (this.debugOption) {
				checkPoint4 = System.currentTimeMillis();
				System.out.println("Docgen\t" + type + "\t" + checkPoint3
						+ "\t" + checkPoint4 + "\t"
						+ (checkPoint4 - checkPoint3) + "\t" + docNums);
				logger.info("Docgen\t" + type + "\t" + checkPoint3
						+ "\t" + checkPoint4 + "\t"
						+ (checkPoint4 - checkPoint3) + "\t" + docNums);
			}
			if (docNums > this.threshold) {
				SolrSupport.indexDocs2(this.zkHost, this.collection,
						this.batchSize, docs); 
				if (this.debugOption) {
					System.out.println("Indexing done");
					checkPoint5 = System.currentTimeMillis();
					System.out
							.println("IndexingTime\t" + type + "\t"
									+ checkPoint4 + "\t" + checkPoint5 + "\t"
									+ (checkPoint5 - checkPoint4) + "\t"
									+ docNums);
					logger.info("IndexingTime\t" + type + "\t"
							+ checkPoint4 + "\t" + checkPoint5 + "\t"
							+ (checkPoint5 - checkPoint4) + "\t"
							+ docNums);
				}
			} else {
				checkPoint5 = System.currentTimeMillis();
				final List<SolrInputDocument> ldocs = docs.collect();
				final long checkPoint7 = System.currentTimeMillis();
				if (this.debugOption) {
					System.out
							.println("CollectTime\t" + type + "\t"
									+ checkPoint7 + "\t" + checkPoint5 + "\t"
									+ (checkPoint7 - checkPoint5) + "\t"
									+ ldocs.size());
					logger.info("CollectTime\t" + type + "\t"
							+ checkPoint7 + "\t" + checkPoint5 + "\t"
							+ (checkPoint7 - checkPoint5) + "\t"
							+ ldocs.size());
				}
				final List<KeyedMessage<String, SolrInputDocument>> lkm = new ArrayList<KeyedMessage<String, SolrInputDocument>>();
				for (final SolrInputDocument s : ldocs) {
					lkm.add(new KeyedMessage<String, SolrInputDocument>(
							this.topic, null, s));
				}
				this.producer.send(lkm);
				if (this.debugOption) {
					checkPoint6 = System.currentTimeMillis();
					System.out.println("SentMessages\t" + type + "\t"
							+ checkPoint5 + "\t" + checkPoint7 + "\t"
							+ (checkPoint6 - checkPoint7) + "\t" + lkm.size());
					logger.info("SentMessages\t" + type + "\t"
							+ checkPoint5 + "\t" + checkPoint7 + "\t"
							+ (checkPoint6 - checkPoint7) + "\t" + lkm.size());
				}
			}
		}
		return null;
	}

	public ExternalSources getExternalSources() {
		return this.externalSources;
	}

	public void setExternalSources(final ExternalSources externalSources) {
		this.externalSources = externalSources;
	}
}
