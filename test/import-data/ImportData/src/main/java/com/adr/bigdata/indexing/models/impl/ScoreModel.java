/**
 * 
 */
package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.vos.ScoreVO;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

/**
 * @author ndn
 *
 */
public class ScoreModel extends SolrModel {
	public static final String S_WHPIMID = "warehouseProductItemMappingId";
	public static final String S_SCORE = "score";
	public static final String S_JSON_SCORE = "jsonScore";
	public static final String S_DISTRICTS_JSON = "districtsJson";
	public static final String S_UPDATE_TIME = "updateTime";

	private static final String PROVINCE_ID = "Id";
	private static final String DISTRICT_ID = "id";
	private static final String WARD_ID = "id";
	private static final String LIST_DISTRICT_ID = "LstDistrictId";
	private static final String LIST_WARD_ID = "LstWardId";

	/**
	 * @param wpimIds
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimeOfWpim(Set<Integer> wpimIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.SCORE)).getAll(wpimIds);
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<Integer, Long>();
		}
	}

	/**
	 * @param updatedScoreVOs
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void updateToSolr(List<ScoreVO> updatedScoreVOs) throws SolrServerException, IOException {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(0);
		for (ScoreVO vo : updatedScoreVOs) {
			SolrInputDocument doc = createDoc(vo);
			if (doc == null) {
				getLogger().error("No thing to update: {}", vo.toString());
			} else {
				docs.add(doc);
				if (docs.size() >= getNumDocPerRequest()) {
					getSolrClient().add(docs);
					docs = new ArrayList<SolrInputDocument>();
				}
			}
		}
		if (!docs.isEmpty()) {
			getSolrClient().add(docs);
		}
		if (isCommit()) {
			getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
		}
	}

	/**
	 * @param updatedScoreVOs
	 */
	public void updateToCache(List<ScoreVO> updatedScoreVOs) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		for (ScoreVO vo : updatedScoreVOs) {
			map.put(vo.getWpimId(), vo.getUpdateTime());
		}
		try {
			getCacheWrapper().getCacheMap(CacheFields.SCORE).putAll(map);
		} catch (Exception e) {
			getLogger().error("", e);

		}
	}

	private SolrInputDocument createDoc(ScoreVO vo) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, vo.getWpimId());
		int count = 0;
		if (vo.getScore() != null) {
			doc.addField(SolrFields.BOOTS_SCORE, new SingleMap("set", vo.getScore()));
			count++;
		}
		if (vo.getJsonScore() != null) {
			count += addCityScore(vo.getJsonScore(), doc);
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

			doc.addField(SolrFields.SERVED_PROVINCE_IDS, new SingleMap("set", provinceIds));
			doc.addField(SolrFields.SERVED_DISTRICT_IDS, new SingleMap("set", districtIds));
			doc.addField(SolrFields.SERVED_WARD_IDS, new SingleMap("set", wardIds));
			count++;
		}

		if (count == 0) {
			return null;
		}
		return doc;
	}

	private int addCityScore(String jsonScore, SolrInputDocument doc) {
		int count = 0;
		JSONObject obj = (JSONObject) JSONValue.parse(jsonScore);
		for (Map.Entry<String, Object> e : obj.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();

			doc.addField(String.format(SolrFields.CITY_SCORE, key), new SingleMap("set", value));
			count++;
		}
		return count;
	}
}
