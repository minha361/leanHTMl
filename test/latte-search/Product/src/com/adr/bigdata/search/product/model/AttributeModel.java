package com.adr.bigdata.search.product.model;

import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.search.model.CachedModel;
import com.hazelcast.core.IMap;

public class AttributeModel extends CachedModel {
	public AttributeModel() {
	}
	
	public Map<String, AttributeValueMeasureUnitDisplayBean> getAttValues(Set<String> keys) {
		IMap<String, AttributeValueMeasureUnitDisplayBean> imap = null;
		try {
			imap = getCachedMap("displayUnitMobile");
		} catch (Exception e) {
			return null;
		}
		if (imap == null) {
			return null;
		}
		
		return imap.getAll(keys);
	}
}
