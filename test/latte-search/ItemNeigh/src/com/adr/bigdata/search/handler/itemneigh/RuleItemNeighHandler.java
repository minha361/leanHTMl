package com.adr.bigdata.search.handler.itemneigh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.json.RequestUtil;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;

import com.adr.bigdata.search.handler.BaseAdrSearchHandler;
import com.adr.bigdata.search.itemneigh.ItemNeighsParams;
import com.adr.bigdata.search.itemneigh.ItemNeighsUtils;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.CommonUltils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.adr.bigdata.search.vo.itemneigh.Tuple2;
import com.ndn.expression.ExpressionTreeBuilder;
import com.ndn.expression.Infix2PostfixConverter;
import com.ndn.expression.tree.Tree;

public class RuleItemNeighHandler extends BaseAdrSearchHandler {
	private int poolSize = 4;
	private int poolBufferSize = 256;

	private ExpressionTreeBuilder eBuilder = new ExpressionTreeBuilder();
	private Infix2PostfixConverter converter = new Infix2PostfixConverter();
	private ConcurrentHashMap<String, Map<Tree, Tuple2<String, String>>> ruleSet = new ConcurrentHashMap<>();

	public RuleItemNeighHandler() {
		setPoolSize(poolSize);
		setPoolBufferSize(poolBufferSize);
	}

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			poolSize = Integer.parseInt(core.getCoreDescriptor().getCoreProperty("itemneigh.poolSize", "4"));
			poolBufferSize = Integer
					.parseInt(core.getCoreDescriptor().getCoreProperty("itemneigh.poolBufferSize", "1024"));
			loadRules(core.getResourceLoader());
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		long start = System.currentTimeMillis();
		SolrParams params = req.getParams();
		if (params.getBool("init", false)) {
			init(rsp);
			return;
		}

		// Get full product information
		Map<Tree, Tuple2<String, String>> rules = ruleSet.get("rules");
		String _productId = params.get(ItemNeighsParams.PRODUCT_ID);
		String[] productIds = _productId == null ? new String[0] : _productId.split(",");
		int limit = params.getInt(ItemNeighsParams.LIMIT, 10);

		SolrParams paramGetProduct = new SolrParamsBuilder().keyword("*:*").collapse(ProductFields.PRODUCT_ID, null)
				.filter(ProductFields.PRODUCT_ID, productIds).limit(10).getParams();
		getLogger().debug("paramGetProduct: {}", paramGetProduct);
		SolrQueryResponse response = execute(null, "/select", paramGetProduct);
		List<Map<String, Object>> docs = getDocsAsMap(req.getSearcher(), response);
		getLogger().debug("result docs size: {}", docs.size());
		// end --- Get full product information

