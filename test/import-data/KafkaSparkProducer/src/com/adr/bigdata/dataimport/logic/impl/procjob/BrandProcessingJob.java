package com.adr.bigdata.dataimport.logic.impl.procjob;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

import com.adr.bigdata.dataimport.utils2.Constants;
import com.adr.bigdata.dataimport.model.converter.BrandPuObjectToJsonConverter;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.BrandModel;
import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.google.gson.JsonObject;
import com.lucidworks.spark.SolrSupport;
import com.nhb.common.data.PuObject;

public class BrandProcessingJob extends CachedModel implements Serializable,
		ProcessingJob {
	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(BrandProcessingJob.class);
	
	private static final long serialVersionUID = 2382501080306299531L;
	
	private String msg;
		
	public void setMsg(String str) {
		msg = str;
	}

	public String getMsg() {
		return msg;
	}
	
	public void run(Properties prop, JsonObject brandJsonObj)
			throws Exception {
		SparkConf conf = new SparkConf().setAppName("BrandProcessingJob");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		// Process Brand update
		BrandPuObjectToJsonConverter converter = new BrandPuObjectToJsonConverter();
		BrandBean bean = converter.convert(brandJsonObj);
		Integer brandId = bean.getId();
		String brandName = bean.getName();
		Integer brandStatus = bean.getStatus();
		long updateTime = bean.getUpdateTime();
		// Check update time
		/* Check cache */
		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				logger.info(
						"brandId: " + brandId + " has been updated by a newer");
				System.out.println("brandId: " + brandId + " has been updated by a newer");
				return;
			}
		} catch (Exception e) {
			logger.error(e);
		}

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			logger.error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			System.out.println(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return;
		}
		/* End: Get warehouseProductItemIds from DB */
		logger.info("BrandId = " + brandId);
		logger.info("Number of warehouseProductItemmappingIds  returned : " + warehouseProductItemMappingIds.size());
		System.out.println("BrandId = " + brandId);
		System.out.println("Number of warehouseProductItemmappingIds  returned : " + warehouseProductItemMappingIds.size());
		
		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemMappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (brandStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandStatus == 1);
							doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
						}
						if (brandName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandName);
							doc.addField(SolrFields.BRAND_NAME, fs);
						}
						return doc;
					}
				});
		
		logger.info("Number of SolrInputDocument returned : " + docs.count());
		System.out.println("Number of SolrInputDocument returned : " + docs.count());
		
		// Parallel process
		String zkHost = prop.getProperty("zkHost");
		String collection = prop.getProperty("collection");
		int batchSize = Integer.valueOf(prop.getProperty("batchDuration"));
		//SolrSupport.indexDocs(zkHost, collection, batchSize, docs);
		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		jsc.close();
	}

	public int run(Properties prop, String puObjectStr)
			throws Exception {
		SparkConf conf = new SparkConf();
		JavaSparkContext jsc = new JavaSparkContext(conf);
		// Process Brand update
		PuObject puObject = new PuObject();
		puObject.fromJSON(puObjectStr);

		int brandId = puObject.getInteger(BrandModel.BRAND_ID);
		String brandName = puObject.getString(BrandModel.BRAND_NAME);
		Integer brandStatus = puObject.getInteger(BrandModel.BRAND_STATUS);
		long updateTime = puObject.getInteger(BrandModel.BRAND_UPDATE_TIME);

		/* Check cache */
		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				getLogger().info(
						"brandId: " + brandId + " has been updated by a newer");
				return -2; // already processed
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return -1; // errors
		}
		/* End: Get warehouseProductItemIds from DB */

		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemMappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = -1353799806932347783L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (brandStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandStatus == 1);
							doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
						}
						if (brandName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandName);
							doc.addField(SolrFields.BRAND_NAME, fs);
						}
						return doc;
					}
				});

		// Parallel process
		String zkHost = prop.getProperty("zkHost");
		String collection = prop.getProperty("collection");
		int batchSize = Integer.valueOf(prop.getProperty("batchDuration"));
		SolrSupport.indexDocs(zkHost, collection, batchSize, docs);
		return 0; // success
	}
	
	public int run(Properties prop)
			throws Exception {
		SparkConf conf = new SparkConf();
		JavaSparkContext jsc = new JavaSparkContext(conf);
		// Process Brand update
		PuObject puObject = new PuObject();
		puObject.fromJSON(this.msg);

		int brandId = puObject.getInteger(BrandModel.BRAND_ID);
		String brandName = puObject.getString(BrandModel.BRAND_NAME);
		Integer brandStatus = puObject.getInteger(BrandModel.BRAND_STATUS);
		long updateTime = puObject.getInteger(BrandModel.BRAND_UPDATE_TIME);

		/* Check cache */
/*		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				getLogger().info(
						"brandId: " + brandId + " has been updated by a newer");
				return -2; // already processed
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
*/
		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return -1; // errors
		}
		/* End: Get warehouseProductItemIds from DB */

		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemMappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = -1353799806932347783L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (brandStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandStatus == 1);
							doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
						}
						if (brandName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandName);
							doc.addField(SolrFields.BRAND_NAME, fs);
						}
						return doc;
					}
				});

		// Parallel process
		String zkHost = prop.getProperty("zkHost");
		String collection = prop.getProperty("collection");
		int batchSize = Integer.valueOf(prop.getProperty("batchDuration"));
		SolrSupport.indexDocs(zkHost, collection, batchSize, docs);
		jsc.close();
		return 0; // success
	}
	
	public int run()
			throws Exception {
		System.err.println("Brand processing begins...");
		String master = Constants.SPARK_MASTER;
		SparkConf conf = new SparkConf().setMaster(master).setAppName("BrandProcessingJob");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		// Process Brand update
		PuObject puObject = new PuObject();
		puObject.fromJSON(this.msg);
		System.err.println("Brand processing begins...2");
		int brandId = puObject.getInteger(BrandModel.BRAND_ID);
		String brandName = puObject.getString(BrandModel.BRAND_NAME);
		Integer brandStatus = puObject.getInteger(BrandModel.BRAND_STATUS);
		long updateTime = puObject.getInteger(BrandModel.BRAND_UPDATE_TIME);

		/* Check cache */
		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				getLogger().info(
						"brandId: " + brandId + " has been updated by a newer");
				return -2; // already processed
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}

		System.err.println("Brand processing begins...3");
		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return -1; // errors
		}
		/* End: Get warehouseProductItemIds from DB */
		System.err.println("Brand processing begins...4");
		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemMappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = -1353799806932347783L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (brandStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandStatus == 1);
							doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
						}
						if (brandName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandName);
							doc.addField(SolrFields.BRAND_NAME, fs);
						}
						return doc;
					}
				});
		
		System.err.println("Number of solr doc = " + docs.count());
		System.err.println("Brand processing begins...5");

		String zkHost = Constants.SOLR_CLOUD_ZKHOST;
		String collection = Constants.COLLECTION;
		int batchSize = Integer.valueOf(Constants.BATCH_SIZE);
		
		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		System.err.println("Brand processing is done...");
		return 0; // success
	}
	
	public int run(SparkConf conf)
			throws Exception {
		//String master = Constants.SPARK_MASTER;
		//SparkConf conf = new SparkConf().setMaster(master).setAppName("BrandProcessingJob");
		conf.setAppName("BrandProcessingJob");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		// Process Brand update
		PuObject puObject = new PuObject();
		puObject.fromJSON(this.msg);

		int brandId = puObject.getInteger(BrandModel.BRAND_ID);
		String brandName = puObject.getString(BrandModel.BRAND_NAME);
		Integer brandStatus = puObject.getInteger(BrandModel.BRAND_STATUS);
		long updateTime = puObject.getInteger(BrandModel.BRAND_UPDATE_TIME);

		/* Check cache */
		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				getLogger().info(
						"brandId: " + brandId + " has been updated by a newer");
				return -2; // already processed
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return -1; // errors
		}
		/* End: Get warehouseProductItemIds from DB */

		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemMappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = -1353799806932347783L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (brandStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandStatus == 1);
							doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
						}
						if (brandName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", brandName);
							doc.addField(SolrFields.BRAND_NAME, fs);
						}
						return doc;
					}
				});

		String zkHost = Constants.SOLR_CLOUD_ZKHOST;
		String collection = Constants.COLLECTION;
		int batchSize = Integer.valueOf(Constants.BATCH_SIZE);
		
		SolrSupport.indexDocs2(zkHost, collection, batchSize, docs);
		jsc.close();
		return 0; // success
	}

	@Override
	public Tuple2<String, List<Integer>> getDocIDs() throws Exception {
		PuObject puObject = new PuObject();
		puObject.fromJSON(this.msg);
		System.err.println("Brand processing begins...2");
		int brandId = puObject.getInteger(BrandModel.BRAND_ID);
		String brandName = puObject.getString(BrandModel.BRAND_NAME);
		Integer brandStatus = puObject.getInteger(BrandModel.BRAND_STATUS);
		long updateTime = puObject.getInteger(BrandModel.BRAND_UPDATE_TIME);

		/* Check cache */
		try {
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				getLogger().info(
						"brandId: " + brandId + " has been updated by a newer");
				return null; // already processed
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}

		System.err.println("Brand processing begins...3");
		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (warehouseProductItemMappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with brandId: "
							+ brandId);
			return null; // errors
		}
		return new Tuple2<String,List<Integer>>("brand",warehouseProductItemMappingIds);
	}
	
	
}
