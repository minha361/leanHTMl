package com.adr.bigdata.indexingrd.logic.impl;

import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.KeywordModel;
import com.nhb.common.data.PuObject;

public class KeywordUpdatedProccessor extends BaseLogicProcessor {
	private KeywordModel keywordModel = null;

	@Override
	public void execute(PuObject data) throws Exception {
		if (data == null) {
			return;
		}
		
		if (keywordModel == null) {
			keywordModel = getModel(KeywordModel.class);
		}
		long start = System.currentTimeMillis();
		
		Integer id = data.getInteger(KeywordModel.K_ID);
		String keyword = data.getString(KeywordModel.K_KEYWORD);
		String link = data.getString(KeywordModel.K_LINK);
		Integer status = data.getInteger(KeywordModel.K_STATUS);
		Long updateTime = data.getLong(KeywordModel.K_UPDATE_TIME);
		
		if (id == null || keyword == null || link == null || status == null || updateTime == null) {
			getLogger().error("Some field is missing: {}", data);
			return;
		}
		keyword = keyword.toLowerCase();
		
		Long lastUpdteTime  = keywordModel.getLastUpdateTime(keyword);
		if (lastUpdteTime != null){
			if(lastUpdteTime > updateTime) {
				getLogger().info("keyword: {} has been updated by a newer", keyword);
				return;
			}
			
		}
		
		
		KeywordBean bean = new KeywordBean();
		bean.setId(id);
		bean.setKeyword(keyword);
		bean.setUrl(link);
		bean.setStatus(status);
		bean.setUpdateTime(updateTime);
		
		keywordModel.updateTimeToCache(bean);
		
		getProfillingLogger().debug("total time to proccess a keyword: {} is {}", keyword, (System.currentTimeMillis() - start));
	}

}
