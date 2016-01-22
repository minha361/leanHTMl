package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemTypeBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class WarehouseModel extends SolrModel {
	public static final String WH_ID = "id";
	public static final String WH_STATUS = "status";
	public static final String WH_CITY_ID = "cityId";
	public static final String WH_UPDATE_TIME = "updateTime";

	public WarehouseBean getWarehouseByCache(int warehouseId) {
		try {
			return (WarehouseBean) getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE).get(warehouseId);
		} catch (Exception e) {
			getLogger().error("warehouseId " + warehouseId + " error ", e);
			return null;
		}
	}

	public List<WarehouseProductItemTypeBean> getWarehouseProductItemmappingIdsByWarehouse(int warehouseId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = this.getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByWarehouseId(warehouseId);
		}
	}

	private SolrInputDocument createSolrDocument(int warehouseProductItemMappingId, int productItemType,
			Integer warehouseStatus, Integer warehouseCityId, Integer productItemPolicy) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);
		doc.addField(SolrFields.PRODUCT_ITEM_TYPE, new SingleMap("set", productItemType));
		if (warehouseStatus != null) {
			doc.addField(SolrFields.WAREHOUSE_STATUS, new SingleMap("set", warehouseStatus));
		}
		if (warehouseCityId != null) {
			if (productItemType == 2 || productItemType == 4 || productItemPolicy != 1) {
				doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(warehouseCityId, 0)));
			} else {
				doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(4, 8, 0)));
			}
		}
		return doc;
	}

	public void updateToSolr(List<WarehouseProductItemTypeBean> warehouseProductItemmappingIds, Integer whStatus,
			Integer whCityId) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (WarehouseProductItemTypeBean warehouseProductItemMappingId : warehouseProductItemmappingIds) {
			SolrInputDocument doc = createSolrDocument(warehouseProductItemMappingId.getWhpimId(),
					warehouseProductItemMappingId.getProductItemType(), whStatus, whCityId, warehouseProductItemMappingId.getProductItemPolicy());
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

	public void updateToCache(WarehouseBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE).put(bean.getId(), bean);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, WarehouseBean> getWarehousesByCache(Set<Integer> warehouseIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE)).getAll(warehouseIds);
		} catch (Exception e) {
			getLogger().error("warehouseId " + warehouseIds + " error ", e);
			return new HashMap<Integer, WarehouseBean>();
		}
	}
}
