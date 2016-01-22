package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;

public class AttributeValueModel extends SolrModel {
	public static final String ATTV_ID = "id";
	public static final String ATTV_ATTRIBUTE_ID = "attributeId";
	public static final String ATTV_VALUE = "value";
	public static final String ATTV_UPDATE_TIME = "updateTime";
	public static final String ATTV_STATUS = "status";

	public void updateAttributeValueToCache(AttributeValueBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.ATTRIBUTE_VALUE).put(bean.getId(), bean);
		} catch (Exception e) {
			getLogger().error("update to cache attribute value " + bean.getId() + " error ", e);
		}
	}

	public AttributeValueBean getAttributeValueByCache(int attributeValueId) {
		try {
			return (AttributeValueBean) getCacheWrapper().getCacheMap(CacheFields.ATTRIBUTE_VALUE)
					.get(attributeValueId);
		} catch (Exception e) {
			getLogger().error("attributeValueId " + attributeValueId + " error ", e);
			return null;
		}
	}

	public List<Integer> getWarehouseProductItemmappingIdsByAttributeValueId(int attributeId, int attributeValueId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByAttributeIdAndAttributeValueId(attributeId, attributeValueId);
		}
	}

	public void updateToSolr(List<Integer> warehouseProductItemmappingIds, int attributeId, int attributeValueId,
			String value, Integer status) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int warehouseProductItemMappingId : warehouseProductItemmappingIds) {
			SolrInputDocument doc = createSolrDocument(warehouseProductItemMappingId, attributeId, attributeValueId,
					value, status);
			docs.add(doc);

			if (docs.size() >= getNumDocPerRequest()) {
				getSolrClient().add(docs);
//				getSolrClient().commit();
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			getSolrClient().add(docs);
//			getSolrClient().commit();
		}
		if (isCommit()) {
			getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
		}
	}

	private SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, int attributeId,
			int attributeValueId, String value, Integer status) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
		if (status == 1) {
			doc.addField(String.format(SolrFields.ATT_S, attributeId), new SingleMap("set", value));
			doc.addField(String.format(SolrFields.ATT_I, attributeId), new SingleMap("set", attributeValueId));
			doc.addField(String.format(SolrFields.ATT_I_FACET, attributeId), new SingleMap("set", attributeValueId));
			try {
				doc.addField(String.format(SolrFields.ATT_D, attributeId), new SingleMap("set", Double.parseDouble(value)));
			} catch (Exception e) {
				
			}
		} else {
			doc.addField(String.format(SolrFields.ATT_S, attributeId), new SingleMap("set", null));
			doc.addField(String.format(SolrFields.ATT_I, attributeId), new SingleMap("set", null));
			doc.addField(String.format(SolrFields.ATT_I_FACET, attributeId), new SingleMap("set", null));
			doc.addField(String.format(SolrFields.ATT_D, attributeId), new SingleMap("set", null));
		}
		return doc;
	}
}
