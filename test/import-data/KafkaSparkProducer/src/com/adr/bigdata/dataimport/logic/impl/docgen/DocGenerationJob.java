package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.IOException;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import com.adr.bigdata.dataimport.system.ExternalSources;
import scala.Tuple2;

public interface DocGenerationJob {
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext conf) throws ParseException, Exception;
	public void setByteArray(byte[] ba);
	public void byteArrayToPuObject() throws IOException;
	public JavaRDD<SolrInputDocument> genDocuments(JavaRDD<Tuple2<String,String>> msg);
	public void setDebug(boolean option);
	public void setExternalSources(ExternalSources es);
	public void setCheckingUpdateTime(boolean checking);
}
