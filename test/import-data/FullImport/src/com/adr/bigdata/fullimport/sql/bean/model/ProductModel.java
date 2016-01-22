package com.adr.bigdata.fullimport.sql.bean.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.AttributeMappingBean;
import com.adr.bigdata.fullimport.sql.bean.LandingPageBean;
import com.adr.bigdata.fullimport.sql.dao.AttributeDAO;
import com.adr.bigdata.fullimport.sql.dao.LandingPageDAO;
import com.adr.bigdata.fullimport.sql.dao.ProductViewDAO;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class ProductModel extends AbstractSQLModel {
	public Map<Integer, List<AttributeMappingBean>> allAtts()
			throws InstantiationException, IllegalAccessException, SQLException {
		AttributeDAO attDAO = new AttributeDAO();
		attDAO.setStmt(stmt);
		return attDAO.allAttributesByPI();
	}

	public Map<Integer, Integer> allProductViews() throws InstantiationException, IllegalAccessException, SQLException {
		ProductViewDAO pvDAO = new ProductViewDAO();
		pvDAO.setStmt(stmt);
		return pvDAO.allProductView();
	}

	public Map<Integer, List<LandingPageBean>> allLandingPages()
			throws InstantiationException, IllegalAccessException, SQLException {
		LandingPageDAO lpDAO = new LandingPageDAO();
		lpDAO.setStmt(stmt);
		return lpDAO.allLandingPage();
	}

	public static Object[] administrativeUnit(String administrativeUnit) {
		List<String> provinceIds = new ArrayList<String>();
		List<String> districtIds = new ArrayList<String>();
		List<String> wardIds = new ArrayList<String>();

		if (administrativeUnit == null) {
			provinceIds.add("0");
			districtIds.add("0");
			wardIds.add("0");
		} else {
			JSONArray lstProvinces = (JSONArray) JSONValue.parse(administrativeUnit);
			if (lstProvinces.isEmpty()) {
				provinceIds.add("0");
				districtIds.add("0");
				wardIds.add("0");
			} else {
				for (Object provinceObj : lstProvinces) {
					JSONObject province = (JSONObject) provinceObj;
					int provinceId = (Integer) province.get(PROVINCE_ID);
					provinceIds.add(String.valueOf(provinceId));

					JSONArray lstDistricts = (JSONArray) province.get(LIST_DISTRICT_ID);
					if (lstDistricts == null || lstDistricts.isEmpty()) {
						districtIds.add(provinceId + "_0");
					} else {
						for (Object districtObj : lstDistricts) {
							JSONObject district = (JSONObject) districtObj;
							int districtId = (Integer) district.get(DISTRICT_ID);
							districtIds.add(String.valueOf(districtId));

							JSONArray lstWards = (JSONArray) district.get(LIST_WARD_ID);
							if (lstWards == null || lstWards.isEmpty()) {
								wardIds.add(districtId + "_0");
							} else {
								for (Object wardObj : lstWards) {
									JSONObject ward = (JSONObject) wardObj;
									int wardId = (Integer) ward.get(WARD_ID);
									wardIds.add(String.valueOf(wardId));
								}
							}
						}
					}
				}
			}
		}

		return new Object[] { provinceIds, districtIds, wardIds };
	}

	public static final String PROVINCE_ID = "Id";
	public static final String DISTRICT_ID = "id";
	public static final String WARD_ID = "id";
	public static final String LIST_DISTRICT_ID = "LstDistrictId";
	public static final String LIST_WARD_ID = "LstWardId";
}
