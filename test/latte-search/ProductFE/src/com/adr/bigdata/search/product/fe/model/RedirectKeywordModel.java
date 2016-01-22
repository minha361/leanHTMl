package com.adr.bigdata.search.product.fe.model;

import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.google.common.base.Strings;

public class RedirectKeywordModel extends CachedModel {
	public String getLink(String keyword) {
		try {
			KeywordBean bean = (KeywordBean) getCachedMap(CacheFields.KEYWORD_REDIRECT).get(keyword.trim().toLowerCase());
			if (bean != null && bean.getStatus() == 1) {
				String url = bean.getUrl();
				if (!Strings.isNullOrEmpty(url)) {
					return url;
				}
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
		return null;
	}
}
