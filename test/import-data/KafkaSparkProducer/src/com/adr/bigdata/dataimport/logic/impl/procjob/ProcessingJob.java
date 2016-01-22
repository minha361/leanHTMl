package com.adr.bigdata.dataimport.logic.impl.procjob;

import java.util.List;
import java.util.Properties;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

import com.google.gson.JsonObject;

public interface ProcessingJob  {
	public void run(Properties prop, JsonObject brandJsonObj) throws Exception;
	public int run(Properties prop, String brandJsonObj) throws Exception;
	public int run(Properties prop) throws Exception;
	public void setMsg(String str);
	public int run() throws Exception;
	public int run(SparkConf conf) throws Exception;
	//public JavaRDD<SolrInputDocument> makeSolrDoc() ;
/*	public JavaRDD<SolrInputDocument> genDocuments() throws ParseException;*/
	public Tuple2<String,List<Integer>> getDocIDs() throws Exception;
}
