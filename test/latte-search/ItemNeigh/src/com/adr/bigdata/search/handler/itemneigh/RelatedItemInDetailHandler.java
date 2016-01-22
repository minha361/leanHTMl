package com.adr.bigdata.search.handler.itemneigh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.itemneigh.ItemNeighsParams;
import com.adr.bigdata.search.itemneigh.ItemNeighsUtils;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.product.qparser.VinQueryParserPlugin;
import com.adr.bigdata.search.utils.CommonUltils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;
import com.nhb.common.Loggable;

import net.minidev.json.JSONObject;

public class RelatedItemInDetailHandler extends SearchHandler implements Loggable {
	private static final String REMOVE_SPECIAL_CHARACTER = "\"|:|'|-|=|!|\\{|\\}|\\+|\\\\|/|\\^|\\(|\\)|\\[|\\]|#";
	private static final String REC_SYS_ON = "on";
	private static final String REC_SYS_OFF = "off";
	private static final String REC_SYS_STATUS = "status";

	private static final Map<String, String> fieldMap = new HashMap<>();

	static {
		fieldMap.put("merchant_id", "merchant_id");
		fieldMap.put("warehouse_id", "warehouse_id");
		fieldMap.put("category_id", "category_id");
		fieldMap.put("brand_id", "brand_id");
		fieldMap.put("brand_name", "brand_name");
		fieldMap.put("product_id", "productid");
		fieldMap.put("product_item_id", "productitemid");

		fieldMap.put("rank", "rank");
		fieldMap.put("id", "id");
		fieldMap.put("algoId", "algoId");
		fieldMap.put("userId", "userId");
		fieldMap.put("itemId", "itemId");
		fieldMap.put("recItemId", "recItemId");
		fieldMap.put("recScore", "recScore");
		fieldMap.put("experimentId", "experimentId");
	}

