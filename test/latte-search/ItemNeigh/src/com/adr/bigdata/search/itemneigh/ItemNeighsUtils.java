package com.adr.bigdata.search.itemneigh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.adr.bigdata.search.vo.itemneigh.Tuple2;
import com.adr.bigdata.search.ws.WSTools;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.ndn.expression.result.ResultTools;
import com.ndn.expression.tree.Tree;
import com.ndn.expression.tree.ValuedTree;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class ItemNeighsUtils {
	private static Logger log = LoggerFactory.getLogger(ItemNeighsUtils.class);

	private static String RD_TEMPLATE_WITH_USER = "/items4UserV2?itemId=%d&contextId=%d&userId=\"%s\"";
	private static String RD_TEMPLATE = "/items4UserV2?itemId=%d&contextId=%d";

	public static Map<Integer, JSONObject> getProductItemIdsFromRD(String host, int productItemId, String userId,
			int contextId, int timeout) {
		try {
			String link;
			if (!Strings.isNullOrEmpty(userId)) {
				link = String.format(RD_TEMPLATE_WITH_USER, productItemId, contextId, userId);
			} else {
				link = String.format(RD_TEMPLATE, productItemId, contextId);
			}
			link = "http://" + host + link;
			JSONArray arr = WSTools.getJSONArrayContent(link, timeout);
			log.debug("result from {}: {}", link, arr);
			Map<Integer, JSONObject> result = new HashMap<>();
			for (Object _o : arr) {
				try {
					JSONObject obj = (JSONObject) _o;
					Integer recId = (Integer) obj.get("recItemId");
					result.put(recId, obj);
				} catch (Exception e) {
				}
			}
			log.info("RnDSUCCESS...{}");
			return result;
		} catch (IOException e) {
			log.warn("RnDTIMEOUT...{}", e.getMessage());
			return Collections.emptyMap();
		}
	}

	public static Map<String, List<String>> getAppropriateRule(Map<Tree, Tuple2<String, String>> rules,
			Map<String, Object> map) {
		List<String> result = new ArrayList<String>();
		List<String> priority = new ArrayList<String>();
		for (Tree tree : rules.keySet()) {
			ValuedTree vTree = tree.assignValue(map);
			if (vTree.result()) {
				Tuple2<String, String> suggestion = rules.get(tree);
				result.add(ResultTools.mapName(suggestion._1, map));
				if (suggestion._2 != null) {
					priority.add(ResultTools.mapName(suggestion._2, map));
				}
			}
		}
		Map<String, List<String>> mapResult = new HashMap<String, List<String>>();
		mapResult.put("criteria", result);
		mapResult.put("priority", priority);
		return mapResult;
	}

	/**
	 * Hàm này từ thông tin sản phẩm đưa vào, nó sẽ tìm ra tập rule thỏa mãn từ
	 * đó đưa ra các quy tắc filter và sắp xếp (ưu tiên) Nếu không có quy tắc ưu
	 * tiên nào được phát hiện (vẫn có quy tắc filter nha) thì sắp xếp ngẫu
	 * nhiên
	 * 
	 */
	public static List<SolrParams> getRecommendSolrParams(List<Map<String, Object>> docs, int limit,
			Map<Tree, Tuple2<String, String>> rules) {
		List<SolrParams> recommendParams = new ArrayList<>();
		for (Map<String, Object> doc : docs) {
			Map<String, List<String>> suggestion = ItemNeighsUtils.getAppropriateRule(rules, doc);
			List<String> _criteria = suggestion.get("criteria");
			if (_criteria == null || _criteria.isEmpty()) {
				continue;
			}
			String[] criteria = new String[_criteria.size()];
			_criteria.toArray(criteria);
			List<String> priority = suggestion.get("priority");

			SolrParamsBuilder paramsBuilder = new SolrParamsBuilder().defType("edismax")
					.add("df", ProductFields.PRODUCT_ITEM_NAME).keyword("*:*").limit(limit)
					.filterWithExistField(criteria);
			if (!priority.isEmpty()) {
				paramsBuilder.add("bq", Joiner.on(' ').join(priority));
			} else {
				long sortSeed = System.currentTimeMillis() / 300000;
				paramsBuilder.sort(ProductFields.random(sortSeed), "desc");
			}

			SolrParams params = paramsBuilder.getParams();
			recommendParams.add(params);
		}
		return recommendParams;
	}

	public static SolrParams getRecommendSolrParams(Collection<String> reccommendQ, int limit, SolrParams oldParams) {
		String query = Joiner.on(" OR ").join(reccommendQ);
		SolrParamsBuilder paramsBuilder = new SolrParamsBuilder().keyword(query).limit(limit);
		long sortSeed = System.currentTimeMillis() / 150000;
		paramsBuilder.add("sort", "score desc, " + ProductFields.random(sortSeed) + " desc");
		String cityId = oldParams.get(ItemNeighsParams.CITY_ID);
		if (!Strings.isNullOrEmpty(cityId)) {
			paramsBuilder.add(ItemNeighsParams.CITY_ID, cityId);
		}
		String districtId = oldParams.get(ItemNeighsParams.DISTRICT_ID);
		if (!Strings.isNullOrEmpty(districtId)) {
			paramsBuilder.add(ItemNeighsParams.DISTRICT_ID, districtId);
		}
		return paramsBuilder.getParams();
	}
}