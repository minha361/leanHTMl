package com.adr.bigdata.misc;

import java.util.Arrays;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class WordCountExample {
	public static void main(String[] args) {
		String txtFile = args[0];
		JavaSparkContext jsc = new JavaSparkContext("spark://10.220.75.133:7077","test");
		JavaStreamingContext jssc = new JavaStreamingContext(jsc,new Duration(Long.valueOf("10")));
		JavaDStream<String> str = jssc.textFileStream(txtFile);
		
		JavaPairDStream<String,Integer> dat = str.flatMap(new FlatMapFunction<String,String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Iterable<String> call(String arg0) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(arg0.split(" "));
			}
		}).cache().mapToPair(new PairFunction<String,String,Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, Integer> call(String arg0) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String,Integer>(arg0,1);
			}
			
		}).reduceByKey(new Function2<Integer,Integer,Integer>() {

			@Override
			public Integer call(Integer arg0, Integer arg1) throws Exception {
				// TODO Auto-generated method stub
				return arg0 + arg1;
			}
			
		});
		
		jssc.start();              // Start the computation
		jssc.awaitTermination(); 
		dat.saveAsHadoopFiles("wc", "test");
		dat.print();
	}
}
