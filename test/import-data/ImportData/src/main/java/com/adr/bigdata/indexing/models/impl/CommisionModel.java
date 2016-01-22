package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.CommisionBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.CommisionVO;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class CommisionModel extends SolrModel {
	public static final String C_MERCHANT_ID = "merchantId";
	public static final String C_CUSTOM_COMMISION_FEE = "customCommissionFee";
	public static final String C_CATGORY_ID = "categoryId";
	public static final String C_IS_NOT_APPLY_COMMISION = "isNotApplyCommision";
	public static final String C_COMMISION_FEE = "commisionFee";
	public static final String C_UPDATE_TIME = "updateTime";

	public Collection<CommisionBean> getWPIMIds(int merchantId, List<Integer> catIds) throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class)) {
			return dao.getByMerchantAndCategories(merchantId, catIds);
		}
	}

	public void updateToSolr(Map<Integer, CommisionVO> data)
			throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (Entry<Integer, CommisionVO> e : data.entrySet()) {
			int wpimId = e.getKey();
			CommisionVO commision = e.getValue();
			docs.add(creatDocument(wpimId, commision.getIsNotApplyCommision() == 1, commision.getCommisionFee()));
			if (docs.size() >= 0) {
				getSolrClient().add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}
			if (!docs.isEmpty()) {
				getSolrClient().add(docs);
			}
			if (isCommit()) {
				getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
			}
		}
	}

	public void updateToCache(int merchantId, Map<Integer, Long> catIdUpdateTimes) {
		try {
			Map<String, Long> map = new HashMap<String, Long>();
			catIdUpdateTimes.forEach((catId, updateTime) -> map.put(merchantId + "_" + catId, updateTime));
			getCacheWrapper().getCacheMap(CacheFields.COMMISION).putAll(map);
		} catch (Exception e) {
			getLogger().error("error update commision of merchant: " + merchantId + " with cats: " + catIdUpdateTimes, e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Long> getLastUpdatedTime(Set<String> keys) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.COMMISION)).getAll(keys);
		} catch (Exception e) {
			getLogger().error("error commision : " + keys, e);
			return new HashMap<String, Long>();
		}
	}

	private SolrInputDocument creatDocument(int wpimId, boolean isNotApplyCommision, double commisionFee) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, wpimId);
		doc.addField(SolrFields.IS_NOT_APPLY_COMMISION, new SingleMap("set", isNotApplyCommision));
		doc.addField(SolrFields.COMMISION_FEE, new SingleMap("set", commisionFee));
		return doc;
	}
}
