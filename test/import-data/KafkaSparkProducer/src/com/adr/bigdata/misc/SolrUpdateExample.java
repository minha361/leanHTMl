package com.adr.bigdata.misc;

import com.adr.bigdata.indexing.SolrFields;
import com.lucidworks.spark.SolrRDD;
import com.lucidworks.spark.SolrSupport;
import com.lucidworks.spark.SparkApp;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of how to query Solr and process the result set as a Spark RDD
 */
public class SolrUpdateExample implements SparkApp.RDDProcessor {

	// private Logger logger = Logger.getLogger(SolrQueryProcessor.class);

	private static final class WordCountSorter implements
			Comparator<Tuple2<String, Integer>>, Serializable {
		public int compare(Tuple2<String, Integer> o1,
				Tuple2<String, Integer> o2) {
			Integer lhs = o1._2;
			Integer rhs = o2._2;
			return (lhs == rhs) ? (o1._1.compareTo(o2._1)) : (lhs > rhs ? 1
					: -1);
		}
	}

	public String getName() {
		return "update-solr";
	}

	public Option[] getOptions() {
		return new Option[] { OptionBuilder.withArgName("QUERY").hasArg()
				.isRequired(false)
				.withDescription("URL encoded Solr query to send to Solr")
				.create("query") };
	}

	public int run(SparkConf conf, CommandLine cli) throws Exception {

		String zkHost = cli.getOptionValue("zkHost", "localhost:9983");
		String collection = cli.getOptionValue("collection", "collection1");
		String queryStr = cli.getOptionValue("query", "*:*");

		JavaSparkContext jsc = new JavaSparkContext(conf);

		// TODO: Would be better to accept a JSON representation of a SolrQuery
		final SolrQuery solrQuery = new SolrQuery(queryStr);
		// solrQuery.setFields("text_t","type_s");
		solrQuery.setFields("product_item_id", "product_id",
				"product_item_id_warehouse_id", "product_item_name",
				"merchant_name","is_brand_active");

		List<SolrQuery.SortClause> sorts = new ArrayList<SolrQuery.SortClause>();
		// sorts.add(new SolrQuery.SortClause("id", "asc"));
		sorts.add(new SolrQuery.SortClause("product_item_id", "asc"));
		// sorts.add(new SolrQuery.SortClause("created_at_tdt", "asc"));
		sorts.add(new SolrQuery.SortClause("product_item_id_warehouse_id",
				"asc"));
		solrQuery.setSorts(sorts);

		SolrRDD solrRDD = new SolrRDD(zkHost, collection);

		JavaRDD<SolrDocument> solrJavaRDD = solrRDD.queryShards(jsc, solrQuery);
		
		JavaRDD<SolrInputDocument> docs = solrJavaRDD.map(new Function<SolrDocument,SolrInputDocument>() {

			@Override
			public SolrInputDocument call(SolrDocument arg0) throws Exception {
				// TODO Auto-generated method stub
				String id = (String) arg0.get("product_item_id_warehouse_id");
				Integer status = (Integer) arg0.get("is_brand_active");
				
				SolrInputDocument nd = new SolrInputDocument();
				
				nd.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,id);
						
				Map<String, Object> fs = new HashMap<String, Object>();
				fs.put("set", status == 0);
				nd.addField(SolrFields.IS_BRAND_ACTIVE, fs);
				return nd;
			}
			
		});
		
		// Parallel process
		SolrSupport.indexDocs(zkHost, collection,5, docs);
		
		jsc.stop();

		return 0;
	}
}
