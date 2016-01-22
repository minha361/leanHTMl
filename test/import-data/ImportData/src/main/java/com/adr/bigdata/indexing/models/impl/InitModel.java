package com.adr.bigdata.indexing.models.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.daos.AttributeCategoryFilterDAO;
import com.adr.bigdata.indexing.db.sql.daos.AttributeDAO;
import com.adr.bigdata.indexing.db.sql.daos.BrandDAO;
import com.adr.bigdata.indexing.db.sql.daos.CategoryDAO;
import com.adr.bigdata.indexing.db.sql.daos.KeywordDAO;
import com.adr.bigdata.indexing.db.sql.daos.MerchantDAO;
import com.adr.bigdata.indexing.models.AbstractModel;
import com.adr.bigdata.indexing.utils.DoubleUtils;
import com.mario.consumer.api.MarioApi;

public class InitModel extends AbstractModel {

	public void init(MarioApi api) throws Exception {
		System.out.println("checkpoint 1... brand");
		Map<Integer, BrandBean> brandMap = new HashMap<Integer, BrandBean>();
		try (BrandDAO dao = getDbAdapter().openDAO(BrandDAO.class)) {
			for (BrandBean bean : dao.getAllBrands()) {
				brandMap.put(bean.getId(), bean);
			}
		} catch (Exception ex) {
			System.err.println("error brand");
			ex.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.BRAND).putAll(brandMap);
		} catch (Exception e) {
			System.err.println("error brand");
			e.printStackTrace();
		}

		System.out.println("checkpoint 2... category");
		Map<Integer, CategoryBean> catMap = new HashMap<Integer, CategoryBean>();
		Map<Integer, Set<Integer>> parent2Children = new HashMap<Integer, Set<Integer>>();
		try (CategoryDAO dao = getDbAdapter().openDAO(CategoryDAO.class)) {
			for (CategoryBean bean : dao.getAllCats()) {
				catMap.put(bean.getId(), bean);
				Set<Integer> childIds = parent2Children.getOrDefault(bean.getParentId(), null);
				if (childIds == null) {
					childIds = new HashSet<Integer>();
				}
				childIds.add(bean.getId());
				parent2Children.put(bean.getParentId(), childIds);
			}
		} catch (Exception ex) {
			System.err.println("error category");
			ex.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.CATEGORY).putAll(catMap);
			api.getCacheMap(CacheFields.CATEGORY_PARENT).putAll(parent2Children);
		} catch (Exception ex) {
			System.err.println("error category");
			ex.printStackTrace();
		}

		System.out.println("checkpoint 3... merchant");
		Map<Integer, MerchantBean> merchantMap = new HashMap<Integer, MerchantBean>();
		try (MerchantDAO dao = getDbAdapter().openDAO(MerchantDAO.class)) {
			for (MerchantBean bean : dao.getAllMerchants()) {
				merchantMap.put(bean.getId(), bean);
			}
		} catch (Exception ex) {
			System.err.println("error merchant");
			ex.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.MERCHANT).putAll(merchantMap);
		} catch (Exception ex) {
			System.err.println("error merchant");
			ex.printStackTrace();
		}

		System.out.println("checkpoint 4.............. category attribute filter");
		Map<Integer, Map<Integer, AttributeCategoryMappingBean>> attCatMap = new HashMap<Integer, Map<Integer, AttributeCategoryMappingBean>>();
		try (AttributeCategoryFilterDAO dao = getDbAdapter().openDAO(AttributeCategoryFilterDAO.class)) {
			for (AttributeCategoryMappingBean bean : dao.getAttributeCategoryMappings()) {
				Map<Integer, AttributeCategoryMappingBean> map = attCatMap.getOrDefault(bean.getCategoryId(), null);
				if (map == null) {
					map = new HashMap<Integer, AttributeCategoryMappingBean>();
				}
				map.put(bean.getAttributeId(), bean);
				attCatMap.put(bean.getCategoryId(), map);
			}
		} catch (Exception e) {
			System.err.println("error attribute category filter");
			e.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.ATTRIBUTE_CATEGORY_FILTER).putAll(attCatMap);
		} catch (Exception e) {
			System.err.println("error attribute category filter");
			e.printStackTrace();
		}
		//TODO remove mobile
		System.out.println("checkpoint 5.............. display unit for (attributeId + value)");
		Map<String, AttributeValueMeasureUnitDisplayBean> attIdAttValue2Unit = new HashMap<String, AttributeValueMeasureUnitDisplayBean>();
		Map<String, AttributeValueMeasureUnitDisplayBean> attIdAttValue2UnitMobile = new HashMap<String, AttributeValueMeasureUnitDisplayBean>();
		try (AttributeDAO dao = getDbAdapter().openDAO(AttributeDAO.class)) {
			for (AttributeValueMeasureUnitDisplayBean bean : dao.getAllDisplayUnit()) {	
				Object attValue;
				try {
					attValue = Double.valueOf(bean.getValue());
					attValue = DoubleUtils.formatDouble((Double) attValue);
				} catch (Exception e) {
					attValue = bean.getValue();
				}
				attIdAttValue2Unit.put(bean.getAttributeId() + "_" + attValue, bean);
				attIdAttValue2UnitMobile.put(bean.getAttributeId() + "_" + bean.getValueId(), bean);
			}
		} catch (Exception e) {
			System.err.println("error display unit for (attributeId + value)");
			e.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.DISPLAY_UNIT_FOR_ATT_VALUE).putAll(attIdAttValue2Unit);
			api.getCacheMap(CacheFields.DISPLAY_UNIT_FOR_ATT_VALUE_MOBILE).putAll(attIdAttValue2UnitMobile);
		} catch (Exception e) {
			System.err.println("error display unit for (attributeId + value)");
			e.printStackTrace();
		}
		
		System.out.println("checkpoint 6.............. keyword => url");
		Map<String, KeywordBean> keyword2Url = new HashMap<>();
		try (KeywordDAO dao = getDbAdapter().openDAO(KeywordDAO.class)) {
			for (KeywordBean bean : dao.getKeywords()) {
				keyword2Url.put(bean.getKeyword(), bean);
			}
		} catch (Exception e) {
			System.err.println("error keyword => url");
			e.printStackTrace();
		}
		try {
			api.getCacheMap(CacheFields.KEYWORD).clear();
			api.getCacheMap(CacheFields.KEYWORD).putAll(keyword2Url);
		} catch (Exception e) {
			System.err.println("error keyword => url");
			e.printStackTrace();
		}
	}
}
