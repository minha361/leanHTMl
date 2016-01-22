package com.adr.bigdata.dataimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.lucidworks.spark.SolrSupport;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.common.utils.FileSystemUtils;
import com.adr.bigdata.dataimport.utils2.Constants;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJob;
import com.adr.bigdata.dataimport.logic.impl.docgen.DocGenerationJobFactory;
import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.db.sql.models.AttributeModel;
import com.adr.bigdata.indexing.db.sql.models.AttributeValueModel;
import com.adr.bigdata.indexing.db.sql.models.BrandModel;
import com.adr.bigdata.indexing.db.sql.models.CategoryModel;
import com.adr.bigdata.indexing.db.sql.models.MerchantModel;
import com.adr.bigdata.indexing.db.sql.models.ProductItemModel;
import com.adr.bigdata.indexing.db.sql.models.PromotionModel;
import com.adr.bigdata.indexing.db.sql.models.PromotionProductItemMappingModel;
import com.adr.bigdata.indexing.db.sql.models.WarehouseModel;
import com.adr.bigdata.indexing.db.sql.models.WarehouseProductItemMappingModel;

public class MainRequestHandlerByKafka  extends BaseRequestHandler implements
		Serializable {
	private static final long serialVersionUID = -3708134032694560594L;
	private String propFile;
	// private Producer<String, JsonObject> producer;
	private Producer<String, String> producer;

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

		Properties prodConfig = new Properties();
		prodConfig.put("metadata.broker.list",
				prop.getProperty("metadata.broker.list"));

		prodConfig.put("broker.list", prop.getProperty("metadata.broker.list"));

		prodConfig
				.put("serializer.class", prop.getProperty("serializer.class"));

		prodConfig.put("'advertised.host.name",
				prop.getProperty("metadata.broker.list"));

		prodConfig.put("zk.connect", prop.getProperty("zk.connect"));

		prodConfig.put("socket.timeout.ms",
				prop.getProperty("socket.timeout.ms"));

		/*
		 * prodConfig.put("partitioner.class",
		 * prop.getProperty("partitioner.class"));
		 */

		prodConfig.put("request.required.acks",
				prop.getProperty("request.required.acks"));

		ProducerConfig pConfig = new ProducerConfig(prodConfig);
		// producer = new Producer<String, JsonObject>(pConfig);
		producer = new Producer<String, String>(pConfig);
		System.out.println("Kafka setup complete ...");
	}

	public MainRequestHandlerByKafka() {

	}

	@Override
	public void destroy() throws Exception {
		producer.close();
	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		String str = data.toJSON();
		Collection<PuObject> col;
		String topic = String.valueOf(type);
		System.out.println("Type : " + type);
		String key;
		switch (type) {
		case 1:
			key = data.getString(BrandModel.BRAND_UPDATE_TIME);
			topic = Constants.BRAND_TOPIC;
			break;
		case 2:
			key = data.getString(CategoryModel.CAT_UPDATE_TIME);
			topic = Constants.CATEGORY_TOPIC;
			break;
		case 3:
			key = data.getString(AttributeModel.ATT_UPDATE_TIME);
			topic = Constants.ATT_TOPIC;
			break;
		case 4:
			key = data.getString(AttributeValueModel.ATTV_UPDATE_TIME);
			topic = Constants.ATT_VALUE_TOPIC;
			break;
		case 5:
			key = data.getString(MerchantModel.MC_UPDATE_TIME);
			topic = Constants.MERCHANT_TOPIC;
			break;
		case 6:
			key = data.getString(WarehouseModel.WH_UPDATE_TIME);
			topic = Constants.WAREHOUSE_TOPIC;
			break;
		case 7:
			key = data.getString(PromotionModel.PM_UPDATE_TIME);
			topic = Constants.PROMOTION_TOPIC;
			break;
		case 8:
			col = data.getPuObjectArray(APIFields.LIST);
			key = col
					.iterator()
					.next()
					.getString(
							PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_UPDATE_TIME);
			topic = Constants.PROMOTION_PRODUCTITEM_TOPIC;
			break;
		case 9:
			col = data.getPuObjectArray(APIFields.LIST);
			key = col
					.iterator()
					.next()
					.getString(
							WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME);
			topic = Constants.WAREHOUSE_PRODUCTITEM_TOPIC;
			break;
		case 10:
			col = data.getPuObjectArray(APIFields.LIST);
			key = col.iterator().next()
					.getString(ProductItemModel.PI_UPDATE_TIME);
			topic = Constants.PRODUCT_ITEM_TOPIC;
			break;
		default:
			throw new IllegalArgumentException("Type not supported " + type);
		}

		if (key != null) {
			String sentKey = key + com.adr.bigdata.dataimport.utils2.Constants.SEP + topic;
			System.out.println(sentKey + "\t" + str);
			producer.send(new KeyedMessage<String, String>(topic, sentKey, str));
			// System.out.println("sent --- type : " + type + " msg : " + str);
		}
		return null;
	}
}
