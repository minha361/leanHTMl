package com.adr.bigdata.misc;

import java.util.Arrays;

import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;

import scala.Tuple2;

public class StreamingTest {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("spark://10.220.75.133:7077").setAppName("NetworkWordCount");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(Long.valueOf("10")));
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
		
		JavaDStream<String> words = lines.flatMap(
				  new FlatMapFunction<String, String>() {
				    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override public Iterable<String> call(String x) {
				      return Arrays.asList(x.split(" "));
				    }
				  });
		
		// Count each word in each batch
		JavaPairDStream<String, Integer> pairs = words.mapToPair(
		  new PairFunction<String, String, Integer>() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public Tuple2<String, Integer> call(String s) throws Exception {
		      return new Tuple2<String, Integer>(s, 1);
		    }
		  });
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
		  new Function2<Integer, Integer, Integer>() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public Integer call(Integer i1, Integer i2) throws Exception {
		      return i1 + i2;
		    }
		  });

		// Print the first ten elements of each RDD generated in this DStream to the console
		wordCounts.print();
		
		jssc.start();              // Start the computation
		jssc.awaitTermination();   // Wait for the computation to terminate
	}
}
