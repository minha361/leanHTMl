/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.bigdata.search.product.cm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;

import com.adr.bigdata.search.product.ProductFields;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 *
 * @author Tong Hoang Anh
 */
public final class CmBackendQuery {

	private String[] merchantIds;
	private String[] categoryIds;
	private String[] brandIds;
	private String[] warehouseIds;
	private String[] warehouseProductItemMappingId;
	private String[] productItemIds;
	private String visible;
	private String[] merchantProductItemStatuses;
	private String[] productItemStatuses;
	private String[] productItemTypes;
	private String inStock;
	private String keyword;
	private String dateFromTo;
	private String rows;
	private String start;
	private String sort = Params.SORT;
	private String safetyStock;
	private String isoriginal;
	private String approved;

	public CmBackendQuery() {

	}

	public CmBackendQuery(SolrQueryRequest req) {
		SolrParams param = req.getParams();
		this.keyword = param.get(Params.KEYWORD);
		this.start = param.get(Params.START, "0");
		this.rows = param.get(Params.ROWS, "10");
		this.sort = param.get(Params.SORT);
		this.dateFromTo = param.get(Params.DATE_FROM_TO);
		this.categoryIds = param.getParams(Params.CATEGORY_ID);
		this.warehouseIds = param.getParams(Params.WAREHOUSE_ID);
		this.warehouseProductItemMappingId = param.getParams(Params.WAREHOUSE_PRODUCT_ITEM_MAPPING_ID);
		this.merchantIds = param.getParams(Params.MERCHANT_ID);
		this.brandIds = param.getParams(Params.BRAND_ID);
		this.productItemIds = param.getParams(Params.PRODUCT_ITEM_ID);
		this.visible = param.get(Params.VISIBLE);
		this.productItemStatuses = param.getParams(Params.PRODUCT_ITEM_STATUS);
		this.merchantProductItemStatuses = param.getParams(Params.MERCHANT_PRODUCT_ITEM_STATUS);
		this.productItemTypes = param.getParams(Params.PRODUCT_ITEM_TYPE);
		this.inStock = param.get(Params.IN_STOCK);
		this.approved = param.get(Params.APPROVED);
		this.safetyStock = param.get(Params.SAFETY_STOCK);
		this.isoriginal = param.get(Params.IS_ORIGINAL);
	}

	public String getIsOriginal() {
		return getIsOriginalFilter(this.isoriginal);
	}

	public String getSafetyStock() {
		return getSafetyStockFilter(this.safetyStock);
	}

	public String getApproved() {
		return getApprovedFilter(approved);
	}

	public String[] getMerchantProductItemStatuses() {
		if (merchantProductItemStatuses != null && merchantProductItemStatuses.length != 0) {
			for (int i = 0; i < merchantProductItemStatuses.length; i++) {
				if (merchantProductItemStatuses[i].trim().equals("0")) {
					return new String[] { "[0 TO *]" };
				}
				if (merchantProductItemStatuses[i].startsWith("-")) {
					this.merchantProductItemStatuses[i] = QueryParser.escape(merchantProductItemStatuses[i]);
				}
			}
		}
		return merchantProductItemStatuses;
	}

	public String[] getProductItemStatuses() {
		if (productItemStatuses != null && productItemStatuses.length != 0) {
			for (int i = 0; i < productItemStatuses.length; i++) {
				if (productItemStatuses[i].trim().equals("0")) {
					return new String[] { "[0 TO *]" };
				}
				if (productItemStatuses[i].startsWith("-")) {
					this.productItemStatuses[i] = QueryParser.escape(productItemStatuses[i]);
				}
			}
		}
		return productItemStatuses;
	}

	private static String getSafetyStockFilter(String safetyStock) {
		if (!Strings.isNullOrEmpty(safetyStock)) {
			if ("0".equals(safetyStock.trim())) {
				return ProductFields.QUANTITY + ":[ * TO 0]";
			} else {
				return ProductFields.QUANTITY + ":[1 TO " + safetyStock + "]";
			}
		}
		return "";
	}

	public String[] getCategoryIds() {
		return categoryIds;
	}

	public String[] getWarehouseIds() {
		return warehouseIds;
	}

	public String[] getBrandIds() {
		return brandIds;
	}

	public String[] getWarehouseProductItemMappingId() {
		return warehouseProductItemMappingId;
	}

	public String[] getMerchantIds() {
		return merchantIds;
	}

	public String[] getProductItemIds() {
		return productItemIds;
	}

