package com.adr.bigdata.search.deal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class DealUtils {
	private static Logger log = LoggerFactory.getLogger(DealUtils.class);
	
	public static final int NUM_ELEMENT_PER_CAT = 4;

	public static int promotionPos(int start) {
		switch (start) {
		case 0:
			return 0;
		case 10:
			return 1;
		case 20:
			return 2;
		case 30:
			return 3;
		default:
			return 4;
		}
	}

	public static String[] wrapRange(String[] ss) {
		if (ss != null) {
			ArrayList<String> arr = new ArrayList<>(ss.length);
			if (ss != null && ss.length != 0) {
				for (int i = 0; i < ss.length; i++) {
					if (!Strings.isNullOrEmpty(ss[i])) {
						arr.add("[" + ss[i] + "]");
					}
				}
			}
			String[] result = new String[arr.size()];
			return arr.toArray(result);
		} else {
			return null;
		}
	}
	
	public static String[] standardize(String[] ss) {
		if (ss != null) {
			ArrayList<String> arr = new ArrayList<>(ss.length);
			if (ss != null && ss.length != 0) {
				for (int i = 0; i < ss.length; i++) {
					if (!Strings.isNullOrEmpty(ss[i])) {
						arr.add(ss[i]);
					}
				}
			}
			String[] result = new String[arr.size()];
			return arr.toArray(result);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static void extractPrice(SolrQueryResponse rsp, NamedList rootNL) {
		try {
			int maxPrice = ((Double) rootNL.findRecursive("stats", "stats_fields", DealFields.SELL_PRICE, "max")).intValue();
			int numRange = Integer.parseInt(System.getProperty("deal.price.num", "5"));
			int minPrice = Integer.parseInt(System.getProperty("deal.price.min", "0"));
			int[][] ranges = DealPriceUtils.getRanges(maxPrice, minPrice, numRange);
			List<SimpleOrderedMap<Object>> listPrice = new ArrayList<SimpleOrderedMap<Object>>();
			for (int i = 0; i < ranges.length; i++) {
				SimpleOrderedMap<Object> doc = new SimpleOrderedMap<Object>();
				doc.add("min", ranges[i][0]);
				doc.add("max", ranges[i][1]);
				listPrice.add(doc);
			}
			rsp.add("listPriceFilter", listPrice);
		} catch (Exception e) {
			rsp.add("listPriceFilter", Collections.emptyList());
			log.warn("", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void extractPromotion(SolrQueryResponse rsp, NamedList facetRanges) {
		try {
			NamedList frDiscount = (NamedList) facetRanges.get(DealFields.DISCOUNT_PERCENT);
			NamedList discountCounts = (NamedList) frDiscount.get("counts");
			int[] promotionResult = new int[5]; // Troll you
			for (int i = 0; i < discountCounts.size(); i++) {
				int start = Integer.valueOf(discountCounts.getName(i));
				int count = Integer.valueOf(discountCounts.getVal(i).toString());
				promotionResult[promotionPos(start)] += count;
			}
	
			rsp.add("listPromotionFilter", makePromotionResult(promotionResult));
		} catch (Exception e) {
			rsp.add("listPromotionFilter", Collections.emptyList());
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void extractDestination(SolrQueryResponse rsp, NamedList facetFields) {
		try {
			NamedList desIds = (NamedList) facetFields.get(DealFields.DESTINATION_FACETS);
			List<SimpleOrderedMap> listDestination = new ArrayList<SimpleOrderedMap>();
			for (int i = 0; i < desIds.size(); i++) {
				String key = desIds.getName(i);
				String[] keyElements = key.split("_");
				if (keyElements.length >= 4) {
					String desId = keyElements[0];
					String desStatus = keyElements[1];
					String desMappingStatus = keyElements[2];
					if ("1".equalsIgnoreCase(desStatus) && "1".equalsIgnoreCase(desMappingStatus)) {
						SimpleOrderedMap doc = new SimpleOrderedMap();
						doc.add("destinationid", desId);
						int beginIndex = desId.length() + desStatus.length() + desMappingStatus.length() + 3;
						doc.add("destinationname", key.substring(beginIndex));
						doc.add("numfound", desIds.getVal(i));
	
						listDestination.add(doc);
					}
				}
			}
			rsp.add("listDestinationFilter", listDestination);
		} catch (Exception e) {
			rsp.add("listDestinationFilter", Collections.emptyList());
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void extractCategory3(SolrQueryResponse rsp, NamedList facetFields) {
		try {
			NamedList cat3Facet = (NamedList) facetFields.get(DealFields.CAT_3_FACET);
			TreeMap<Long, SimpleOrderedMap> orderedCat3Facet = new TreeMap<Long, SimpleOrderedMap>();
			for (int i = 0; i < cat3Facet.size(); i++) {
				String key = cat3Facet.getName(i);
				String[] keyElements = key.split("_", NUM_ELEMENT_PER_CAT);
				if (keyElements.length >= NUM_ELEMENT_PER_CAT) {
					int catId = 0;
					try {
						catId = Integer.parseInt(keyElements[0]);
					} catch (Exception e) {
					}
					String catStatus = keyElements[1];
					int catSort = 0;
					try {
						catSort = Integer.parseInt(keyElements[2]);
					} catch (Exception e) {
					}
					String catName = keyElements[3];
					if ("1".equalsIgnoreCase(catStatus)) {
						SimpleOrderedMap map = new SimpleOrderedMap();
						map.add("categoryid", catId);
						map.add("categoryname", catName);
						map.add("numfound", cat3Facet.getVal(i));
						map.add("sort", catSort);
	
						long sort = (((long) catSort) << Integer.BYTES) | ((long) catId);
						orderedCat3Facet.put(sort, map);
					}
				}
			}
			rsp.add("listCategory", orderedCat3Facet.values());
		} catch (Exception e) {
			rsp.add("listCategory", Collections.emptyList());
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void extractCategoryFilter(SolrQueryResponse rsp, NamedList facetFields, String userCatFilterField) {
		if (userCatFilterField != null) {
			try {
				NamedList catFacet = (NamedList) facetFields.get(userCatFilterField);
				TreeMap<Long, SimpleOrderedMap> orderedCatFacet = new TreeMap<Long, SimpleOrderedMap>();
				for (int i = 0; i < catFacet.size(); i++) {
					String key = catFacet.getName(i);
					String[] keyElements = key.split("_", NUM_ELEMENT_PER_CAT);
					if (keyElements.length >= NUM_ELEMENT_PER_CAT) {
						int catId = 0;
						try {
							catId = Integer.parseInt(keyElements[0]);
						} catch (Exception e) {
						}
						String catStatus = keyElements[1];
						int catSort = 0;
						try {
							catSort = Integer.parseInt(keyElements[2]);
						} catch (Exception e) {
						}
						String catName = keyElements[3];
						if ("1".equalsIgnoreCase(catStatus)) {
							SimpleOrderedMap map = new SimpleOrderedMap();
							map.add("categoryid", catId);
							map.add("categoryname", catName);
							map.add("numfound", catFacet.getVal(i));
							map.add("sort", catSort);
		
							long sort = (((long) catSort) << Integer.BYTES) | ((long) catId);
							orderedCatFacet.put(sort, map);
						}
					}
				}
				rsp.add("listCategoryFilter", orderedCatFacet.values());
			} catch (Exception e) {
				rsp.add("listCategoryFilter", Collections.emptyList());
				log.warn("", e);
			}
		}
	}

	private static List<SimpleOrderedMap<Object>> makePromotionResult(int[] promotionResult) {
		List<SimpleOrderedMap<Object>> res = new ArrayList<SimpleOrderedMap<Object>>();
		for (int i = 0; i < 4; i++) {
			if (promotionResult[i] != 0) {
				SimpleOrderedMap<Object> doc = new SimpleOrderedMap<Object>();
				doc.add("minpromotion", i * 10);
				doc.add("maxpromotion", (i + 1) * 10);
				res.add(doc);
			}
		}
		if (promotionResult[4] != 0) {
			SimpleOrderedMap<Object> doc = new SimpleOrderedMap<Object>();
			doc.add("minpromotion", 40);
			doc.add("maxpromotion", 100);
			res.add(doc);
		}

		return res;
	}
}
