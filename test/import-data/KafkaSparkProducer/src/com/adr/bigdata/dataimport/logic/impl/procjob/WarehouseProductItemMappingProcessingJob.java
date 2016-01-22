package com.adr.bigdata.dataimport.logic.impl.procjob;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.SparkConf;

import scala.Tuple2;

import com.adr.bigdata.dataimport.utils2.Constants;
import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.adr.bigdata.indexing.db.sql.models.WarehouseProductItemMappingModel;
import com.google.gson.JsonObject;
import com.lucidworks.spark.SolrSupport;
import com.nhb.common.data.PuObject;

public class WarehouseProductItemMappingProcessingJob extends CachedModel
		implements Serializable, ProcessingJob {

	/**
	 * 
	 */
	org.slf4j.Logger sLogger = org.slf4j.LoggerFactory.getLogger("kafka-consumer");
	
	private static final long serialVersionUID = 6781642970604049456L;
	private String msg;

	public void setMsg(String str) {
		msg = str;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public void run(Properties prop, JsonObject brandJsonObj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int run(Properties prop, String brandJsonObj) throws Exception {
		return numDocPerRequest;
		// TODO Auto-generated method stub

	}

	@Override
	public int run(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public int run() throws Exception {
		// TODO Auto-generated method stub
		System.err.println("Ware house product item begins...");
		String master = Constants.SPARK_MASTER;
		SparkConf conf = new SparkConf().setMaster(master).setAppName("WarehouseProductItemMappingProcessingJob");
		JavaSparkContext jsc = new JavaSparkContext(conf);

		// process warehouse product_item mapping event
		PuObject originalMsg = new PuObject();
		originalMsg.fromJSON(msg);
		Collection<PuObject> warehouseProductItemMappings = originalMsg
				.getPuObjectArray(APIFields.LIST);
		getLogger().info("Collection size : " + warehouseProductItemMappings.size());
		System.out.println("Collection size : " + warehouseProductItemMappings.size());
		
		// Filtering function
		class MessageFilter implements Function<PuObject, Boolean> {

			@Override
			public Boolean call(PuObject warehouseProductItemMapping)
					throws Exception {
				// TODO Auto-generated method stub
				Integer whpimId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID);
				if (whpimId == null)
					return false;
				Integer whId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID);
				Integer piId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID);
				Integer mcId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID);
				Integer quantity = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY);
				String mcSKU = warehouseProductItemMapping
						.getString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU);
				Double sellPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE);
				Double oriPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE);
				Integer mcPIStatus = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS);
				Integer safetyStock = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK);
				Long updateTime = warehouseProductItemMapping
						.getLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME);
				Integer isVisble = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE);
				if (whId == null || piId == null || mcId == null
						|| quantity == null || mcSKU == null
						|| sellPrice == null || oriPrice == null
						|| mcPIStatus == null || safetyStock == null
						|| updateTime == null || isVisble == null) {
					return false;
				}

				return true;
			}

		}

		class UpdateTimeFilter implements Function<PuObject, Boolean> {

			@Override
			public Boolean call(PuObject arg0) throws Exception {
				// TODO Auto-generated method stub
				return true;
			}

		}

		JavaRDD<PuObject> filteredMsg = jsc
				.parallelize(
						new ArrayList<PuObject>(warehouseProductItemMappings))
				.filter(new MessageFilter()).filter(new UpdateTimeFilter());
		
		getLogger().info("Collection size after filtering : " + filteredMsg.count());
		System.out.println("Collection size after filtering : " + filteredMsg.count());
		
		class CreateSolrDoc implements Function<PuObject, SolrInputDocument> {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3119550600378809581L;

			@Override
			public SolrInputDocument call(PuObject warehouseProductItemMapping) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				Integer whpimId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID);
				Integer whId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID);
				Integer piId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID);
				Integer mcId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID);
				Integer quantity = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY);
				String mcSKU = warehouseProductItemMapping
						.getString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU);
				Double sellPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE);
				Double oriPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE);
				Integer mcPIStatus = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS);
				Integer safetyStock = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK);
				Long updateTime = warehouseProductItemMapping
						.getLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME);
				Integer isVisble = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE);
				
				WarehouseProductItemMappingModel md= new WarehouseProductItemMappingModel();
				Method med = WarehouseProductItemMappingModel.class.getDeclaredMethod("createSolrDocument");
				med.setAccessible(true);
				return (SolrInputDocument) med.invoke(md,whpimId, whId, piId, mcId, quantity,
						mcSKU, sellPrice, oriPrice, mcPIStatus, safetyStock,
						isVisble);

			}
		}
		
		JavaRDD<SolrInputDocument> docs = filteredMsg.map(new CreateSolrDoc());
		getLogger().info("Number of SolrInputDocument : " + docs.count());
		String zkHost = Constants.SOLR_CLOUD_ZKHOST;
		String collection = Constants.COLLECTION;
		int batchSize = Integer.valueOf(Constants.BATCH_SIZE);

		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		getLogger().info("Indexing to SolrCloud done");
		System.out.println("Indexing to SolrCloud done");
		System.err.println("Ware house product item done...");
		return 0; // success
	}
	
	public int run(SparkConf conf) throws Exception {
		// TODO Auto-generated method stub
		//String master = Constants.SPARK_MASTER;
		//SparkConf conf = new SparkConf().setMaster(master).setAppName("WarehouseProductItemMappingProcessingJob");
		conf.setAppName("WarehouseProductItemMappingProcessingJob");
		JavaSparkContext jsc = new JavaSparkContext(conf);

		// process warehouse product_item mapping event
		PuObject originalMsg = new PuObject();
		originalMsg.fromJSON(msg);
		Collection<PuObject> warehouseProductItemMappings = originalMsg
				.getPuObjectArray(APIFields.LIST);
		getLogger().info("Collection size : " + warehouseProductItemMappings.size());
		System.out.println("Collection size : " + warehouseProductItemMappings.size());
		
		// Filtering function
		class MessageFilter implements Function<PuObject, Boolean> {

			@Override
			public Boolean call(PuObject warehouseProductItemMapping)
					throws Exception {
				// TODO Auto-generated method stub
				Integer whpimId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID);
				if (whpimId == null)
					return false;
				Integer whId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID);
				Integer piId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID);
				Integer mcId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID);
				Integer quantity = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY);
				String mcSKU = warehouseProductItemMapping
						.getString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU);
				Double sellPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE);
				Double oriPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE);
				Integer mcPIStatus = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS);
				Integer safetyStock = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK);
				Long updateTime = warehouseProductItemMapping
						.getLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME);
				Integer isVisble = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE);
				if (whId == null || piId == null || mcId == null
						|| quantity == null || mcSKU == null
						|| sellPrice == null || oriPrice == null
						|| mcPIStatus == null || safetyStock == null
						|| updateTime == null || isVisble == null) {
					return false;
				}

				return true;
			}

		}

		class UpdateTimeFilter implements Function<PuObject, Boolean> {

			@Override
			public Boolean call(PuObject arg0) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

		}

		JavaRDD<PuObject> filteredMsg = jsc
				.parallelize(
						new ArrayList<PuObject>(warehouseProductItemMappings))
				.filter(new MessageFilter()).filter(new UpdateTimeFilter());
		
		getLogger().info("Collection size after filtering : " + filteredMsg.count());
		System.out.println("Collection size after filtering : " + filteredMsg.count());
		
		class CreateSolrDoc implements Function<PuObject, SolrInputDocument> {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3119550600378809581L;

			@Override
			public SolrInputDocument call(PuObject warehouseProductItemMapping) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				Integer whpimId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID);
				Integer whId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID);
				Integer piId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID);
				Integer mcId = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID);
				Integer quantity = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY);
				String mcSKU = warehouseProductItemMapping
						.getString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU);
				Double sellPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE);
				Double oriPrice = warehouseProductItemMapping
						.getDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE);
				Integer mcPIStatus = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS);
				Integer safetyStock = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK);
				Long updateTime = warehouseProductItemMapping
						.getLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME);
				Integer isVisble = warehouseProductItemMapping
						.getInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE);
				
				WarehouseProductItemMappingModel md= new WarehouseProductItemMappingModel();
				Method med = WarehouseProductItemMappingModel.class.getDeclaredMethod("createSolrDocument");
				med.setAccessible(true);
				return (SolrInputDocument) med.invoke(md,whpimId, whId, piId, mcId, quantity,
						mcSKU, sellPrice, oriPrice, mcPIStatus, safetyStock,
						isVisble);

			}
		}
		
		JavaRDD<SolrInputDocument> docs = filteredMsg.map(new CreateSolrDoc());
		getLogger().info("Number of SolrInputDocument : " + docs.count());
		String zkHost = Constants.SOLR_CLOUD_ZKHOST;
		String collection = Constants.COLLECTION;
		int batchSize = Integer.valueOf(Constants.BATCH_SIZE);

		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		getLogger().info("Indexing to SolrCloud done");
		System.out.println("Indexing to SolrCloud done");
		return 0; // success
	}

	@Override
	public Tuple2<String, List<Integer>> getDocIDs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
