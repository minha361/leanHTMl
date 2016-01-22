package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.adr.bigdata.fullimport.sql.bean.WarehouseProductItemMappingBean;
import com.google.common.base.Strings;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class WarehouseProductItemMapper implements SQLMapper<WarehouseProductItemMappingBean> {

	Calendar cal = Calendar.getInstance();

	public WarehouseProductItemMapper() {
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public WarehouseProductItemMappingBean map(int row, ResultSet rs) throws SQLException {
		WarehouseProductItemMappingBean bean = new WarehouseProductItemMappingBean();
		bean.setProductItemWarehouseId(rs.getInt("ProductItemWarehouseId"));
		bean.setBarcode(rs.getString("Barcode"));
		bean.setWarehouseId(rs.getInt("WarehouseId"));
		bean.setProductId(rs.getInt("ProductId"));
		bean.setCategoryId(rs.getInt("CategoryId"));
		bean.setProductItemId(rs.getInt("ProductItemId"));
		bean.setSellPrice(rs.getDouble("SellPrice"));
		bean.setBrandName(rs.getString("BrandName"));
		bean.setBrandId(rs.getInt("BrandId"));
		bean.setProductItemName(rs.getString("ProductItemName"));
		bean.setCreateTime(rs.getTimestamp("CreateTime", cal).getTime());
		bean.setMerchantId(rs.getInt("MerchantId"));
		bean.setMerchantName(rs.getString("MerchantName"));
		bean.setMerchantProductItemSKU(rs.getString("merchantProductItemSKU"));
		bean.setIsPromotionMapping(rs.getBoolean("IsPromotionMapping"));
		bean.setIsPromotion(rs.getBoolean("IsPromotion"));
		try {
			bean.setPromotionPrice(rs.getDouble("PromotionPrice"));
			bean.setStartDateDiscount(rs.getTimestamp("StartDateDiscount", cal).getTime());
			bean.setFinishDateDiscount(rs.getTimestamp("FinishDateDiscount", cal).getTime());
		} catch (Exception e) {
			
		}

		String sCityIds = rs.getString("CityIds");
		if (!Strings.isNullOrEmpty(sCityIds)) {
			String[] arrCityIds = sCityIds.split(",");
			int[] cityIds = new int[arrCityIds.length];
			for (int i = 0; i < arrCityIds.length; i++) {
				try {
					cityIds[i] = Integer.parseInt(arrCityIds[i]);
				} catch (NumberFormatException e) {
					getLogger().warn("", e);
				}
			}
			bean.setCityIds(cityIds);
		}

		bean.setWeight(rs.getDouble("Weight"));
		bean.setProductItemType(rs.getInt("ProductItemType"));
		bean.setOnsite(rs.getInt("OnSite"));

		String sScore = rs.getString("Score");
		if (!Strings.isNullOrEmpty("Score")) {
			try {
				JSONObject _score = (JSONObject) JSONValue.parse(sScore);
				Map<Integer, Double> score = new HashMap<Integer, Double>();
				for (Entry<String, Object> e : _score.entrySet()) {
					score.put(Integer.parseInt(e.getKey()), Double.parseDouble(e.getValue().toString()));
				}
				bean.setScore(score); 
			} catch (Exception e) {
				getLogger().warn("", e);
			}
		}

		bean.setBoostScore(rs.getDouble("BoostScore"));
		bean.setAdministrativeUnit(rs.getString("AdministrativeUnit"));
		bean.setProductItemPolicy(rs.getInt("ProductItemPolicy"));

		bean.setPriceFlag(rs.getBoolean("PriceFlag"));

		return bean;
	}

}
