package com.adr.bigdata.search.product.fe.logic.impl;

import java.util.Arrays;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.ModeledLogic;
import com.adr.bigdata.search.product.fe.model.AttributeModel;
import com.adr.bigdata.search.product.fe.model.BrandModel;
import com.adr.bigdata.search.product.fe.model.CategoryModel;
import com.adr.bigdata.search.product.fe.model.MerchantModel;
import com.adr.bigdata.search.product.fe.utils.FilterUtils;
import com.adr.bigdata.search.product.fe.utils.SortType;
import com.adr.bigdata.search.product.fe.utils.SortUtils;
import com.adr.bigdata.search.product.fe.utils.StatisticUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;
import com.nhb.eventdriven.Callable;

public abstract class CommonFilterLogic extends ModeledLogic  {
	protected CategoryModel categoryModel;
	protected AttributeModel attributeModel;
	protected BrandModel brandModel;
	protected MerchantModel merchantModel;

	public CommonFilterLogic(ModelFactory modelFactory) throws Exception {
		super(modelFactory);
	}

	private void init(SolrQueryRequest req) throws Exception {
		if (categoryModel == null) {
			categoryModel = modelFactory.getModel(CategoryModel.class, req.getCore());
		}
		if (attributeModel == null) {
			attributeModel = modelFactory.getModel(AttributeModel.class, req.getCore());
		}
		if (brandModel == null) {
			brandModel = modelFactory.getModel(BrandModel.class, req.getCore());
		}
		if (merchantModel == null) {
			merchantModel = modelFactory.getModel(MerchantModel.class, req.getCore());
		}
	}

	/**
	 * attribute: first value is attributeId <br />
	 * See {@link SortType} and {@link SortUtils} to understand sort <br />
	 * keyword empty for listing only <br />
	 * 
	 * param:category=1&brand=1&merchant=1&attribute=1-2-3&price=5000-1000&new=true&promotion=true
	 * 
	 */
	@Override
	public void execute(SolrQueryRequest req, SolrQueryResponse rsp, Callable callBack) throws Exception {
		init(req);
		SolrParams params = req.getParams();
		String[] catIds = params.getParams(P_CATEGORY);
		String[] brandIds = params.getParams(P_BRAND);
		String[] merchantIds = params.getParams(P_MERCHANT);
		String[] attFilters = params.getParams(P_ATTRIBUTE);
		String priceFilter = params.get(P_PRICE);
		Boolean isNew = params.getBool(P_NEW);
		Boolean isPromotion = params.getBool(P_PROMOTION);
		String extra = req.getCore().getCoreDescriptor().getCoreProperty(VinHandler.EXTRA_PROMOTION, "");

		SolrParamsBuilder paramsBuilder = new SolrParamsBuilder().limit(0).facet(1).add("facet.sort", "count");
		paramsBuilder = search(paramsBuilder, req);

		paramsBuilder = FilterUtils.filterByCategory(paramsBuilder, catIds);
		paramsBuilder = FilterUtils.filterByBrand(paramsBuilder, brandIds);
		paramsBuilder = FilterUtils.filterByMerchant(paramsBuilder, merchantIds);
		paramsBuilder = FilterUtils.filterByFeatured(paramsBuilder, isNew, isPromotion, extra);
		if (!Strings.isNullOrEmpty(priceFilter)) {
			String[] priceSplit = priceFilter.split("-", 2);
			int min = Integer.parseInt(priceSplit[0]);
			int max = Integer.parseInt(priceSplit[1]);
			paramsBuilder = FilterUtils.filterByPrice(paramsBuilder, min, max);
		}
		if (attFilters != null && attFilters.length != 0) {
			for (String attFilter : attFilters) {
				String[] attSplit = attFilter.split("-");
				int attId = Integer.parseInt(attSplit[0]);
				String[] attValueIds = Arrays.copyOfRange(attSplit, 1, attSplit.length);
				paramsBuilder = FilterUtils.filterByAttributeInt(paramsBuilder, attId, attValueIds);
			}
		}

		paramsBuilder = StatisticUtils.byMaxPrice(paramsBuilder);
		paramsBuilder = facet(paramsBuilder, req, callBack);

		getLogger().debug("params: {}", paramsBuilder.getParams());

		req.setParams(paramsBuilder.addAdd(params).getParams());
	}
	
	protected abstract SolrParamsBuilder facet(SolrParamsBuilder paramsBuilder, SolrQueryRequest req, Callable callBack) throws Exception;

	protected abstract SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req);
	
	public static final String P_CATEGORY = "catid";
	public static final String P_BRAND = "brandid";
	public static final String P_MERCHANT = "mcid";
	public static final String P_ATTRIBUTE = "att";
	public static final String P_PRICE = "price";
	public static final String P_NEW = "isnew";
	public static final String P_PROMOTION = "ispro";
	public static final String P_KEYWORD = "keyword";
}
