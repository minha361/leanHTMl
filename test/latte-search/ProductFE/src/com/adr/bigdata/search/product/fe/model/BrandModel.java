package com.adr.bigdata.search.product.fe.model;

import java.util.Collection;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.hazelcast.core.IMap;

public class BrandModel extends CachedModel {

	public Collection<BrandBean> getBrands(Set<Integer> brandIds) throws Exception {
		if (brandIds == null || brandIds.isEmpty()) {
			return null;
		}

		IMap<Integer, BrandBean> id2Brand = null;
		try {
			id2Brand = getCachedMap(CacheFields.BRAND);
		} catch (Exception e) {
			getLogger().error("fail to get brands from cache", e);
			return null;
		}

		return id2Brand.getAll(brandIds).values();
	}
	
	public BrandBean getBrand(int brandId) {
		if (brandId < 0) {
			return null;
		}

		IMap<Integer, BrandBean> id2Brand = null;
		try {
			id2Brand = getCachedMap(CacheFields.BRAND);
		} catch (Exception e) {
			getLogger().error("fail to get brands from cache", e);
			return null;
		}
		return id2Brand.get(brandId);
	}
}