	public String getKeyword() {
		if (Strings.isNullOrEmpty(keyword)) {
			return "*";
		}
		return keyword;
	}

	public String getDateFromTo() {
		return changeDateFormat(dateFromTo);
	}

	public String getRows() {
		return rows;
	}

	public String getStart() {
		return start;
	}

	public String getSort() {
		return sort;
	}

	public String getVisible() {
		return getFilterVisible(visible);
	}

	public String[] getProductItemTypes() {
		return productItemTypes;
	}

	public String getInStock() {
		if ("true".equalsIgnoreCase(inStock)) {
			return InStock.TRUE;
		} else if ("false".equalsIgnoreCase(inStock)) {
			return InStock.FALSE;
		}
		return "";
	}

	private static SimpleDateFormat searchTransformDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String APPROVED = "price_status:1 AND vat_status:1";
	private static final String NOT_APPROVED = "-(price_status:1 AND vat_status:1)";

	private static String getIsOriginalFilter(String isOriginal) {
		if ("true".equalsIgnoreCase(isOriginal)) {
			return ProductFields.WEIGHT + ":0";
		}
		return "";
	}

	private static String getApprovedFilter(String approved) {
		if ("true".equalsIgnoreCase(approved)) {
			return APPROVED;
		} else if ("false".equalsIgnoreCase(approved)) {
			return NOT_APPROVED;
		}
		return "";
	}

	/**
	 * @author anhth
	 */
	private static String changeDateFormat(String dateFromTo) {
		if (Strings.isNullOrEmpty(dateFromTo)) {
			return "";
		}
		try {
			String[] splitted = dateFromTo.split(" TO ");
			if (splitted.length != 2) {
				throw new IllegalStateException("Bad date format");
			}
			if (dateFromTo.contains("*")) {
				return dateFromTo;
			}
			Date firstDate = searchTransformDf.parse(splitted[0].replace("Z", ".00Z").substring(1));
			Date secondDate = searchTransformDf
					.parse(splitted[1].substring(0, splitted[1].length() - 1).replace("Z", ".00Z"));
			return "[" + firstDate.getTime() + " TO " + secondDate.getTime() + "]";
		} catch (IllegalStateException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @author anhth
	 */
	private static String getFilterVisible(String visible) {
		if (Strings.isNullOrEmpty(visible)) {
			return "";
		}

		if ("-511".equals(visible)) {
			return "-" + ProductFields.ON_SITE + ":511";
		}

		if ("-256".equals(visible)) {
			return "-" + ProductFields.VISIBLE + ":256";
		}

		int v = 0;
		try {
			v = Integer.valueOf(visible);
		} catch (Exception e) {
		}

		return Joiner.on(" AND ").join(getListBits(v));
	}

	/**
	 * @author anhth
	 */
	private static List<String> getListBits(int n) {
		List<String> result = new ArrayList<>();
		int i = 0;
		int a = 1 << i;
		while (a <= n) {
			if ((n & a) == a) {
				result.add(ProductFields.VISIBLE + ":" + a);
			}
			i++;
			a = 1 << i;
		}
		return result;
	}

	public static class Params {

		public static final String PRODUCT_ITEM_ID = "productitemid";
		public static final String MERCHANT_NAME = "merchantname";
		public static final String PRODUCT_ITEM_STATUS = "productitemstatus";
		public static final String MERCHANT_SKU = "merchantsku";
		public static final String MERCHANT_ID = "merchantid";
		public static final String MERCHANT_PRODUCT_ITEM_STATUS = "merchantproductitemstatus";
		public static final String KEYWORD = "keyword";
		public static final String MERCHANT_CODE = "merchantcode";
		public static final String WAREHOUSE_ID = "warehouseid";
		public static final String CATEGORY_ID = "categoryid";
		public static final String ROWS = "rows";
		public static final String START = "start";
		public static final String DATE_FROM_TO = "dateFromTo";
		public static final String WAREHOUSE_PRODUCT_ITEM_MAPPING_ID = "warehouseproductitemmappingid";
		public static final String BRAND_ID = "brandid";
		public static final String SORT = "sort";
		public static final String VISIBLE = "visible";
		public static final String PRODUCT_ITEM_TYPE = "productitemtype";
		public static final String IN_STOCK = "instock";
		public static final String APPROVED = "approved";
		public static final String SAFETY_STOCK = "safetystock";
		public static final String IS_ORIGINAL = "isoriginal";
	}

	public class InStock {
		public static final String TRUE = "-quantity:[* TO 0]";
		public static final String FALSE = "quantity:[* TO 0]";
	}

}
