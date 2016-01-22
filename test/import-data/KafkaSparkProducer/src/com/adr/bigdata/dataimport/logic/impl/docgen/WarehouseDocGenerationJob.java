package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;

public class WarehouseDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4104836342392099867L;

	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws Exception {
		byteArrayToPuObject();
		Integer warehouseId = getPuObject().getInteger(
				com.adr.bigdata.indexing.db.sql.models.WarehouseModel.WH_ID);
		if (warehouseId == null || warehouseId <= 0) {
			return null;
		}

		Integer whStatus = getPuObject()
				.getInteger(
						com.adr.bigdata.indexing.db.sql.models.WarehouseModel.WH_STATUS);
		Integer whCityId = getPuObject()
				.getInteger(
						com.adr.bigdata.indexing.db.sql.models.WarehouseModel.WH_CITY_ID);
		Long updateTime = getPuObject()
				.getLong(
						com.adr.bigdata.indexing.db.sql.models.WarehouseModel.WH_UPDATE_TIME);

		if (whStatus == null || whCityId == null || updateTime == null) {
			getLogger().error("missing fields");
			return null;
		}

		/* Check cache */
		if (getCheckingUpdateTime()) {
			try {
				WarehouseBean warehouseBean = (WarehouseBean) getCacheWrapper()
						.getCacheMap(CacheFields.WAREHOUSE).get(warehouseId);
				if (warehouseBean != null
						&& warehouseBean.getUpdateTime() >= updateTime) {
					getLogger().info(
							"warehouseId: " + warehouseId
									+ " has been updated by a newer");
					return null;
				}
				if (warehouseBean == null) {
					System.out.println("Warehouse " + warehouseId
							+ " is not in the cache.");
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* End: Check cache */

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemmappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemmappingIds = dao.getByWarehouseId(warehouseId);
		}
		if (warehouseProductItemmappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with warehouseId: "
							+ warehouseId);
			return null;
		}
		/* End: Get warehouseProductItemIds from DB */

		// Parallellize
		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemmappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);
						if (whStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", whStatus == 1);
							doc.addField(SolrFields.IS_WAREHOUSE_ACTIVE, fs);
						}
						if (whCityId != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", Arrays.asList(whCityId, 0));
							// yet to process received_city_id by productItemType
							doc.addField("received_city_id", fs);
							//doc.addField(SolrFields.CITY_ID_FACET, fs);
						}
						
						
/*						if (warehouseCityId != null) {
							if (productItemType == 2 || productItemType == 4) {
								doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(warehouseCityId, 0)));
							} else {
								doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(4, 8, 0)));
							}
						}*/
						
						
						
						return doc;
					}
				});

		return docs;
	}
}
