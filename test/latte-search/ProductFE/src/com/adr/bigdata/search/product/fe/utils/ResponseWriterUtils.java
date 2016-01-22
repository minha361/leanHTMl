package com.adr.bigdata.search.product.fe.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.product.fe.model.AttributeModel;
import com.adr.bigdata.search.product.fe.model.BrandModel;
import com.adr.bigdata.search.product.fe.model.CategoryModel;
import com.adr.bigdata.search.product.fe.model.MerchantModel;
import com.adr.bigdata.search.product.utils.SimpleOrderedMapUtils;
import com.adr.bigdata.search.vo.Tupple2;

public class ResponseWriterUtils {
	private static Logger log = LoggerFactory.getLogger(ResponseWriterUtils.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeMaxPrice(SolrQueryResponse rsp) {
		try {
			NamedList max = (NamedList) rsp.getValues().findRecursive("stats", "stats_fields", ProductFields.SELL_PRICE);
			double maxPrice = (double) max.get("max");
			rsp.getValues().add("maxPrice", maxPrice);
		} catch (Exception e) {
			rsp.getValues().add("maxPrice", -1);
			log.warn("", e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeCategoryFacet(SolrQueryResponse rsp, CategoryModel categoryModel) throws Exception {
		try {
			NamedList catFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", 
					"facet_fields",
					ProductFields.CATEGORY_ID);
			if (catFacet != null) {
				Set<Integer> catIds = new HashSet<>();
				for (int i = 0; i < catFacet.size(); i++) {
					catIds.add(Integer.parseInt(catFacet.getName(i)));
				}
				Collection<CategoryBean> cats = categoryModel.getCategories(catIds);
				List<SimpleOrderedMap> listCategory = new ArrayList<>();
				for (CategoryBean cat : cats) {
					SimpleOrderedMap som = SimpleOrderedMapUtils.create("categoryId", cat.getId(), 
							"categoryParentId", cat.getParentId(), 
							"categoryName", cat.getName(), 
							"numFound", "1");
					listCategory.add(som);
				}
				rsp.getValues().add("listCategory", listCategory);
			}
		} catch (Exception e) {
			rsp.getValues().add("listCategory", null);
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeAttributeFacet(SolrQueryResponse rsp, Collection<AttributeCategoryMappingBean> atts,
			AttributeModel attributeModel, Tupple2<Integer, Integer> ... selectedAtts) {
		try {
			List<SimpleOrderedMap> listCatAttFilter = new ArrayList<>();
			if (atts != null && !atts.isEmpty()) {
				// GetAll Value and DisplayUnit
				Set<String> keys = new HashSet<>();
				for (AttributeCategoryMappingBean att : atts) {
					NamedList attFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", 
							"facet_fields",
							ProductFields.attrInt(att.getAttributeId()));
					for (int i = 0; i < attFacet.size(); i++) {
						keys.add(att.getAttributeId() + "_" + attFacet.getName(i));
					}
				}
				if (selectedAtts != null) {
					for (Tupple2<Integer, Integer> att : selectedAtts) {
						keys.add(att._1() + "_" + att._2());
					}
				}
				Map<String, AttributeValueMeasureUnitDisplayBean> displayUnit = attributeModel.getDisplayUnit(keys);
				log.debug("displayUnit: {}", displayUnit);
				// End: GetAll Value and DisplayUnit
	
				for (AttributeCategoryMappingBean att : atts) {
					NamedList attFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", 
							"facet_fields", 
							ProductFields.attrInt(att.getAttributeId()));
					log.debug("attFacet: {}", attFacet);
					if (selectedAtts != null) {
						for (Tupple2<Integer, Integer> selectedAtt: selectedAtts) {
							if (selectedAtt._1() == att.getAttributeId() && attFacet.get(selectedAtt._2().toString()) == null) {
								attFacet.add(selectedAtt._2().toString(), 0);
							}
						}
					}
					if (attFacet.size() > 0) {
						if (att.getAttributeType() == 2) {
							SimpleOrderedMap som = createAttElement2(attFacet, att, displayUnit);
							if (som != null) {
								listCatAttFilter.add(som);
							}
						} else if (att.getAttributeType() == 4) {
							SimpleOrderedMap som = createAttElement4(attFacet, att, displayUnit);
							if (som != null) {
								listCatAttFilter.add(som);
							}
						}
					}
				}
			}
			sort(listCatAttFilter, "attId");
			rsp.getValues().add("listCatAttFilter", listCatAttFilter);
		} catch (Exception e) {
			rsp.getValues().add("listCatAttFilter", null);
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeBrandFacet(SolrQueryResponse rsp, BrandModel brandModel, int ... selectedBrandIds) throws Exception {
		try {
			NamedList brandFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", 
					"facet_fields",
					ProductFields.BRAND_ID);
	
			List<SimpleOrderedMap> listCatMan = new ArrayList<>();
			// Get all brands
			Map<Integer, Integer> brandIds = new HashMap<>();
			if (selectedBrandIds != null) {
				for (int brandId : selectedBrandIds) {
					brandIds.put(brandId, 0);
				}
			}
			if (brandFacet != null) {
				for (int i = 0; i < brandFacet.size(); i++) {
					brandIds.put(Integer.parseInt(brandFacet.getName(i)),
							Integer.parseInt(brandFacet.getVal(i).toString()));
				}
			}
			if (!brandIds.isEmpty()) {
				Collection<BrandBean> brands = brandModel.getBrands(brandIds.keySet());
				for (BrandBean brand : brands) {
					listCatMan.add(SimpleOrderedMapUtils.create("manufactuerId", brand.getId(), 
							"manufactuerName",
							brand.getName(), "numFound", 
							brandIds.get(brand.getId())));
				}	
			}
			isort(listCatMan, "numFound");
			rsp.getValues().add("listCatMan", listCatMan);
		} catch (Exception e) {
			rsp.getValues().add("listCatMan", null);
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeMerchantFacet(SolrQueryResponse rsp, MerchantModel merchantModel, int ... selectedMcIds) throws Exception {
		try {
			NamedList merchantFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", 
					"facet_fields",
					ProductFields.MERCHANT_ID);
			List<SimpleOrderedMap> listMerchant = new ArrayList<>();
			// Get all merchant
			Map<Integer, Integer> merchantMap = new HashMap<>();
			if (merchantFacet != null) {
				for (int i = 0; i < merchantFacet.size(); i++) {
					merchantMap.put(Integer.parseInt(merchantFacet.getName(i)),
							Integer.parseInt(merchantFacet.getVal(i).toString()));
				}
			}
			if (selectedMcIds != null) {
				for (int mcId : selectedMcIds) {
					merchantMap.put(mcId, 0);
				}
			}
			if (!merchantMap.isEmpty()) {
				Collection<MerchantBean> merchants = merchantModel.getAllMerchants(merchantMap.keySet());
				for (MerchantBean mc : merchants) {
					listMerchant.add(SimpleOrderedMapUtils.create("merchantId", mc.getId(), 
							"merchantName", mc.getName(),
							"numFound", merchantMap.get(mc.getId())));
				}
			}
			isort(listMerchant, "numFound");
			rsp.getValues().add("listMerchant", listMerchant);
		} catch (Exception e) {
			rsp.getValues().add("listMerchant", null);
			log.warn("", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeFeaturedFacet(SolrQueryResponse rsp) {
		try {
			NamedList featuredFacet = (NamedList) rsp.getValues().findRecursive("facet_counts", "facet_queries");
			int countPromotion = (int) featuredFacet.get(FacetUtils.IS_PROMOTION_KEY);
			int countNew = (int) featuredFacet.get(FacetUtils.IS_NEW_KEY);
			List<SimpleOrderedMap> listFeatured = new ArrayList<>();
			listFeatured.add(SimpleOrderedMapUtils.create("featuredName", "isPromotion", "numFound", countPromotion));
			listFeatured.add(SimpleOrderedMapUtils.create("featuredName", "newArrival", "numFound", countNew));
			rsp.getValues().add("listFeatured", listFeatured);
		} catch (Exception e) {
			List<SimpleOrderedMap> listFeatured = new ArrayList<>();
			listFeatured.add(SimpleOrderedMapUtils.create("featuredName", "isPromotion", "numFound", 0));
			listFeatured.add(SimpleOrderedMapUtils.create("featuredName", "newArrival", "numFound", 0));
			rsp.getValues().add("listFeatured", listFeatured);
		}
	}

	private static String transform(AttributeValueMeasureUnitDisplayBean displayunit) {
		try {
			Double doubleVal = Double.parseDouble(displayunit.getValue());
			double ratio = displayunit.getDisplayRatio();
			String unit = displayunit.getDisplayUnitName();
			Integer accuracy = Integer.valueOf(System.getProperty("getfilter.attribute.doubleaccuracy", "3"));
			double displayVal = doubleVal;
			if (ratio > 0) {
				displayVal = doubleVal / ratio;
			}
			String strAccuracy = StringUtils.repeat("#", accuracy);
			DecimalFormat format = new DecimalFormat("0." + strAccuracy);
			String result = format.format(displayVal);
			return (result + " " + unit.trim());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@SuppressWarnings("rawtypes")
	private static SimpleOrderedMap createAttElement2(NamedList attFacet, AttributeCategoryMappingBean att,
			Map<String, AttributeValueMeasureUnitDisplayBean> displayUnit) {
		if (attFacet != null && attFacet.size() > 0) {
			List<SimpleOrderedMap> lvalue = new ArrayList<>();
			for (int i = 0; i < attFacet.size(); i++) {
				String valueId = attFacet.getName(i);
				String key = att.getAttributeId() + "_" + valueId;
				if (displayUnit.containsKey(key)) {
					lvalue.add(SimpleOrderedMapUtils.create("value", displayUnit.get(key).getValue(), 
							"numFound", attFacet.getVal(i),
							"valueId", valueId,
							"displayValue", displayUnit.get(key).getValue()));
				}
			}
			SimpleOrderedMap result = SimpleOrderedMapUtils.create("attId", att.getAttributeId(), 
					"attName", att.getAttributeName(), 
					"unitName", null, 
					"typeValue", 2, 
					"lvalue", lvalue);
			return result;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static SimpleOrderedMap createAttElement4(NamedList attFacet, AttributeCategoryMappingBean att,
			Map<String, AttributeValueMeasureUnitDisplayBean> displayUnit) {
		if (attFacet != null && attFacet.size() > 0) {
			List<SimpleOrderedMap> lvalue = new ArrayList<>();
			for (int i = 0; i < attFacet.size(); i++) {
				String valueId = attFacet.getName(i);
				String key = att.getAttributeId() + "_" + valueId;
				if (displayUnit.containsKey(key)) {
					AttributeValueMeasureUnitDisplayBean displayBean = displayUnit.get(key);
					lvalue.add(SimpleOrderedMapUtils.create("value", displayBean.getValue(), 
							"numFound", attFacet.getVal(i),
							"displayValue", transform(displayBean), 
							"valueId", valueId));
				}
			}
			sort4(lvalue, "value");
			SimpleOrderedMap result = SimpleOrderedMapUtils.create("attId", att.getAttributeId(), 
					"attName", att.getAttributeName(), 
					"unitName", null, 
					"typeValue", 4, 
					"lvalue", lvalue);
			return result;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void sort4(List<SimpleOrderedMap> origin, String name) {
		origin.sort(new Comparator<SimpleOrderedMap>() {

			@Override
			public int compare(SimpleOrderedMap o1, SimpleOrderedMap o2) {
				try {
					Double field1 = Double.parseDouble(o1.get(name).toString());
					Double field2 = Double.parseDouble(o2.get(name).toString());
					return field1.compareTo(field2);
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	private static void sort(List<SimpleOrderedMap> origin, String name) {
		origin.sort(new Comparator<SimpleOrderedMap>() {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(SimpleOrderedMap o1, SimpleOrderedMap o2) {
				try {
					Comparable field1 = (Comparable) o1.get(name);
					Comparable field2 = (Comparable) o2.get(name);
					return field1.compareTo(field2);
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	private static void isort(List<SimpleOrderedMap> origin, String name) {
		origin.sort(new Comparator<SimpleOrderedMap>() {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(SimpleOrderedMap o1, SimpleOrderedMap o2) {
				try {
					Comparable field1 = (Comparable) o1.get(name);
					Comparable field2 = (Comparable) o2.get(name);
					return field2.compareTo(field1);
				} catch (Exception e) {
					return 0;
				}
			}
		});
	}
}
