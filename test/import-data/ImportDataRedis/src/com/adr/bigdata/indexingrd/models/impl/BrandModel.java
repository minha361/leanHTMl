package com.adr.bigdata.indexingrd.models.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.WPMAndVisibleBean;
import com.adr.bigdata.indexingrd.ProductFields;
import com.adr.bigdata.indexingrd.db.sql.daos.BrandDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class BrandModel extends RdCachedModel {
	public static final String BRAND_ID = "id";
	public static final String BRAND_NAME = "name";
	public static final String BRAND_STATUS = "status";
	public static final String BRAND_IMAGE = "image";
	public static final String BRAND_DESCRIPTION = "description";
	public static final String BRAND_UPDATE_TIME = "updateTime";

	public List<WPMAndVisibleBean> getWarehouseProductItemMappingIdsByBrand(int brandId) throws Exception {
		try (BrandDAO dao = this.getDbAdapter().openDAO(BrandDAO.class)) {
			return dao.getWPMByBrandId(brandId);
		}
	}

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String STATUS = "stt";
	private static final String IMAGE = "image";
	private static final String DESCRIPTION = "des";
	
	private static final String PREFIX = "br:";
	private static final String OUTPUT_CACHE_PREFIX = "br:";
	
	public Long getUpdateTimeFromCache(Integer brandId) {
		if (brandId == null) {
			return null;
		}
		
		try (Jedis j = getJedisUpdateTime()) {
			String key = PREFIX + brandId;
			byte[] bytes = j.get(key.getBytes());
			return bytesToLong(bytes);
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}

	public List<Object> updateToRedis(List<Integer> warehouseProductItemMappingIds, String brandName) throws IOException {
		try (Jedis j = getJedis(); Pipeline p = j.pipelined()) {
			for (int wpmid : warehouseProductItemMappingIds) {
				String key = String.valueOf(wpmid);
				p.hset(key, ProductFields.BRAND_NAME, brandName);
			}
			return p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}

	public void updateBrandToOuputCache(BrandBean bean) {
		try (Jedis j = getJedisUpdateTime()) {
			Map<String, String> data = new HashMap<>();
			data.put(ID, String.valueOf(bean.getId()));
			data.put(NAME, bean.getName());
			data.put(STATUS, String.valueOf(bean.getStatus()));
			data.put(IMAGE, bean.getImage());
			data.put(DESCRIPTION, bean.getDescription());
			String key = OUTPUT_CACHE_PREFIX + bean.getId();
			j.hmset(key, data);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	public void updateUpdateTimeToCache(int brId, long updateTime) {
		try (Jedis j = getJedisUpdateTime()) {
			String key = PREFIX + brId;
			j.set(key.getBytes(), longToBytes(updateTime));
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

//	public void sendToQueue(List<WPMAndVisibleBean> warehouseProductItemMappings, String brandName) throws IOException {
//		List<PuObject> data2Queue = new ArrayList<>();
//		for (WPMAndVisibleBean wpm : warehouseProductItemMappings) {
//			if (wpm.getIsVisible() == ONSITE_STATUS) {
//				PuObject data = new PuObject(ProductFields.BRAND_NAME, brandName);
//				data2Queue.add(createMessage(wpm.getWpmid(), ACTION_UPDATE, data));
//			}
//		}
//		if (!data2Queue.isEmpty()) {
//			sendMessageToQueue(data2Queue);
//		}
//	}
}
