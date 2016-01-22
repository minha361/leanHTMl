package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.nhb.common.data.PuObject;
import com.adr.bigdata.indexing.db.sql.models.PromotionProductItemMappingModel;

public class PromotionProductItemMappingDocGenerationJob extends
		DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws IOException {
		byteArrayToPuObject();
		Collection<PuObject> col = getPuObject().getPuObjectArray(
				APIFields.LIST);
		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				new ArrayList<PuObject>(col)).map(
				new Function<PuObject, SolrInputDocument>() {

					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(
							PuObject promotionProductItemMappingModel)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						Integer warehouseProductItemMappingId = promotionProductItemMappingModel
								.getInteger(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_ID);
						if (warehouseProductItemMappingId == null) {
							return null;
						}

						Integer status = promotionProductItemMappingModel
								.getInteger(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_STATUS);
						Long updateTime = promotionProductItemMappingModel
								.getLong(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_UPDATE_TIME);
						if (status == null || updateTime == null) {
							getLogger().error("missing som fields");
							return null;
						}

						/* Check cache */
						if (getCheckingUpdateTime()) {
							try {
								Long lastUpdateTime = (Long) getCacheWrapper()
										.getCacheMap(
												CacheFields.PROMOTION_PRODUCT_ITEM_MAPPING)
										.get(warehouseProductItemMappingId);
								if (lastUpdateTime != null
										&& lastUpdateTime >= updateTime) {
									getLogger()
											.info("promotion of warehouseProductItemMappingId: "
													+ warehouseProductItemMappingId
													+ " has been update by a newer");
									return null;
								} else {
									/*
									 * updatedWarehouseProductItemMappingIds.add(
									 * warehouseProductItemMappingId);
									 * updatedTimes.add(updateTime);
									 */
								}
							} catch (Exception e) {
								getLogger().error("", e);
							}
						}
						/* End: Check cache */

						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								warehouseProductItemMappingId);

						if (status != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", status == 1);
							doc.addField(SolrFields.IS_PROMOTION_MAPPING, fs);
						}
						// Update time into cache
						return doc;
					}
				});
		return docs;
	}

}
