package com.adr.bigdata.indexing.models.impl;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.indexing.models.CachedModel;

public class KeywordModel extends CachedModel {
	public static final String K_ID = "id";
	public static final String K_KEYWORD = "keyword";
	public static final String K_LINK = "link";
	public static final String K_STATUS = "status";
	public static final String K_UPDATE_TIME = "updateTime";
	
	public KeywordBean get(String keyword) {
		try {
			return (KeywordBean) getCacheWrapper().getCacheMap(CacheFields.KEYWORD).get(keyword);
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
	}
	
	public void updateCache(KeywordBean bean) {
		try {
			getCacheWrapper().getCacheMap(CacheFields.KEYWORD).put(bean.getKeyword(), bean);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
}
