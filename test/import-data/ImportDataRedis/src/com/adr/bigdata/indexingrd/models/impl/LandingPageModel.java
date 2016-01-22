/**
 * 
 */
package com.adr.bigdata.indexingrd.models.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.adr.bigdata.indexing.db.sql.beans.LandingPageStatusBean;
import com.adr.bigdata.indexingrd.CacheFields;
import com.adr.bigdata.indexingrd.ProductFields;
import com.adr.bigdata.indexingrd.db.sql.daos.LandingPageDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

/**
 * @author ndn
 *
 */
public class LandingPageModel extends RdCachedModel {
	public static final String LANDING_PAGE_ID = "landingPageId";
	public static final String LANDING_PAGE_GROUP_ID = "landingPageGroupId";
	public static final String PRODUCT_ITEM_ID = "productItemId";
	public static final String UPDATE_TIME = "updateTime";

	public List<LandingPageStatusBean> getWPIMIds(Integer landingPageId, Integer landingPageGroupId, Integer productItemId) throws Exception {
		try (LandingPageDAO dao = getDbAdapter().openDAO(LandingPageDAO.class)) {
			return dao.getByGroupAndProductItem(landingPageId, landingPageGroupId, productItemId);
		}
	}

	public List<LandingPageStatusBean> getWPIMIds(Integer landingPageId, Integer landingPageGroupId) throws Exception {
		try (LandingPageDAO dao = getDbAdapter().openDAO(LandingPageDAO.class)) {
			return dao.getByGroup(landingPageId, landingPageGroupId);
		}
	}

	public List<Object> updateToRedis(int landingPageId, int landingPageGroupId, List<LandingPageStatusBean> updatedBeans) {
		try (Jedis j = getJedis(); Pipeline p = j.pipelined()) {
			for (LandingPageStatusBean bean : updatedBeans) {
				String key = keyOfWPM(bean.getWarehouseProductItemMappingId());//String.valueOf(bean.getWarehouseProductItemMappingId());
				String landingPageGroupField = String.format(ProductFields.LANDING_PAGE_GROUP_TEMPLATE, landingPageGroupId);
				if (bean.getStatus() == 1) {
					p.hset(key, landingPageGroupField, String.valueOf(bean.getPriority()));
				} else {
					if(j.hget(key, landingPageGroupField) != null){
						p.hdel(key, landingPageGroupField);
					}
				}

				String landingPageField = String.format(ProductFields.LANDING_PAGE_TEMPLATE, landingPageId);
				if (bean.getlStatus() == 1) {
					p.hset(key, landingPageField, String.valueOf(bean.getPriority()));
				} else {
					if(j.hget(key, landingPageField) != null){
						p.hdel(key, landingPageField);
					}
				}
			}
			return p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}
	
	public Map<String, String> getLastUpdateTimes(int landingPageId) {
		try {
			Jedis j = getJedisUpdateTime();
			String key = CacheFields.LANDING_PAGE + landingPageId;
			return j.hgetAll(key);

		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<String, String>();
		}
	}

	public void updateTimeToCache(int landingPageId, List<LandingPageStatusBean> updatedBeans, Long updateTime) {
		try (Jedis j = getJedisUpdateTime(); Pipeline p = j.pipelined()) {
			String key = CacheFields.LANDING_PAGE + landingPageId;
			for (LandingPageStatusBean landingPageStatusBean : updatedBeans) {
				String field = String.valueOf(landingPageStatusBean.getWarehouseProductItemMappingId());
				p.hset(key, field, String.valueOf(updateTime));
			}
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
}
