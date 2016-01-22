package com.adr.bigdata.indexingrd.models.impl;

import net.minidev.json.JSONObject;
import redis.clients.jedis.Jedis;

import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.indexingrd.CacheFields;
import com.adr.bigdata.indexingrd.models.RdCachedModel;
import com.adr.bigdata.indexingrd.utils.JsonUtil;

public class KeywordModel extends RdCachedModel {
	public static final String K_ID = "id";
	public static final String K_KEYWORD = "keyword";
	public static final String K_LINK = "link";
	public static final String K_STATUS = "status";
	public static final String K_UPDATE_TIME = "updateTime";

	public Long getLastUpdateTime(String keyword) {
		try {
			Jedis j = getJedisUpdateTime();
			String key = CacheFields.KEYWORD;
			String jsonString = j.hget(key, keyword);
			JSONObject jsonObject = JsonUtil.fromString(jsonString);
			return Long.valueOf(jsonObject.getAsString(K_UPDATE_TIME));
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}

	public void updateTimeToCache(KeywordBean bean) {
		try (Jedis j = getJedisUpdateTime()) {
			String key = CacheFields.KEYWORD;
			JSONObject obj = new JSONObject();
	        obj.put(K_ID, bean.getId());
	        obj.put(K_KEYWORD, bean.getKeyword());
	        obj.put(K_LINK, bean.getUrl());
	        obj.put(K_STATUS, bean.getStatus());
	        obj.put(K_UPDATE_TIME, bean.getUpdateTime());
			j.hset(key, bean.getKeyword(), JsonUtil.objectToJson(obj));
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
}
