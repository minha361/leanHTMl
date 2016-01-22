/**
 * 
 */
package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.CollectionGroupProductStatusBean;
import com.adr.bigdata.indexing.db.sql.daos.CollectionGroupProductDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

/**
 * @author ndn
 *
 */
public class CollectionGroupProductItemModel extends SolrModel {
	public static final String COLLECTION_GROUP_ID = "collectionGroupId";
	public static final String PRODUCT_ITEM_ID = "productItemId";
	public static final String UPDATE_TIME = "updateTime";

//	public List<CollectionGroupProductStatusBean> getWPIMIds(Integer landingPageId,
//			Integer productItemId) throws Exception {
//		try (CollectionGroupProductDAO dao = getDbAdapter().openDAO(CollectionGroupProductDAO.class)) {
//			return dao.getByGroupAndProductItem(landingPageId, productItemId);
//		}
//	}

	public List<CollectionGroupProductStatusBean> getWPIMIds(Integer collectionGroupId) throws Exception {
		try (CollectionGroupProductDAO dao = getDbAdapter().openDAO(CollectionGroupProductDAO.class)) {
			return dao.getByGroup(collectionGroupId);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimes(Set<Integer> warehouseProductItemMappingIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.COLLECTION_GROUP_PRODUCT))
					.getAll(warehouseProductItemMappingIds);
		} catch (Exception e) {
			getLogger().error("error when getting warehouseProductItemMapping: " + warehouseProductItemMappingIds, e);
			return new HashMap<Integer, Long>();
		}
	}

	public void updateToSolr(int landingPageId, List<CollectionGroupProductStatusBean> updatedBeans) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (CollectionGroupProductStatusBean bean : updatedBeans) {
			docs.add(createDoc(landingPageId, bean));
			if (docs.size() > getNumDocPerRequest()) {
				getSolrClient().add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			getSolrClient().add(docs);
		}
		
		if (isCommit()) {
			getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
		}
	}

	private SolrInputDocument createDoc(int collectionGroupId, CollectionGroupProductStatusBean bean) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, bean.getWarehouseProductItemMappingId());
		//doc.addField("on_site", 511);//To Test
		
		if (bean.getStatus() == 1) {
			doc.addField(String.format(SolrFields.COLLECTION_GROUP_ORDER, collectionGroupId), new SingleMap("set", bean.getPriority()));
		} else {
			doc.addField(String.format(SolrFields.COLLECTION_GROUP_ORDER, collectionGroupId), new SingleMap("set", null));
		}

		return doc;
	}

	public void updateToCache(List<CollectionGroupProductStatusBean> updatedBeans, Long updateTime) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		updatedBeans.forEach(e -> map.put(e.getWarehouseProductItemMappingId(), updateTime));

		try {
			getCacheWrapper().getCacheMap(CacheFields.COLLECTION_GROUP_PRODUCT).putAll(map);
		} catch (Exception e) {
			getLogger().error("error update to cache collection group of warehouseProductItemMapping: " + map, e);
		}
	}

}
