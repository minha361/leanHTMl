/**
 * 
 */
package com.adr.bigdata.indexingrd.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.solr.client.solrj.SolrServerException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.adr.bigdata.indexingrd.CacheFields;
import com.adr.bigdata.indexingrd.ProductFields;
import com.adr.bigdata.indexingrd.models.RdCachedModel;
import com.adr.bigdata.indexingrd.utils.JsonUtil;
import com.adr.bigdata.indexingrd.vos.ScoreVO;

/**
 * @author ndn
 *
 */
public class ScoreModel extends RdCachedModel {
	public static final String S_WHPIMID = "warehouseProductItemMappingId";
	public static final String S_SCORE = "score";
	public static final String S_JSON_SCORE = "jsonScore";
	public static final String S_DISTRICTS_JSON = "districtsJson";
	public static final String S_UPDATE_TIME = "updateTime";

	public static final String PROVINCE_ID = "Id";
	public static final String DISTRICT_ID = "id";
	public static final String WARD_ID = "id";
	public static final String LIST_DISTRICT_ID = "LstDistrictId";
	public static final String LIST_WARD_ID = "LstWardId";
	
	
	public void updateToRedis(List<ScoreVO> updatedScoreVOs) throws SolrServerException, IOException {
		try (Jedis j = getJedis(); Pipeline p = j.pipelined()) {
			for (ScoreVO vo : updatedScoreVOs) {
				String key = keyOfWPM(vo.getWpimId());//String.valueOf(vo.getWpimId());
//				int count = 0;
				if (vo.getScore() != null) {
					p.hset(key, ProductFields.BOOST_SCORE, String.valueOf(vo.getScore()));
//					count++;
				}
				if (vo.getJsonScore() != null) {
					addCityScore(vo.getJsonScore(), key);
//					count += addCityScore(vo.getJsonScore(), key);
				}
				if (vo.getDistrictsJson() != null) {
					List<String> provinceIds = new ArrayList<String>();
					List<String> districtIds = new ArrayList<String>();
					List<String> wardIds = new ArrayList<String>();

					JSONArray lstProvinces = (JSONArray) JSONValue.parse(vo.getDistrictsJson());
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

					p.hset(key, ProductFields.SERVED_PROVINCE_IDS, JsonUtil.collectionToJsonString(provinceIds));
					p.hset(key, ProductFields.SERVED_DISTRICT_IDS, JsonUtil.collectionToJsonString(districtIds));
					p.hset(key, ProductFields.SERVED_WARD_IDS, JsonUtil.collectionToJsonString(wardIds));
//					count++;
				}
			}
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
	
	private int addCityScore(String jsonScore, String docKey) {
		Jedis j = getJedis();
		Pipeline p = j.pipelined();
		int count = 0;
		JSONObject obj = (JSONObject) JSONValue.parse(jsonScore);
		for (Map.Entry<String, Object> e : obj.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			p.hset(docKey, ProductFields.cityScore(Integer.valueOf(key)), value.toString());
			count++;
		}
		p.syncAndReturnAll();
		return count;
	}
	
	public Map<String, String> getLastUpdateTimes(Set<Integer> wpimIds) {
		String key = CacheFields.SCORE;
		List<String> fields = new ArrayList<String>();
		for(Integer wpimId : wpimIds){
			fields.add(String.valueOf(wpimId));
		}
		return getTimes(key, fields);
	}

	public void updateTimeToCache(List<ScoreVO> updatedBeans) {
		try (Jedis j = getJedisUpdateTime(); Pipeline p = j.pipelined()) {
			String key = CacheFields.SCORE;
			for (ScoreVO updateBean : updatedBeans) {
				String field = String.valueOf(updateBean.getWpimId());
				p.hset(key, field, String.valueOf(updateBean.getUpdateTime()));
			}
			p.syncAndReturnAll();
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	
}
