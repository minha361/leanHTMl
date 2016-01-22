package com.adr.bigdata.search.product.fe.model;

import java.util.Collection;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.hazelcast.core.IMap;

public class MerchantModel extends CachedModel {
	public final static int DISABLE_MERCHANT_ID = -1;

	public MerchantBean getMerchant(int mcId) throws Exception {
		if (mcId != DISABLE_MERCHANT_ID) {
			try {
				return (MerchantBean) getCachedMap(CacheFields.MERCHANT).get(mcId);
			} catch (Exception e) {
				getLogger().error("fail to  get merchant bean map from cache", e);
				return null;
			}
		} else {
			return null;
		}
	}

	public Collection<MerchantBean> getAllMerchants(Set<Integer> setMerchantIds) throws Exception {

		if (setMerchantIds == null || setMerchantIds.isEmpty()) {
			return null;
		}

		IMap<Integer, MerchantBean> id2Merchant = null;

		try {
			id2Merchant = getCachedMap(CacheFields.MERCHANT);
		} catch (Exception e) {
			getLogger().error("fail to  get merchant bean map from cache", e);
			return null;
		}

		return id2Merchant.getAll(setMerchantIds).values();
	}
}
