package com.adr.bigdata.search.product.fe.model;

import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.hazelcast.core.IMap;

public class AttributeModel extends CachedModel {
	public Map<String, AttributeValueMeasureUnitDisplayBean> getDisplayUnit(Set<String> keys) {
		IMap<String, AttributeValueMeasureUnitDisplayBean> map = null;
		try {
			map = getCachedMap(CacheFields.DISPLAY_UNIT);
		} catch (Exception e) {
			getLogger().error("", e);
			return null;
		}
		return map.getAll(keys);
	}
}