	private String host = null;
	private Random random = new Random();
	private int rndTimeout = 30000;
	private boolean isGetFromRecSys = true;

	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList initParams) {
		super.init(initParams);
		getLogger().info("initParams: {}", initParams);
		String h = (String) (initParams.get("rdHost"));
		getLogger().info("rdHost: {}", h);
		if (!Strings.isNullOrEmpty(h)) {
			this.host = h;
		}
		try {
			rndTimeout = (Integer) initParams.get("rdTimeout");
		} catch (Exception ex) {
			//use the default
		}
	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String recSysCmd = req.getParams().get("recsys", null);
		if (recSysCmd != null) {
			proccessCmd(recSysCmd, req, rsp);
		} else {
			if (isGetFromRecSys) {
				random.setSeed(System.currentTimeMillis());
				int randomValue = random.nextInt(4);
				if (randomValue == 1) {
					long start = System.currentTimeMillis();
					resultFromRD(req, rsp, 1);
					getLogger().info("RNDQTIME_TYPE1...{}", System.currentTimeMillis() - start);
				} else if (randomValue == 2) {
					long start = System.currentTimeMillis();
					resultFromRD(req, rsp, 2);
					getLogger().info("RNDQTIME_TYPE2...{}", System.currentTimeMillis() - start);
				} else {
					long start = System.currentTimeMillis();
					resultFromBigdata(req, rsp);
					getLogger().info("BIGDATAQTIME_TYPE0...{}", System.currentTimeMillis() - start);
				}
			} else {
				long start = System.currentTimeMillis();
				resultFromBigdata(req, rsp);
				getLogger().info("BIGDATAQTIME_TYPE0...{}", System.currentTimeMillis() - start);
			}
		}
	}

	private void proccessCmd(String recSysCmd, SolrQueryRequest req, SolrQueryResponse rsp) {
		if (recSysCmd.equalsIgnoreCase(REC_SYS_OFF)) {
			isGetFromRecSys = false;
			rsp.add("annoucement", "You have turned off recSys");
		} else if (recSysCmd.equalsIgnoreCase(REC_SYS_ON)) {
			isGetFromRecSys = true;
			rsp.add("annoucement", "You have turned on recSys");
		} else if (recSysCmd.equalsIgnoreCase(REC_SYS_STATUS)) {
			rsp.add("RecSys Status", isGetFromRecSys);
		}
	}

	private void resultFromBigdata(SolrQueryRequest req, SolrQueryResponse rsp) {
		SolrParams params = req.getParams();
		int catId = params.getInt(ItemNeighsParams.CATEGORY_ID, -1);
		int productId = params.getInt(ItemNeighsParams.PRODUCT_ID, 0);
		String productItemName = params.get(ItemNeighsParams.PRODUCT_ITEM_NAME);
		productItemName = productItemName.replaceAll(REMOVE_SPECIAL_CHARACTER, " ");
		double price = params.getDouble(ItemNeighsParams.PRICE, 1000000);
		String cityId = params.get(VinQueryParserPlugin.P_CITY);
		String districtId = params.get(VinQueryParserPlugin.P_DISTRICT);

		SolrParams queryParams = new SolrParamsBuilder().keyword("*:*")
				.filter(ProductFields.CATEGORY_PATH, String.valueOf(catId))
				.filter("-" + ProductFields.PRODUCT_ID, String.valueOf(productId))
				.add(VinQueryParserPlugin.P_CITY, cityId).add(VinQueryParserPlugin.P_DISTRICT, districtId)
				.add("df", ProductFields.PRODUCT_ITEM_NAME)
				.add("rq", "{!rerank reRankQuery=$name reRankDocs=$reRankDocs}").add("name", productItemName)
				.sort("abs(sub($final_price," + price + "))", "asc").getParams();

		req.setParams(queryParams);
		super.handleRequest(req, rsp);

		try {
			List<Map<String, Object>> rs = CommonUltils.getDocsAsMap(req.getSearcher(), rsp);
			getLogger().debug("result if bigdata: {}", rs);
			for (Map<String, Object> r : rs) {
				r.put("experimentId", -1);
				r.put("rank", -1);
			}
			SolrDocumentList res = toNamedList(rs);
			rsp.add("numFound", rs.size());
			rsp.add("listProduct", res);
		} catch (IOException e) {
			getLogger().error("", e);
		}
	}

	private void resultFromRD(SolrQueryRequest req, SolrQueryResponse rsp, int contextId) {
		SolrParams params = req.getParams();

		int productItemId = params.getInt(ItemNeighsParams.PRODUCT_ITEM_ID, 0);
		String userId = params.get(ItemNeighsParams.USER_ID);
		String cityId = params.get(VinQueryParserPlugin.P_CITY);
		String districtId = params.get(VinQueryParserPlugin.P_DISTRICT);

		List<JSONObject> rdResult = checkOnsite(req.getCore(), userId, productItemId, cityId, districtId, 5, contextId);

		SolrParams nonParams = new SolrParamsBuilder().keyword("*:*").limit(0).getParams();
		req.setParams(nonParams);
		super.handleRequest(req, rsp);

		SolrDocumentList res = toNamedList(rdResult);
		rsp.add("numFound", rdResult.size());
		rsp.add("listProduct", res);
	}

	private SolrDocumentList toNamedList(List<? extends Map<String, Object>> datas) {
		SolrDocumentList res = new SolrDocumentList();
		for (Map<String, Object> data : datas) {
			SolrDocument doc = new SolrDocument();
			for (Entry<String, Object> e : data.entrySet()) {
				if (fieldMap.containsKey(e.getKey())) {
					doc.addField(fieldMap.get(e.getKey()), e.getValue());
				}
			}
			res.add(doc);
		}

		return res;
	}

	private List<JSONObject> checkOnsite(SolrCore core, String userId, int productItemId, String cityId,
			String districtId, int limit, int contextId) {
		Map<Integer, JSONObject> recProductItems = ItemNeighsUtils.getProductItemIdsFromRD(this.host, productItemId,
				userId, contextId, rndTimeout);
		if (recProductItems == null || recProductItems.isEmpty()) {
			return Collections.emptyList();
		}

		String[] ids = new String[recProductItems.size()];
		int i = 0;
		for (Integer id : recProductItems.keySet()) {
			ids[i] = String.valueOf(id);
			i++;
		}
		SolrParams params = new SolrParamsBuilder().keyword("*:*").filter(ProductFields.PRODUCT_ITEM_ID, ids)
				.add(VinQueryParserPlugin.P_CITY, cityId).add(VinQueryParserPlugin.P_DISTRICT, districtId).getParams();
		params = SolrParams.wrapAppended(params, appends);
		params = SolrParams.wrapDefaults(params, defaults);
		getLogger().debug("query params for rnd: {}", params);
		SolrQueryRequest req = new LocalSolrQueryRequest(core, params);

		try {
			SolrQueryResponse rsp = new SolrQueryResponse();
			core.getRequestHandler("/select").handleRequest(req, rsp);
			List<Map<String, Object>> rs = CommonUltils.getDocsAsMap(req.getSearcher(), rsp);
			getLogger().debug("- result from rnd after filter: {}", rs);
			List<JSONObject> result = new ArrayList<>();
			for (Map<String, Object> e : rs) {
				Integer piid = Integer.parseInt(e.get("product_item_id").toString());
				getLogger().debug("piid: {}", piid);
				if (recProductItems.containsKey(piid)) {
					JSONObject piObj = recProductItems.get(piid);
					piObj.putAll(e);
					result.add(piObj);
				}
			}
			getLogger().debug("result from rnd after filter: {}", result);
			sort(result, "rank");
			int size = result.size() > limit ? limit : result.size();
			if (size == 0) {
				return Collections.emptyList();
			}
			return result.subList(0, size);
		} catch (Exception e) {
			getLogger().error("", e);
			return Collections.emptyList();
		} finally {
			req.close();
		}

	}

	private void sort(List<JSONObject> result, String field) {
		result.sort(new Comparator<JSONObject>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				try {
					Comparable v1 = (Comparable) o1.get(field);
					Comparable v2 = (Comparable) o2.get(field);
					return v1.compareTo(v2);
				} catch (Exception e) {
					getLogger().debug("error when get field to compare");
					return 0;
				}
			}
		});
	}

}
