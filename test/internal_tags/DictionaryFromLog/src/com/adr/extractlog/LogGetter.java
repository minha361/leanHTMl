package com.adr.extractlog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.adr.extractlog.bean.LogBean;
import com.adr.extractlog.util.DBConnectionUtil;
import com.adr.extractlog.util.VietnameseUtil;
import com.adr.log.manager.ConfigManager;

public class LogGetter {
	/** true => don't tag Brand, false => also tag brand */
	private static final boolean LOAD_BRAND = false;
	
	private static final String GET_LOG_SQL_PATTERN = "SELECT product_item_id, product_name, referrer_url, category_id, category_name, category_path, brand_name FROM action_log WHERE action=1 AND environment_id=3 AND referrer_url Like 'https://www.adayroi.com/tim-kiem%'";

	private static final String UTF8 = "UTF-8";

	private final ScriptEngineManager factory = new ScriptEngineManager();
	private final ScriptEngine engine = factory.getEngineByName("JavaScript");

	private boolean hasMore = true;

	public static Connection conn = null;

	private static Set<String> brands = null;

	public List<LogBean> getLogs(int pageNumber) throws IOException,
			ClassNotFoundException, SQLException, ScriptException {
		if (brands == null) {
			brands = new HashSet<String>();
			if (LOAD_BRAND) {
				BrandGetter brandGetter = new BrandGetter();
				int brandPageNumber = 0;
				while (brandGetter.isHasMore()) {
					brands.addAll(brandGetter.getBrands(brandPageNumber));
					brandPageNumber++;
				}
			}

		}
		List<LogBean> logBeans = new ArrayList<LogBean>();
		Statement stmt = null;
		try {
			long now = System.currentTimeMillis();
			ConfigManager configManager = ConfigManager.getIntence();
			conn = DBConnectionUtil.getMySQLConnection();
			stmt = conn.createStatement();
			String sql;
			sql = GET_LOG_SQL_PATTERN
					+ String.format("limit %d, %d",
							pageNumber * configManager.getDocsPerPage(),
							configManager.getDocsPerPage());
			ResultSet rs = stmt.executeQuery(sql);

			int docNumber = 0;
			// STEP 5: Extract data from result set
			while (rs.next()) {
				docNumber++;
				LogBean logBean = createLogObject(rs);
				// ignore brand-keyword and  blank-keyword
				if (logBean.getKeyWord() != null
						&& logBean.getKeyWord().length() > 0
						&& !brands.contains(logBean.getKeyWord())) {
					logBeans.add(logBean);
				}
			}
			if (docNumber < configManager.getDocsPerPage()) {
				hasMore = false;
			} else {
				hasMore = true;
			}
			System.out.println("get 1 log's page: "
					+ (System.currentTimeMillis() - now));
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			// conn.close();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
		}
		return logBeans;
	}

	public LogBean createLogObject(ResultSet rs) throws SQLException,
			MalformedURLException, UnsupportedEncodingException,
			ScriptException {
		LogBean logBean = new LogBean();
		logBean.setRefUrl(rs.getString("referrer_url"));
		logBean.setKeyWord(getKeyword(rs.getString("referrer_url")));
		logBean.setProductItemName(rs.getString("product_name"));
		logBean.setProductItemId(rs.getLong("product_item_id"));
		logBean.setCategoryName(rs.getString("category_name"));
		logBean.setCategoryId(rs.getLong("category_id"));
		logBean.setCategoryPath(rs.getString("category_path"));
		logBean.setBrandName(rs.getString("brand_name"));

		// logBean.tagJson = "";// rs.getString("id");
		return logBean;
	}

	public String getKeyword(String refUrl) throws MalformedURLException,
			UnsupportedEncodingException, ScriptException {
		// System.out.println(refUrl);
		if (refUrl == null || refUrl.length() == 0) {
			return null;
		}
		URL url = new URL(refUrl);
		Map<String, String> queries = splitQuery(url);
		String searchKeyword = queries.get("q");
		if (searchKeyword != null) {
			searchKeyword = searchKeyword.replaceAll("\\+", " ");
			searchKeyword = searchKeyword.replaceAll("-", " ");
			searchKeyword = searchKeyword.replaceAll("/", " ");
			searchKeyword = searchKeyword.toLowerCase();
			searchKeyword = searchKeyword.trim();
			while (searchKeyword.contains("  ")) {
				searchKeyword = searchKeyword.replace("  ", " ");
			}
			searchKeyword = VietnameseUtil.unAccent(searchKeyword);
		}
		return searchKeyword;
	}

	public Map<String, String> splitQuery(URL url)
			throws UnsupportedEncodingException, ScriptException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		if (query != null) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				if (idx > 0) {
					try {// try decode raw url encode
						query_pairs
								.put(URLDecoder.decode(pair.substring(0, idx),
										UTF8), URLDecoder.decode(
										pair.substring(idx + 1), UTF8));
					} catch (Exception e) {// try decode javascript enconde
						query_pairs
								.put(URLDecoder.decode(pair.substring(0, idx),
										UTF8),
										unescapeUsingJavaScriptEngine(pair
												.substring(idx + 1)));
					}
				}
			}
		}
		return query_pairs;
	}

	// public static Map<String, List<String>> splitQuery(URL url) throws
	// UnsupportedEncodingException {
	// final Map<String, List<String>> query_pairs = new LinkedHashMap<String,
	// List<String>>();
	// final String[] pairs = url.getQuery().split("&");
	// for (String pair : pairs) {
	// final int idx = pair.indexOf("=");
	// final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx),
	// "UTF-8") : pair;
	// if (!query_pairs.containsKey(key)) {
	// query_pairs.put(key, new LinkedList<String>());
	// }
	// final String value = idx > 0 && pair.length() > idx + 1 ?
	// URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
	// query_pairs.get(key).add(value);
	// }
	// return query_pairs;
	// }

	private String unescapeUsingJavaScriptEngine(String jsEscapedString)
			throws ScriptException {
		String result = (String) engine.eval("unescape('" + jsEscapedString
				+ "')");
		return result;
	}

	public boolean isHasMore() {
		return hasMore;
	}

}
