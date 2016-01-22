package com.adr.bigdata.search.product.fe.logic.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.product.fe.logic.ModeledLogic;
import com.adr.bigdata.search.product.fe.model.CategoryModel;
import com.adr.bigdata.search.product.utils.SimpleOrderedMapUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Joiner;
import com.nhb.eventdriven.Callable;

public class SuggestionLogic extends ModeledLogic {
	private static final String SUGGEST_Q = "{!edismax qf=product_item_name pf=product_item_name^3 pf2=product_item_name pf3=product_item_name^2 v=$keywordFuzzy} "
			+ "{!edismax qf=product_item_name pf=product_item_name^3 pf2=product_item_name pf3=product_item_name^2 v=$keywordPrefix}";

	private CategoryModel categoryModel;

	private void init(SolrQueryRequest req) throws Exception {
		if (categoryModel == null) {
			categoryModel = modelFactory.getModel(CategoryModel.class, req.getCore());
		}
	}

	public SuggestionLogic(ModelFactory modelFactory) {
		super(modelFactory);
	}

	@Override
	public void execute(SolrQueryRequest req, SolrQueryResponse rsp, Callable callBack) throws Exception {
		init(req);

		String keyword = req.getParams().get(KEYWORD, "*:*");
		SolrParamsBuilder paramsBuilder = new SolrParamsBuilder().facet(1).facetField(ProductFields.CATEGORY_ID);
		Object[] fuzzy = fuzzy(keyword);
		getLogger().debug("terms: {}", fuzzy[2]);
		if (keyword.equals("*:*")) {
			paramsBuilder.keyword(keyword);
		} else {
			paramsBuilder.keyword(SUGGEST_Q);
			paramsBuilder.add("keywordFuzzy", (String) fuzzy[0]);
			paramsBuilder.add("keywordPrefix", (String) fuzzy[1]);
			req.setParams(paramsBuilder.getParams());
		}
		paramsBuilder.addAdd(req.getParams());
		req.setParams(paramsBuilder.getParams());
		if (callBack != null) {
			callBack.call(fuzzy[2]);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object... others) throws Exception {
		List<String> terms = (List<String>) others[0];
		getLogger().debug("terms: {}", terms);
		proccessPName(req, rsp);
		proccessCategory(req, rsp, terms);

		rsp.getValues().remove("response");
		rsp.getValues().remove("facet_counts");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void proccessCategory(SolrQueryRequest req, SolrQueryResponse rsp, List<String> terms) throws IOException {
		NamedList catFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", "facet_fields",
				ProductFields.CATEGORY_ID);

		Map<Integer, Integer> catFacetMap = new HashMap<>();
		for (int i = 0; i < catFacet.size(); i++) {
			try {
				int catId = Integer.parseInt(catFacet.getName(i));
				int count = (int) catFacet.getVal(i);
				catFacetMap.put(catId, count);
			} catch (Exception e) {
				getLogger().warn("", e);
			}
		}
		Map<Integer, CategoryBean> _result = categoryModel.getCategoryLevel1Of(catFacetMap.keySet());
		Map<Integer, CategoryVO> result = new HashMap<>();
		for (Entry<Integer, CategoryBean> e : _result.entrySet()) {
			if (result.containsKey(e.getValue().getId())) {
				result.get(e.getValue().getId()).count += catFacetMap.get(e.getKey());
			} else {
				CategoryBean bean = e.getValue();
				CategoryVO vo = new CategoryVO(bean.getName(), bean.getId(), genSuggestedKeyword(rsp, terms),
						catFacetMap.get(e.getKey()));
				result.put(bean.getId(), vo);
			}
		}

		CategoryVO[] _sorted = new CategoryVO[result.size()];
		result.values().toArray(_sorted);
		Arrays.sort(_sorted);

		List<SimpleOrderedMap> listSuggestCategory = new ArrayList<>();

		for (CategoryVO vo : _sorted) {
			// only select the best 3 first class category
			if (listSuggestCategory.size() > 3) {
				break;
			}
			listSuggestCategory.add(vo.solrJson());
		}

		rsp.getValues().add("listSuggestCategory", listSuggestCategory);
	}

	@SuppressWarnings("unchecked")
	private String genSuggestedKeyword(SolrQueryResponse rsp, List<String> terms) throws IOException {
		Collection<String> pNames = (Collection<String>) rsp.getValues().get("listSuggestKeyword");
		int longestLength = terms.size();
		String longestKeyword = Joiner.on(' ').join(terms);
		for (String pName : pNames) {
			List<String> _terms = terms(pName);
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for (String term : terms) {
				if (pName.contains(term)) {
					sb.append(contain(_terms, term)).append(' ');
					count++;
				} else {
					String matchest = matchest(_terms, term);
					if (matchest != null) {
						sb.append(matchest).append(' ');
						count++;
					}
				}
			}
			if (count >= longestLength) {
				longestLength = count;
				longestKeyword = sb.toString();
			}
		}
		return longestKeyword;
	}

	private String contain(List<String> terms, String term) {
		for (String s : terms) {
			if (s.contains(term)) {
				return s;
			}
		}
		return "";
	}

	private String matchest(List<String> terms, String term) {
		int distance = Integer.MAX_VALUE;
		String matchest = null;
		for (String s : terms) {
			int d = minDistance(s, term);
			if (d < distance) {
				distance = d;
				matchest = s;
			}
		}
		if (distance <= 3) {
			if (term.length() >= 4 && term.length() < 6 && distance <= 1) {
				return matchest;
			}
			if (term.length() >= 6 && term.length() < 8 && distance <= 2) {
				return matchest;
			}
			if (term.length() >= 8 && distance <= 3) {
				return matchest;
			}
		}
		return null;
	}

	private List<String> terms(String name) throws IOException {
		try (StandardTokenizer _st = new StandardTokenizer()) {
			_st.setReader(new StringReader(name));
			try (ASCIIFoldingFilter st = new ASCIIFoldingFilter(_st, true)) {
				List<String> terms = new ArrayList<>();
				CharTermAttribute term = st.addAttribute(CharTermAttribute.class);
				st.reset();
				while (st.incrementToken()) {
					terms.add(term.toString().toLowerCase());
				}
				return terms;
			}
		}
	}

	/**
	 * Hàm này copy trên mạng
	 */
	public static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}

		// iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);

				// if last two chars equal
				if (c1 == c2) {
					// update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}

		return dp[len1][len2];
	}

	@SuppressWarnings({ "unchecked" })
	private void proccessPName(SolrQueryRequest req, SolrQueryResponse rsp) throws IOException {
		Set<String> result = null;
		Object suggestionResult = rsp.getValues().get("response");
		if (suggestionResult != null) {
			if (suggestionResult instanceof ResultContext) {
				result = getListSuggestion(((ResultContext) suggestionResult).docs, rsp, req);
			} else if (suggestionResult instanceof SolrDocumentList) {
				result = getListSuggestion((SolrDocumentList) suggestionResult);
			}
			if (result != null) {
				rsp.getValues().add("listSuggestKeyword", result);
			}
		}
	}

	public static final String KEYWORD = "keyword";
	public static final String USER_ID = "userid";

	class CategoryVO implements Comparable<CategoryVO> {
		private final String categoryName;
		private final int categoryId;
		private final String keyword;
		public int count = 0;

		public CategoryVO(String categoryName, int categoryId, String keyword, int count) {
			this.categoryName = categoryName;
			this.categoryId = categoryId;
			this.keyword = keyword;
			this.count = count;
		}

		@SuppressWarnings("rawtypes")
		public SimpleOrderedMap solrJson() {
			return SimpleOrderedMapUtils.create("categoryName", categoryName, "categoryId", categoryId, "keyword",
					keyword);
		}

		@Override
		public int compareTo(CategoryVO o) {
			return Integer.compare(o.count, this.count);
		}
	}

	private static Set<String> getListSuggestion(DocList docs, SolrQueryResponse rsp, SolrQueryRequest req)
			throws IOException {
		Set<String> result = new LinkedHashSet<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			if (result.size() >= 10) {
				break;
			}
			int id = it.next();
			Document doc = req.getSearcher().doc(id);
			for (IndexableField field : doc.getFields()) {
				if (field.name().equals(ProductFields.PRODUCT_ITEM_NAME)) {
					Object[] os = doc.getValues(field.name());
					if (os != null && os.length != 0) {
						result.add(((String) os[0]).toLowerCase().trim());
					}
				}
			}
		}

		return result;
	}

	private static Set<String> getListSuggestion(SolrDocumentList docList) {
		if (docList == null) {
			return Collections.emptySet();
		}
		Set<String> listSuggestKeyword = new LinkedHashSet<>();
		for (SolrDocument doc : docList) {
			if (listSuggestKeyword.size() >= 10) {
				break;
			}
			IndexableField value = (IndexableField) doc.getFirstValue(ProductFields.PRODUCT_ITEM_NAME);
			listSuggestKeyword.add(value.stringValue().toLowerCase().trim());
		}

		return listSuggestKeyword;
	}

	/**
	 * Nếu term quá ngắn (<=3 kí tự) thì không fuzzy <br />
	 * Nếu term từ 4 đến 6 kí tự thì cho phép distance là 1 <br />
	 * Từ 7 kí tự trở đi, cho phép distance là 2. <b<hehe</b>
	 */
	private Object[] fuzzy(String keyword) throws IOException {
		try (StandardTokenizer st = new StandardTokenizer()) {
			st.setReader(new StringReader(keyword));
			CharTermAttribute term = st.addAttribute(CharTermAttribute.class);
			st.reset();
			StringBuffer sbFuzzy = new StringBuffer();
			StringBuffer sbPrefix = new StringBuffer();
			List<String> terms = new ArrayList<>();
			while (st.incrementToken()) {
				String word = term.toString().toLowerCase();
				if (word.length() > 3 && word.length() < 7) {
					sbFuzzy.append(word).append("~").append(1).append(" ");
				} else if (word.length() >= 7) {
					sbFuzzy.append(word).append("~").append(2).append(" ");
				} else {
					sbFuzzy.append(word).append(" ");
				}
				sbPrefix.append(word).append("* ");
				terms.add(word);
			}
			return new Object[] { sbFuzzy.toString(), sbPrefix.toString(), terms };
		}
	}

	public static void main(String[] args) {
	}
}
