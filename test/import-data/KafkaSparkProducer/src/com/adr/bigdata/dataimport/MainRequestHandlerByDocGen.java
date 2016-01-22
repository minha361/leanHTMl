package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;
import com.adr.bigdata.dataimport.utils2.Constants;
import com.lucidworks.spark.SolrSupport;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.utils.FileSystemUtils;

public class MainRequestHandlerByDocGen extends BaseRequestHandler implements
		Serializable {
	private static final long serialVersionUID = -3708134032694560594L;
	private String propFile;
	private Properties prop;

	@Override
	public void init(PuObjectRO initParams) {
		String propFilePath = FileSystemUtils.createPathFrom(
				FileSystemUtils.getBasePathForClass(MainRequestHandler.class),
				"conf", "log.properties");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MainRequestHandlerByDocGen() {

	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		String master = Constants.SPARK_MASTER;
		SparkConf sparkConf = new SparkConf().setMaster(master);
		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		DocGenerationJob job = DocGenerationJobFactory
				.getDocGenerationJob(type);
		JavaRDD<SolrInputDocument> docs = job.genDocuments(jsc);
		String zkHost = prop.getProperty("solrZKConnect");
		String collection = prop.getProperty("collection");
		int batchSize = Integer.valueOf(prop.getProperty("solrBatchSize"));
		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		return null;
	}
}
