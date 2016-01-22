package com.adr.bigdata.fullimport.redis.model;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.adr.bigdata.fullimport.ProductFields;
import com.adr.bigdata.fullimport.sql.bean.AttributeMappingBean;
import com.adr.bigdata.fullimport.sql.bean.CategoryBean;
import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.LandingPageBean;
import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.WarehouseProductItemMappingBean;
import com.adr.bigdata.fullimport.sql.bean.model.ProductModel;
import com.google.common.base.Joiner;

public class FullUpdateRedisModel extends RdCacheModel {
	public boolean add(WarehouseProductItemMappingBean wpmBean, List<AttributeMappingBean> attBeans,
			CategoryBean catBean, CommisionFeeBean commsion, Integer view,
			NotApplyCommisionFeeBean notApplyCommissionBean, List<LandingPageBean> landingPageBeans) {
		Map<String, Object> data = new HashMap<>();
		
		data.put(ProductFields.PRODUCT_ITEM_ID_WAREHOUSE_ID ,wpmBean.getProductItemWarehouseId());
		data.put(ProductFields.BARCODE ,wpmBean.getBarcode());
		data.put(ProductFields.BOOST_SCORE ,wpmBean.getBoostScore());
		data.put(ProductFields.BRAND_NAME ,wpmBean.getBrandName());
		data.put(ProductFields.CATEGORY_ID ,wpmBean.getCategoryId());
		data.put(ProductFields.CATEGORY_PATH ,catBean == null ? null : catBean.getPath());
		data.put(ProductFields.COMMISION_FEE ,commsion == null ? 0 : commsion.getValue());
		data.put(ProductFields.CREATE_TIME ,wpmBean.getCreateTime());
		data.put(ProductFields.FINISH_TIME_DISCOUNT ,wpmBean.getFinishDateDiscount());
		data.put(ProductFields.IS_NOT_APPLY_COMMISION ,notApplyCommissionBean == null ? false : notApplyCommissionBean.isNotApplyCommissionFee());
		data.put(ProductFields.IS_PROMOTION ,wpmBean.getIsPromotion());
		data.put(ProductFields.IS_PROMOTION_MAPPING ,wpmBean.getIsPromotionMapping());
		data.put(ProductFields.MERCHANT_ID ,wpmBean.getMerchantId());
		data.put(ProductFields.MERCHANT_NAME ,wpmBean.getMerchantName());
		data.put(ProductFields.MERCHANT_PRODUCT_ITEM_SKU ,wpmBean.getMerchantProductItemSKU());
		data.put(ProductFields.ON_SITE ,wpmBean.getOnsite());
		data.put(ProductFields.PRICE_FLAG ,wpmBean.getPriceFlag());
		data.put(ProductFields.PRODUCT_ID ,wpmBean.getProductId());
		data.put(ProductFields.PRODUCT_ITEM_ID ,wpmBean.getProductItemId());
		data.put(ProductFields.PRODUCT_ITEM_ID_MERCHANT_ID ,wpmBean.getProductItemId() + "_" + wpmBean.getMerchantId());
		data.put(ProductFields.PRODUCT_ITEM_NAME ,wpmBean.getProductItemName());
		data.put(ProductFields.PRODUCT_ITEM_POLICY ,wpmBean.getProductItemPolicy());
		data.put(ProductFields.PRODUCT_ITEM_TYPE ,wpmBean.getProductItemType());
		data.put(ProductFields.PROMOTION_PRICE ,wpmBean.getPromotionPrice());
		data.put(ProductFields.RECEIVED_CITY_ID ,wpmBean.getCityIds());
		data.put(ProductFields.SELL_PRICE ,wpmBean.getSellPrice());

		Object[] administrativeUnit = ProductModel.administrativeUnit(wpmBean.getAdministrativeUnit());
		data.put(ProductFields.SERVED_DISTRICT_IDS ,administrativeUnit[1]);
		data.put(ProductFields.SERVED_PROVINCE_IDS ,administrativeUnit[0]);
		data.put(ProductFields.SERVED_WARD_IDS ,administrativeUnit[2]);

		data.put(ProductFields.START_TIME_DISCOUNT ,wpmBean.getStartDateDiscount());
		data.put(ProductFields.VIEWED_DAY ,view);
		data.put(ProductFields.WAREHOUSE_ID ,wpmBean.getWarehouseId());
		data.put(ProductFields.WEIGHT ,wpmBean.getWeight());

		data.put(ProductFields.PRODUCT_ITEM_GROUP ,wpmBean.getProductId());

		if (landingPageBeans != null && !landingPageBeans.isEmpty()) {
			for (LandingPageBean bean : landingPageBeans) {
				data.put(ProductFields.landingPage(bean.getLandingPageId()), bean.getPriority());
				data.put(ProductFields.landingPageGroup(bean.getLandingPageGroupId()), bean.getPriority());
			}
		}
		
		if (wpmBean.getScore() != null && !wpmBean.getScore().isEmpty()) {
			for (Entry<Integer, Double> e : wpmBean.getScore().entrySet()) {
				data.put(ProductFields.cityScore(e.getKey()), e.getValue());
			}
		}

		if (attBeans != null && !attBeans.isEmpty()) {
			for (AttributeMappingBean bean : attBeans) {
				data.put(ProductFields.attrInt(bean.getAttributeId()), bean.getAttributeValueId());
			}
		}
		
		data.put(ProductFields.BRAND_ID ,wpmBean.getBrandId());
		put(String.valueOf(wpmBean.getProductItemWarehouseId()), convert(data));		
		return true;
	}
	
	private Map<String, String> convert(Map<String, Object> oldData) {
		Map<String, String> rs = new HashMap<>();
		
		for (Entry<String, Object> e : oldData.entrySet()) {
			String key = e.getKey();
			Object val = e.getValue();
			if (val == null) {
				continue;
			}
			String sVal = val.toString();
			if (val.getClass().isArray()) {
				Object[] os = new Object[Array.getLength(val)];
				for (int i = 0; i < os.length; i++) {
					os[i] = Array.get(val, i);
				}
				sVal = '[' + Joiner.on(',').join(os) + ']';
			}
			rs.put(key, sVal);
		}
		return rs;
	}
}
