/**
 * 
 */
package com.adr.bigdata.indexingrd.models.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.adr.bigdata.indexing.db.sql.beans.CollectionGroupProductStatusBean;
import com.adr.bigdata.indexingrd.CacheFields;
import com.adr.bigdata.indexingrd.ProductFields;
import com.adr.bigdata.indexingrd.db.sql.daos.CollectionGroupProductDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

/**
 * @author ndn
 *
 */
public class CollectionGroupProductItemModel extends RdCachedModel {
	public static final String COLLECTION_GROUP_ID = "collectionGroupId";
	public static final String PRODUCT_ITEM_ID = "productItemId";
	public static final String UPDATE_TIME = "updateTime";

	public List<CollectionGroupProductStatusBean> getWPIMIds(Integer collectionGroupId) throws Exception {
		try (CollectionGroupProductDAO dao = getDbAdapter().openDAO(CollectionGroupProductDAO.class)) {
			return dao.getByGroup(collectionGroupId);
		}
	}
	
	
	public List<Object> updateToRedis(int collectionGroupId, List<CollectionGroupProductStatusBean> updatedBeans) {
		try (Jedis j = getJedis(); Pipeline p = j.pipelined()) {
			for (CollectionGroupProductStatusBean bean : updatedBeans) {
				String key = keyOfWPM(bean.getWarehouseProductItemMappingId());//String.valueOf(bean.getWarehouseProductItemMappingId());
				String collectionGroupField = String.format(ProductFields.COLLECTION_GROUP_TEMPLATE, collectionGroupId);
				if (bean.getStatus() == 1) {
					p.hset(key, collectionGroupField, String.valueOf(bean.getPriority()));
				} else {
					if(j.hget(key, collectionGroupField) != null){
						p.hdel(key, collectionGroupField);
					}
				}
			}
			return p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}
	
	
	public Map<String, String> getLastUpdateTimes(int collectionGroupId) {
		try {
			Jedis j = getJedisUpdateTime();
			String key = CacheFields.COLLECTION_GROUP_PRODUCT + collectionGroupId;
			return j.hgetAll(key);
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<String, String>();
		}
	}

	public void updateTimeToCache(int collectionGroupId, List<CollectionGroupProductStatusBean> updatedBeans, Long updateTime) {
		try (Jedis j = getJedisUpdateTime(); Pipeline p = j.pipelined()) {
			String key = CacheFields.COLLECTION_GROUP_PRODUCT + collectionGroupId;
			for (CollectionGroupProductStatusBean collectionStatusBean : updatedBeans) {
				String field = String.valueOf(collectionStatusBean.getWarehouseProductItemMappingId());
				p.hset(key, field, String.valueOf(updateTime));
			}
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
	
}
