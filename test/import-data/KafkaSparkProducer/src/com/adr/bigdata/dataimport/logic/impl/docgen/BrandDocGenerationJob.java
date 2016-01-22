package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.BrandModel;

public class BrandDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws Exception {

		byteArrayToPuObject();

		int brandId = getPuObject().getInteger(BrandModel.BRAND_ID);
		String brandName = getPuObject().getString(BrandModel.BRAND_NAME);
		Integer brandStatus = getPuObject().getInteger(BrandModel.BRAND_STATUS);
		long updateTime = getPuObject()
				.getInteger(BrandModel.BRAND_UPDATE_TIME);

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

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemMappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemMappingIds = dao.getByBrandId(brandId);
		}
		if (getCheckingUpdateTime()) {
			if (warehouseProductItemMappingIds == null) {
				getLogger().error(
						"can't find warehouseProductItemmappingIds with brandId: "
								+ brandId);
				return null; // errors
			}
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

		return docs; // success
	}

}
