package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.db.sql.models.WarehouseProductItemMappingModel;
import com.nhb.common.data.PuObject;
import  com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.WarehouseProductItemMappingSolrDocCreator;

public class WarehouseProductItemMappingDocGenerationJob extends
		DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws ParseException, IOException {
		byteArrayToPuObject();
		Collection<PuObject> warehouseProductItemMappings = getPuObject()
				.getPuObjectArray(APIFields.LIST);

/*		
		System.out.println("Collection size : "
				+ warehouseProductItemMappings.size());*/

		JavaRDD<PuObject> filteredMsg = jsc
				.parallelize(
						new ArrayList<PuObject>(warehouseProductItemMappings))
				.filter(new Function<PuObject, Boolean>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 2994016684134399363L;

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
				}).filter(new Function<PuObject, Boolean>() {
					private static final long serialVersionUID = 9007494845548328403L;

					@Override
					public Boolean call(PuObject arg0) throws Exception {
						// TODO Auto-generated method stub
						return true;
					}
				});

/*		getLogger().info(
				"Collection size after filtering : " + filteredMsg.count());
		System.out.println("Collection size after filtering : "
				+ filteredMsg.count());
*/
		
		JavaRDD<SolrInputDocument> docs = filteredMsg.map(new Function<PuObject,SolrInputDocument>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3119550600378809581L;

			@Override
			public SolrInputDocument call(PuObject warehouseProductItemMapping)
					throws NoSuchMethodException, SecurityException,
					IllegalAccessException, IllegalArgumentException,
					InvocationTargetException {
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

				WarehouseProductItemMappingSolrDocCreator md = new WarehouseProductItemMappingSolrDocCreator();

				return md.createSolrDocument(whpimId, whId, piId,
						mcId, quantity, mcSKU, sellPrice, oriPrice, mcPIStatus,
						safetyStock, isVisble);

			}
		});
		
/*		System.out.println("Number of SolrDoc returned : "
				+ docs.count());*/
		
		return docs; // success
	}

}