		// Get recommend product id by rule
		List<SolrParams> _recommendParams = ItemNeighsUtils.getRecommendSolrParams(docs, limit, rules);
		List<SolrParams> recommendParams = new ArrayList<>();
		for (SolrParams _params : _recommendParams) {
			recommendParams.add(wrapDefaultsAndAppends(_params));
		}
		getLogger().debug("recommendParams: {}", recommendParams);
		Set<String> ruledProducts = new HashSet<>();
		if (!recommendParams.isEmpty()) {
			List<SolrQueryResponse> recResults = super.executeParallel(null, "/select", recommendParams);
			getLogger().debug("recResults: {}", recResults);

			for (SolrQueryResponse recResult : recResults) {
				List<String> pIds = CommonUltils.getField(req.getSearcher(), recResult, ProductFields.PRODUCT_ID);
				for (String pid : pIds) {
					ruledProducts.add(ProductFields.PRODUCT_ID + ":" + pid + "^2");
				}
			}
		}
		// end --- Get recommend product id by rule
		getLogger().debug("profilling 1: {}", (System.currentTimeMillis() - start));
		List<String> reccommendQ = new ArrayList<>();
		reccommendQ.addAll(ruledProducts);
		reccommendQ.addAll(getCats(docs));
		getLogger().debug("profilling 3: {}", (System.currentTimeMillis() - start));
		SolrParams finalParams = ItemNeighsUtils.getRecommendSolrParams(reccommendQ, limit, params);
		req.setParams(finalParams);
		RequestUtil.processParams(this, req, defaults, appends, invariants);
		getLogger().debug("final params: {}", finalParams);
		super.execute("/select", req, rsp);
	}

	private void init(SolrQueryResponse rsp) {
		try {
			loadRules(this.core.getResourceLoader());
			rsp.add("status", "Chưa thấy lỗi gì, cứ check lại thử xem -_-");
		} catch (Exception e) {
			rsp.add("status", "Có lỗi đây này, \n" + e.getMessage());
			rsp.add("Exception", e);
		}
	}

	private List<String> getCats(List<Map<String, Object>> docs) {
		List<String> result = new ArrayList<>();
		for (Map<String, Object> doc : docs) {
			if (doc.containsKey(ProductFields.CATEGORY_ID) && doc.get(ProductFields.CATEGORY_ID) != null) {
				String catId = doc.get(ProductFields.CATEGORY_ID).toString();
				result.add(ProductFields.CATEGORY_ID + ":" + catId);
			}
		}
		return result;
	}

	private void loadRules(SolrResourceLoader resourceLoader) throws IOException {
		String file = (String) getInitArgs().get("rule");
		InputStream is = resourceLoader.openResource(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line;
		Map<String, Tuple2<String, String>> map = new HashMap<String, Tuple2<String, String>>();
		while ((line = reader.readLine()) != null) {
			String[] ss = line.split("=>");
			if (ss.length > 1) {
				if (ss.length > 2) {
					map.put(ss[0].trim(), new Tuple2<String, String>(ss[1].trim(), ss[2].trim()));
				} else {
					map.put(ss[0].trim(), new Tuple2<String, String>(ss[1].trim(), null));
				}
			}
		}

		Map<Tree, Tuple2<String, String>> mapRule = new HashMap<Tree, Tuple2<String, String>>();
		int i = 0;
		for (String rule : map.keySet()) {
			try {
				mapRule.put(this.eBuilder.build(this.converter.convert(rule), i), map.get(rule));
			} catch (RuntimeException e) {
				getLogger().error("Loi khi dung cay bieu thuc", e);
			}
			i++;
		}
		this.ruleSet.put("rules", mapRule);
	}

	private List<Map<String, Object>> getDocsAsMap(ResultContext rc, SolrIndexSearcher searcher)
			throws IOException {
		DocList docs = rc.docs;
		List<Map<String, Object>> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Map<String, Object> map = new HashMap<>();
			Document doc = searcher.doc(id);
			for (IndexableField field : doc.getFields()) {
				Object[] os = doc.getValues(field.name());
				if (os != null) {
					if (os.length > 1) {
						List<String> tmpList = new ArrayList<String>();
						for (int i = 0; i < os.length; i++) {
							tmpList.add(os[i].toString());
						}
						map.put(field.name(), tmpList);
					} else {
						map.put(field.name(), os[0]);
					}
				}
			}
			result.add(map);
		}
		return result;
	}

	private List<Map<String, Object>> getDocsAsMap(SolrDocumentList docs) throws IOException {
		List<Map<String, Object>> result = new ArrayList<>();
		for (SolrDocument doc : docs) {
			result.add(doc);
		}
		return result;
	}

	/**
	 * used for ruled recommend only
	 */
	private List<Map<String, Object>> getDocsAsMap(SolrIndexSearcher searcher, SolrQueryResponse rsp)
			throws IOException {
		Object response = rsp.getValues().get("response");
		if (response == null) {
			return Collections.emptyList();
		}
		if (response instanceof ResultContext) {
			return getDocsAsMap((ResultContext) response, searcher);
		}
		if (response instanceof SolrDocumentList) {
			return getDocsAsMap((SolrDocumentList) response);
		}
		return Collections.emptyList();
	}
}
