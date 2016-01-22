package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingIdPromotionStatusBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.PromotionModel;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.SerializableWarehouseProductItemMappingIdPromotionStatusBean;

public class PromotionDocGenerationJob extends DocGenerationBase {

	private static final long serialVersionUID = 1L;

	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws Exception {
		byteArrayToPuObject();
		getLogger().debug("promotion: " + getPuObject());

		Integer promotionId = getPuObject().getInteger(PromotionModel.PM_ID);
		if (promotionId == null) {
			getLogger().debug("promotionId is Null");
			return null;
		}

		String sStartDate = getPuObject().getString(
				PromotionModel.PM_START_DATE);
		String sFinishDate = getPuObject().getString(
				PromotionModel.PM_FINISH_DATE);
		Integer status = getPuObject().getInteger(PromotionModel.PM_STATUS);
		Double discountPercent = getPuObject().getDouble(
				PromotionModel.PM_DISCOUNT_PERCENT);
		Long updateTime = getPuObject().getLong(PromotionModel.PM_UPDATE_TIME);

		if (sStartDate == null || sFinishDate == null || status == null
				|| discountPercent == null || updateTime == null) {
			getLogger().error("missing some fields");
			return null;
		}

		/* Check cache of Promotion */
		if (getCheckingUpdateTime()) {
			try {
				Long lastUpdateTime = (Long) getCacheWrapper().getCacheMap(
						CacheFields.PROMOTION).get(promotionId);
				if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
					getLogger().info(
							"promotionId: " + promotionId
									+ " has been updated by a newer");
					return null;
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* End: Check cache of Promotion */
		List<SerializableWarehouseProductItemMappingIdPromotionStatusBean> warehouseProductItemmappingIds = new ArrayList<SerializableWarehouseProductItemMappingIdPromotionStatusBean>();
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			List<WarehouseProductItemMappingIdPromotionStatusBean> tmp = dao
					.getByPromotionId(promotionId);
			if (tmp == null) {
				getLogger().info(
						"No warehouseProductItemMappingIds founded with promotionId: "
								+ promotionId);
				return null;
			}
			for (WarehouseProductItemMappingIdPromotionStatusBean b : tmp)
				warehouseProductItemmappingIds
						.add(new SerializableWarehouseProductItemMappingIdPromotionStatusBean(
								b));
		}

		JavaRDD<SolrInputDocument> docs = jsc
				.parallelize(warehouseProductItemmappingIds)
				.map(new Function<SerializableWarehouseProductItemMappingIdPromotionStatusBean, SolrInputDocument>() {

					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(
							SerializableWarehouseProductItemMappingIdPromotionStatusBean arg0)
							throws Exception {
						SolrInputDocument doc = new SolrInputDocument();
						WarehouseProductItemMappingIdPromotionStatusBean warehouseProductItemMapping = arg0
								.getBean();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								warehouseProductItemMapping
										.getWarehouseProductItemMappingId());

						Map<String, Object> fsStartTime = new HashMap<String, Object>();
						fsStartTime.put("set", warehouseProductItemMapping
								.getStartDateDiscount());
						doc.addField(SolrFields.START_TIME_DISCOUNT,
								fsStartTime);

						Map<String, Object> fsFinishTime = new HashMap<String, Object>();
						fsFinishTime.put("set", warehouseProductItemMapping
								.getFinishDateDiscount());
						doc.addField(SolrFields.FINISH_TIME_DISCOUNT,
								fsFinishTime);

						Map<String, Object> fsIsPromotion = new HashMap<String, Object>();
						fsIsPromotion.put("set",
								warehouseProductItemMapping.isPromotionStatus());
						doc.addField(SolrFields.IS_PROMOTION, fsIsPromotion);

						Map<String, Object> fsIsPromotionMapping = new HashMap<String, Object>();
						fsIsPromotionMapping.put("set",
								warehouseProductItemMapping
										.isPromotionMappingStatus());
						doc.addField(SolrFields.IS_PROMOTION_MAPPING,
								fsIsPromotionMapping);

						Map<String, Object> fsDiscount = new HashMap<String, Object>();
						fsDiscount.put("set", warehouseProductItemMapping
								.getDiscountPercent());
						doc.addField(SolrFields.DISCOUNT_PERCENT, fsDiscount);

						return doc;
					}
				});
		return docs;
	}
}
