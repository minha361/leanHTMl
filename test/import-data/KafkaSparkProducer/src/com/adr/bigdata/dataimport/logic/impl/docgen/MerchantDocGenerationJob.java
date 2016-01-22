package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.parser.ParseException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;

public class MerchantDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext conf)
			throws ParseException, Exception {
		byteArrayToPuObject();
		Integer merchantId = getPuObject().getInteger(
				com.adr.bigdata.indexing.db.sql.models.MerchantModel.MC_ID);
		if (merchantId == null || merchantId <= 0) {
			return null;
		}

		String mcName = getPuObject().getString(
				com.adr.bigdata.indexing.db.sql.models.MerchantModel.MC_NAME);
		Integer mcStatus = getPuObject().getInteger(
				com.adr.bigdata.indexing.db.sql.models.MerchantModel.MC_STATUS);
		Long updateTime = getPuObject()
				.getLong(
						com.adr.bigdata.indexing.db.sql.models.MerchantModel.MC_UPDATE_TIME);
		String image = getPuObject().getString(
				com.adr.bigdata.indexing.db.sql.models.MerchantModel.MC_IMAGE);
		if (mcName == null || mcStatus == null || updateTime == null) {
			getLogger().error("missing fields");
			return null;
		}

		/* Check cache */
		if (getCheckingUpdateTime()) {
			try {
				MerchantBean merchantBean = (MerchantBean) getCacheWrapper()
						.getCacheMap(CacheFields.MERCHANT).get(merchantId);
				if (merchantBean != null
						&& merchantBean.getUpdateTime() >= updateTime) {
					getLogger().info(
							"merchantId: " + merchantId
									+ " has been updated by a newer");
					return null;
				}
				if (merchantBean == null) {
					System.out.println("Merchant " + merchantId
							+ " is not in the cache");
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* Check cache */

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemmappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemmappingIds = dao.getByMerchantId(merchantId);
		}
		if (warehouseProductItemmappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with merchantId: "
							+ merchantId);
			return null;
		}
		/* Get warehouseProductItemIds from DB */

		JavaRDD<SolrInputDocument> docs = conf.parallelize(
				warehouseProductItemmappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						if (mcName != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", mcName);
							doc.addField(SolrFields.MERCHANT_NAME, fs);
						}
						if (mcStatus != null) {
							Map<String, Object> fs = new HashMap<String, Object>();
							fs.put("set", mcStatus == 1);
							doc.addField(SolrFields.IS_MERCHANT_ACTIVE, fs);
						}
						return doc;
					}

				});

		/* If success, update to cache */
		try {
			MerchantBean bean = new MerchantBean();
			bean.setId(merchantId);
			bean.setName(mcName);
			bean.setStatus(mcStatus);
			bean.setUpdateTime(updateTime);
			bean.setImage(image);
			getCacheWrapper().getCacheMap(CacheFields.MERCHANT).put(merchantId,
					bean);
		} catch (Exception e) {
			getLogger().error("", e);
		}
		return docs;
	}
}
