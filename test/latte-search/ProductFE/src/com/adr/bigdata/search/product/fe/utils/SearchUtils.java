package com.adr.bigdata.search.product.fe.utils;

import java.util.List;

import com.adr.bigdata.search.model.Model;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class SearchUtils implements Model {
	public static final String AUTO_PARSER = "autophrasingParser";
	private static final String REMOVE_SPECIAL_CHARACTER = "\"|:|'|-|=|!|\\{|\\}|\\+|\\\\|/|\\^|\\(|\\)|\\[|\\]|#";
	
	public static final String QUERY = "_query_:{!q.op=AND df=barcode v=$keyword} "
			+ "_query_:{!autophrasingParser qf=product_item_name^5 pf=product_item_name^10 bq='@bq' mm=2<75% v=$keyword} "
			+ "_query_:{!q.op=AND df=brand_name v=$keyword} "
			+ "_query_:{!q.op=AND df=merchant_name v=$keyword} "
			+ "_query_:{!q.op=AND df=category_tree v=$keyword} "
			+ "_query_:{!vinQueryTagSearch tagBoost=5 q.op=AND df=tags v=$keyword}\r\n"
			+ "_query_:{!q.op=AND df=attribute_search v=$keyword}\r\n"
			+ "_query_:{!edismax qf=short_description^0.5 pf=short_description pf2=short_description pf3=short_description mm=100% v=$keyword}";
	
	public static SolrParamsBuilder search(SolrParamsBuilder builder, String keyword, List<String> prioCats) {
		if (Strings.isNullOrEmpty(keyword) || keyword.equals("*:*")) {
			return builder.keyword("*:*").add("df", ProductFields.PRODUCT_ITEM_NAME);
		} else {
			keyword = keyword.replaceAll(REMOVE_SPECIAL_CHARACTER, " ");
			if (prioCats == null || prioCats.isEmpty()) {
				return builder.keyword(queryWithBq("")).add("keyword", keyword);
			} else {
				String bq = Joiner.on(' ').join(prioCats);
				return builder.keyword(queryWithBq(bq)).add("keyword", keyword);
			}
		}
	}
	
	public static String queryWithBq(String bq) {
		return QUERY.replaceAll("@bq", bq);
	}
}
