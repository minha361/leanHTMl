/**
 * 
 */
package com.adr.bigdata.index.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

/**
 * @author ndn
 *
 */
public class AdministrativeUnitTransformer extends Transformer {
	public static final String FIELD_SERVERD_PROVINCE_IDS = "served_province_ids";
	public static final String FIELD_SERVERD_DISTRICT_IDS = "served_district_ids";
	public static final String FIELD_SERVERD_WARD_IDS = "served_ward_ids";

	public static final String PROVINCE_ID = "Id";
	public static final String DISTRICT_ID = "id";
	public static final String WARD_ID = "id";
	public static final String LIST_DISTRICT_ID = "LstDistrictId";
	public static final String LIST_WARD_ID = "LstWardId";

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		List<String> provinceIds = new ArrayList<String>();
		List<String> districtIds = new ArrayList<String>();
		List<String> wardIds = new ArrayList<String>();

		String s = (String) row.get("AdministrativeUnit");
		if (s == null) {
			provinceIds.add("0");
			districtIds.add("0");
			wardIds.add("0");
		} else {
			JSONArray lstProvinces = (JSONArray) JSONValue.parse(s);
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

		row.put(FIELD_SERVERD_PROVINCE_IDS, provinceIds);
		row.put(FIELD_SERVERD_DISTRICT_IDS, districtIds);
		row.put(FIELD_SERVERD_WARD_IDS, wardIds);
		row.remove("AdministrativeUnit");

		return row;
	}
}
