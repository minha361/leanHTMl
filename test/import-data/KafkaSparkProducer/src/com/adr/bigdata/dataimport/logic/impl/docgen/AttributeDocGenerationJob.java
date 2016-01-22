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
import com.adr.bigdata.indexing.db.sql.beans.AttributeSingleBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;

public class AttributeDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws ParseException, Exception {
		// Process Brand update
		byteArrayToPuObject();
		Integer attributeId = getPuObject().getInteger(
				com.adr.bigdata.indexing.db.sql.models.AttributeModel.ATT_ID);
		if (attributeId == null || attributeId <= 0) {
			return null;
		}

		String attName = getPuObject().getString(
				com.adr.bigdata.indexing.db.sql.models.AttributeModel.ATT_NAME);
		Integer attStatus = getPuObject()
				.getInteger(
						com.adr.bigdata.indexing.db.sql.models.AttributeModel.ATT_STATUS);
		Long updateTime = getPuObject()
				.getLong(
						com.adr.bigdata.indexing.db.sql.models.AttributeModel.ATT_UPDATE_TIME);
		if (attName == null || attStatus == null || updateTime == null) {
			getLogger().error("missing fields");
			return null;
		}

		/* Check cache */
		if (getCheckingUpdateTime()) {
			try {
				AttributeSingleBean attributeSingleBean = (AttributeSingleBean) getCacheWrapper()
						.getCacheMap(CacheFields.ATTRIBUTE).get(attributeId);
				if (attributeSingleBean != null
						&& attributeSingleBean.getUpdateTime() >= updateTime) {
					getLogger().info(
							"attributeId: " + attributeId
									+ " has been updated by a newer");
					return null;
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* End: Check cache */

		/* Get warehouseProductItemIds from DB */
		List<Integer> warehouseProductItemmappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(
				WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemmappingIds = dao.getByAttributeId(attributeId);
		}
		if (warehouseProductItemmappingIds == null) {
			getLogger().error(
					"can't find warehouseProductItemmappingIds with attributeId: "
							+ attributeId);
			return null;
		}
		/* End: Get warehouseProductItemIds from DB */
		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemmappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub
						SolrInputDocument doc = new SolrInputDocument();
						doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);
						Map<String, Object> fs = new HashMap<String, Object>();
						fs.put("set", attStatus == 1);
						doc.addField(
								String.format(SolrFields.ATT_B, attributeId),
								fs);
						Map<String, Object> fs2 = new HashMap<String, Object>();
						fs2.put("set", attName);
						doc.addField(String.format(SolrFields.ATT_NAME_S,
								attributeId), fs2);
						return doc;
					}

				});
		return docs;

	}
}
