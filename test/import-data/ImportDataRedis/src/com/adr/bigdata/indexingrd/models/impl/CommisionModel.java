package com.adr.bigdata.indexingrd.models.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrServerException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.adr.bigdata.indexing.db.sql.beans.CommisionBean;
import com.adr.bigdata.indexingrd.CacheFields;
import com.adr.bigdata.indexingrd.ProductFields;
import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;
import com.adr.bigdata.indexingrd.vos.CommisionVO;

public class CommisionModel extends RdCachedModel {
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

	/**
	 * data : <wpimId, commisionVo>
	 * @param data
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void updateToRedis(Map<Integer, CommisionVO> data) throws SolrServerException, IOException {
		try (Jedis j = getJedis(); Pipeline p = j.pipelined()) {
			for (Entry<Integer, CommisionVO> e : data.entrySet()) {
				int wpimId = e.getKey();
				String key = keyOfWPM(wpimId);//String.valueOf(wpimId);
				CommisionVO commision = e.getValue();
				j.hset(key, ProductFields.IS_NOT_APPLY_COMMISION, String.valueOf(commision.getIsNotApplyCommision()));
				j.hset(key, ProductFields.COMMISION_FEE, String.valueOf(commision.getCommisionFee()));
			}
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	public Map<String, String> getLastUpdateTimes(int merchantId) {
		try {
			Jedis j = getJedisUpdateTime();
			String key = CacheFields.COMMISION + merchantId;
			return j.hgetAll(key);

		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<String, String>();
		}
	}

	public void updateTimeToCache(int merchantId, Map<Integer, Long> catIdUpdateTimes) {
		try (Jedis j = getJedisUpdateTime(); Pipeline p = j.pipelined()) {
			String key = CacheFields.COMMISION + merchantId;
			catIdUpdateTimes.forEach((catId, updateTime) -> {
				p.hset(key, merchantId + "_" + catId, String.valueOf(updateTime));
			});
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("error update commision of merchant: " + merchantId + " with cats: " + catIdUpdateTimes, e);
		}
	}
}
