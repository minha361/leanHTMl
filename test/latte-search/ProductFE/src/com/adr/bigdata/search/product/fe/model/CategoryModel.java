package com.adr.bigdata.search.product.fe.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.hazelcast.core.IMap;

public class CategoryModel extends CachedModel {
	public Set<AttributeCategoryMappingBean> getFilterAttributes(int catId) throws Exception {
		CategoryBean categoryBean = null;
		IMap<Integer, CategoryBean> id2Cat = null;
		try {
			id2Cat = getCachedMap(CacheFields.CATEGORY);
		} catch (Exception e) {
			getLogger().error("fail to get CATEGORY from cache", e);
			return null;
		}

		categoryBean = id2Cat.get(catId);

		IMap<Integer, Map<Integer, AttributeCategoryMappingBean>> id2AttCategoryMapping = null;
		try {
			id2AttCategoryMapping = getCachedMap(CacheFields.ATTRIBUTE_CATEGORY_FILTER);
		} catch (Exception ex) {
			getLogger().error("fail to get AttributeCategoryMappingBean from cache", ex);
			return null;
		}

		Collection<Map<Integer, AttributeCategoryMappingBean>> cachedAttCategory = 
				id2AttCategoryMapping.getAll(new HashSet<Integer>(categoryBean.getPath())).values();
		
		return unionAllMapCategory(cachedAttCategory);
	}

	public Collection<CategoryBean> getCategories(Set<Integer> facetedCatIds) throws Exception {

		if (facetedCatIds == null || facetedCatIds.isEmpty())
			return null;

		IMap<Integer, CategoryBean> id2Cat;
		try {
			id2Cat = getCachedMap(CacheFields.CATEGORY);
		} catch (Exception e) {
			getLogger().error("Fail to get category map from cache", e);
			return null;
		}

		return id2Cat.getAll(facetedCatIds).values();
	}

	public CategoryBean getCategory(int catId) {
		IMap<Integer, CategoryBean> id2Cat;
		try {
			id2Cat = getCachedMap(CacheFields.CATEGORY);
		} catch (Exception e) {
			getLogger().error("Fail to get category map from cache", e);
			return null;
		}

		return id2Cat.get(catId);
	}
	
	public Map<Integer, CategoryBean> getCategoryLevel1Of(Set<Integer> catIds) {
		IMap<Integer, CategoryBean> id2Cat;
		try {
			id2Cat = getCachedMap(CacheFields.CATEGORY);
		} catch (Exception e) {
			getLogger().error("Fail to get category map from cache", e);
			return null;
		}

		Map<Integer, CategoryBean> map = id2Cat.getAll(catIds);
		Map<Integer, Integer> cat2cat1Ids = new HashMap<>();
		Set<Integer> cat1Ids = new HashSet<>();
		for(CategoryBean bean : map.values()) {
			if (!bean.getPath().isEmpty()) {
				int last = bean.getPath().get(bean.getPath().size() - 1);
				if (last <= 0) {
					last = bean.getPath().get(bean.getPath().size() - 2);
				}
				cat2cat1Ids.put(bean.getId(), last);
				cat1Ids.add(last);
			}
		}
		Map<Integer, CategoryBean> _result = id2Cat.getAll(cat1Ids);
		Map<Integer, CategoryBean> result = new HashMap<>();
		for (Entry<Integer, Integer> e : cat2cat1Ids.entrySet()) {
			if (_result.containsKey(e.getValue())) {
				result.put(e.getKey(), _result.get(e.getValue()));
			}
		}
		return result;
	}

	private Set<AttributeCategoryMappingBean> unionAllMapCategory(Collection<Map<Integer, AttributeCategoryMappingBean>> 
			cachedAttCategory) {
		Set<Integer> attIdSet = new HashSet<>();
		Set<AttributeCategoryMappingBean> result = new HashSet<AttributeCategoryMappingBean>();
		for (Map<Integer, AttributeCategoryMappingBean> attId2Bean : cachedAttCategory) {
			for (AttributeCategoryMappingBean bean : attId2Bean.values()) {
				if (bean.getAttributeType() == 2 || bean.getAttributeType() == 4) {
					if (!attIdSet.contains(bean.getAttributeId())) {
						result.add(bean);
						attIdSet.add(bean.getAttributeId());
					}
				}
			}
		}
		return result;
	}
}
