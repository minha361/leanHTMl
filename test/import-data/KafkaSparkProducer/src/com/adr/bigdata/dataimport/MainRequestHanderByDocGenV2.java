package com.adr.bigdata.dataimport;

import java.io.Serializable;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;
import com.lucidworks.spark.SolrSupport;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.utils.FileSystemUtils;

public class MainRequestHanderByDocGenV2 extends BaseRequestHandler implements
		Serializable {
	private static final long serialVersionUID = -3708134032694560594L;
	private String zkHost;
	private String collection;
	private int batchSize;
	private String master;
	private SparkConf conf;
	private JavaSparkContext jsc;
	private boolean debugOption = false;

	@Override
	public void init(PuObjectRO initParams) {
		zkHost = initParams.getString("zkHost");
		collection = initParams.getString("collection");
		batchSize = initParams.getInteger("batchSize");
		master = initParams.getString("sparkMaster");
		debugOption = initParams.getBoolean("debugOption");

		String jarFile = FileSystemUtils.createPathFrom(
				FileSystemUtils.getBasePathForClass(MainRequestHandler.class),
				"lib", "kafka-spark-producer-0.0.1-jar-with-dependencies.jar");

		conf = new SparkConf().setMaster(master).setAppName(
				"MainRequestHandler");
		conf.setJars(new String[] { jarFile });
		jsc = new JavaSparkContext(conf);
	}

	public void destroy() throws Exception {
		jsc.close();
	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {

		DocGenerationJob job = DocGenerationJobFactory
				.getDocGenerationJob(type);
		byte[] bytes = data.toMessagePack();
		job.setByteArray(bytes);

		long startTime = 0, endTime = 0, endIndexingTime = 0;
		if (debugOption) {
			System.out.println("Started job type : " + type);
			startTime = System.currentTimeMillis();
		}
		JavaRDD<SolrInputDocument> docs = job.genDocuments(jsc);
		if (docs.count() > 0) {
			if (debugOption) {
				System.out.println("Number of docs : " + docs.count());
				System.out.println(zkHost + "\t" + collection + "\t"
						+ batchSize);
				endTime = System.currentTimeMillis();
				System.out.println("Gendoc : " + startTime + "\t" + endTime
						+ "\t" + (endTime - startTime) + "\t" + docs.count());
			}
			SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);

			if (debugOption) {
				System.out.println("Indexing done");
				endIndexingTime = System.currentTimeMillis();
				System.out.println("Indexing : " + endTime + "\t"
						+ endIndexingTime + "\t" + (endIndexingTime - endTime)
						+ "\t" + docs.count());
			}
		}

		return null;
	}
}
