package com.adr.bigdata.indexingrd.models.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.indexing.db.sql.beans.TrendingKeywordBean;
import com.adr.bigdata.indexingrd.db.sql.daos.TrendingKeywordDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class TrendingKeywordModel extends RdCachedModel {	
	public void trending() throws Exception {
		try (TrendingKeywordDAO dao = getDbAdapter().openDAO(TrendingKeywordDAO.class)) {
			List<TrendingKeywordBean> trendings = dao.getAllTrending();
			Map<String, List<String>> map = new HashMap<>();
			for (TrendingKeywordBean trending: trendings) {
				String keyword = trending.getKeyword().toLowerCase().trim();
				if (keyword != null && !keyword.isEmpty()) {
					String boost = trending.getFieldName() + ":" + trending.getFieldValue() + "^" + trending.getPriority();
					if (map.containsKey(keyword)) {
						map.get(keyword).add(boost);
					} else {
						List<String> lst = new ArrayList<>();
						lst.add(boost);
						map.put(keyword, lst);
					}
				}
			}
			//TODO add to cache and update mysql db here 
		}
	}	
}
