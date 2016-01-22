package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.AttributeSingleBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;

public class AttributeModel extends SolrModel {
	public static final String ATT_ID = "id";
	public static final String ATT_NAME = "name";
	public static final String ATT_STATUS = "status";
	public static final String ATT_UPDATE_TIME = "updateTime";

	public void updateAttributeToCache(AttributeSingleBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.ATTRIBUTE).put(bean.getId(), bean);
		} catch (Exception e) {
			getLogger().error("update attribute " + bean.getId() + " error ", e);
		}
	}

	public AttributeSingleBean getAttributeByCache(int attributeId) {
		try {
			return (AttributeSingleBean) getCacheWrapper().getCacheMap(CacheFields.ATTRIBUTE).get(attributeId);
		} catch (Exception e) {
			getLogger().error("attributeId: " + attributeId + " error ", e);
			return null;
		}
	}

	public List<Integer> getWarehouseProductItemmappingIdsByAttributeId(int attributeId) throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByAttributeId(attributeId);
		}
	}

	public void updateToSolr(List<Integer> warehouseProductItemmappingIds, int attributeId, String attName,
			int attStatus) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int warehouseProductItemMappingId : warehouseProductItemmappingIds) {
			SolrInputDocument doc = createSolrDocument(warehouseProductItemMappingId, attributeId, attName, attStatus);
			docs.add(doc);

			if (docs.size() >= getNumDocPerRequest()) {
				getSolrClient().add(docs);
				// getSolrClient().commit();
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			getSolrClient().add(docs);
			// getSolrClient().commit();
		}
		if (isCommit()) {
			getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
		}
	}

	private SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, int attributeId, String attName,
			Integer attStatus) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
//		if (attStatus != null) {
//			doc.addField(String.format(SolrFields.ATT_B, attributeId), new SingleMap("set", attStatus == 1));
//		}
		if (attName != null) {
			doc.addField(String.format(SolrFields.ATT_NAME_S, attributeId), new SingleMap("set", attName));
		}

		return doc;
	}
}
