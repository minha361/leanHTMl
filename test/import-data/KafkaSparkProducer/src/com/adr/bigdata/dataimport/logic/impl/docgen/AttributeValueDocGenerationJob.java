package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.dataimport.utils2.Constants;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.nhb.common.data.PuObject;

public class AttributeValueDocGenerationJob extends DocGenerationBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws ParseException, Exception {

		byteArrayToPuObject();

		Integer attributeValueId = getPuObject()
				.getInteger(
						com.adr.bigdata.indexing.db.sql.models.AttributeValueModel.ATTV_ID);
		if (attributeValueId == null || attributeValueId <= 0) {
			return null;
		}

		Integer attributeId = getPuObject()
				.getInteger(
						com.adr.bigdata.indexing.db.sql.models.AttributeValueModel.ATTV_ATTRIBUTE_ID);
		if (attributeId == null || attributeId <= 0) {
			return null;
		}

		String value = getPuObject()
				.getString(
						com.adr.bigdata.indexing.db.sql.models.AttributeValueModel.ATTV_VALUE);
		Long updateTime = getPuObject()
				.getLong(
						com.adr.bigdata.indexing.db.sql.models.AttributeValueModel.ATTV_UPDATE_TIME);
		if (value == null || updateTime == null) {
			getLogger().error("missing fields");
			return null;
		}

		/* Check cache */
		if (getCheckingUpdateTime()) {
			try {
				AttributeValueBean attributeValueBean = (AttributeValueBean) getCacheWrapper()
						.getCacheMap(CacheFields.ATTRIBUTE_VALUE).get(
								attributeValueId);
				if (attributeValueBean != null
						&& attributeValueBean.getUpdateTime() >= updateTime) {
					getLogger().info(
							"attributeValueId: " + attributeValueId
									+ " has been updated by a newer");
					return null;
				}
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
		/* End: Check to cache */

		List<Integer> warehouseProductItemmappingIds = null;
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter()
				.openDAO(WarehouseProductItemMappingIdDAO.class);) {
			warehouseProductItemmappingIds = dao
					.getByAttributeIdAndAttributeValueId(attributeId,
							attributeValueId);
		}
		if (warehouseProductItemmappingIds == null) {
			getLogger().error(
					"cant not get warehouseProductItemmappingIds with attributeValueId: "
							+ attributeValueId);
			return null;
		}

		JavaRDD<SolrInputDocument> docs = jsc.parallelize(
				warehouseProductItemmappingIds).map(
				new Function<Integer, SolrInputDocument>() {

					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					@Override
					public SolrInputDocument call(Integer arg0)
							throws Exception {
						// TODO Auto-generated method stub\
						SolrInputDocument doc = new SolrInputDocument();
						doc.setField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
								arg0);

						Map<String, Object> fsAttValue = new HashMap<String, Object>();
						fsAttValue.put("set", value);
						doc.addField(
								String.format(SolrFields.ATT_S, attributeId),
								fsAttValue);
						try {
							Map<String, Object> fsAttValueDouble = new HashMap<String, Object>();
							fsAttValueDouble.put("set",
									Double.parseDouble(value));
							doc.addField(String.format(SolrFields.ATT_D,
									attributeId), fsAttValueDouble);
						} catch (NumberFormatException e) {
							// do nothing
						}

						return doc;
					}

				});

		return docs;
	}

}
