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
import com.adr.bigdata.indexing.db.sql.beans.LandingPageStatusBean;
import com.adr.bigdata.indexing.db.sql.daos.LandingPageDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

/**
 * @author ndn
 *
 */
public class LandingPageModel extends SolrModel {
	public static final String LANDING_PAGE_ID = "landingPageId";
	public static final String LANDING_PAGE_GROUP_ID = "landingPageGroupId";
	public static final String PRODUCT_ITEM_ID = "productItemId";
	public static final String UPDATE_TIME = "updateTime";

	public List<LandingPageStatusBean> getWPIMIds(Integer landingPageId, Integer landingPageGroupId,
			Integer productItemId) throws Exception {
		try (LandingPageDAO dao = getDbAdapter().openDAO(LandingPageDAO.class)) {
			return dao.getByGroupAndProductItem(landingPageId, landingPageGroupId, productItemId);
		}
	}

	public List<LandingPageStatusBean> getWPIMIds(Integer landingPageId, Integer landingPageGroupId) throws Exception {
		try (LandingPageDAO dao = getDbAdapter().openDAO(LandingPageDAO.class)) {
			return dao.getByGroup(landingPageId, landingPageGroupId);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimes(Set<Integer> warehouseProductItemMappingIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.LANDING_PAGE))
					.getAll(warehouseProductItemMappingIds);
		} catch (Exception e) {
			getLogger().error("error when getting warehouseProductItemMapping: " + warehouseProductItemMappingIds, e);
			return new HashMap<Integer, Long>();
		}
	}

	public void updateToSolr(int landingPageId, int landingPageGroupId, List<LandingPageStatusBean> updatedBeans) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (LandingPageStatusBean bean : updatedBeans) {
			docs.add(createDoc(landingPageId, landingPageGroupId, bean));
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

	private SolrInputDocument createDoc(int landingPageId, int landingPageGroupId, LandingPageStatusBean bean) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, bean.getWarehouseProductItemMappingId());

		if (bean.getStatus() == 1) {
			doc.addField(String.format(SolrFields.LANDING_PAGE_GROUP_ORDER, landingPageGroupId), new SingleMap("set", bean.getPriority()));
		} else {
			doc.addField(String.format(SolrFields.LANDING_PAGE_GROUP_ORDER, landingPageGroupId), new SingleMap("set", null));
		}
		
		if (bean.getlStatus() == 1) {
			doc.addField(String.format(SolrFields.LANDING_PAGE_ORDER, landingPageId), new SingleMap("set", bean.getPriority()));
		} else {
			doc.addField(String.format(SolrFields.LANDING_PAGE_ORDER, landingPageId), new SingleMap("set", null));
		}

		return doc;
	}

	public void updateToCache(List<LandingPageStatusBean> updatedBeans, Long updateTime) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		updatedBeans.forEach(e -> map.put(e.getWarehouseProductItemMappingId(), updateTime));

		try {
			getCacheWrapper().getCacheMap(CacheFields.LANDING_PAGE).putAll(map);
		} catch (Exception e) {
			getLogger().error("error update to cache landing page of warehouseProductItemMapping: " + map, e);
		}
	}

}
