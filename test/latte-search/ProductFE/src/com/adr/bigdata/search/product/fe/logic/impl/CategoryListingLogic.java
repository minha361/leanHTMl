package com.adr.bigdata.search.product.fe.logic.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.ModeledLogic;
import com.adr.bigdata.search.product.fe.model.TrendingModel;
import com.adr.bigdata.search.product.fe.utils.FilterUtils;
import com.adr.bigdata.search.product.fe.utils.SearchUtils;
import com.adr.bigdata.search.product.fe.utils.SortType;
import com.adr.bigdata.search.product.fe.utils.SortUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.google.common.base.Strings;
import com.nhb.eventdriven.Callable;

public class CategoryListingLogic extends ModeledLogic {
	
	public CategoryListingLogic(ModelFactory modelFactory) {
		super(modelFactory);
	}

	private TrendingModel trendingModel;
	
	private void init(SolrQueryRequest req) throws Exception {
		try {
			if (trendingModel == null) {
				trendingModel = modelFactory.getModel(TrendingModel.class, req.getCore());
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	/**
	 * attribute: first value is attributeId <br />
	 * See {@link SortType} and {@link SortUtils} to understand sort <br />
	 * keyword empty for listing only <br />
	 * 
	 * <code>param:</code> category=1&brand=1&merchant=1&attribute=1-2-3&price=5000-1000
	 * &sort=1&order=asc&offset=0&limit=24&new=true&promotion=true
	 * 
	 */
	@Override
	public void execute(SolrQueryRequest req, SolrQueryResponse rsp, Callable callBack) throws Exception {
		init(req);
		
		SolrParams params = req.getParams();
		String[] catIds = params.getParams(CommonFilterLogic.P_CATEGORY);
		String[] brandIds = params.getParams(CommonFilterLogic.P_BRAND);
		String[] merchantIds = params.getParams(CommonFilterLogic.P_MERCHANT);
		String[] attFilters = params.getParams(CommonFilterLogic.P_ATTRIBUTE);
		String priceFilter = params.get(CommonFilterLogic.P_PRICE);
		Boolean isNew = params.getBool(CommonFilterLogic.P_NEW);
		Boolean isPromotion = params.getBool(CommonFilterLogic.P_PROMOTION);
		String sort = params.get(P_SORT);
		String order = params.get(P_ORDER);
		int offset = params.getInt(P_OFFSET, 0);
		int limit = params.getInt(P_LIMIT, 24);
		String extra = req.getCore().getCoreDescriptor().getCoreProperty(VinHandler.EXTRA_PROMOTION, "");

		SolrParamsBuilder paramsBuilder = new SolrParamsBuilder().offset(offset).limit(limit);
		paramsBuilder = search(paramsBuilder, req);
		paramsBuilder = FilterUtils.filterByCategory(paramsBuilder, catIds);
		paramsBuilder = FilterUtils.filterByBrand(paramsBuilder, brandIds);
		paramsBuilder = FilterUtils.filterByMerchant(paramsBuilder, merchantIds);
		paramsBuilder = FilterUtils.filterByFeatured(paramsBuilder, isNew, isPromotion, extra);
		paramsBuilder = SortUtils.sort(paramsBuilder, sort, order);

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

		ModifiableSolrParams _params = new ModifiableSolrParams(params);
		_params.remove("sort");
		_params.remove("order");
		_params.remove("keyword");
		req.setParams(paramsBuilder.addAdd(_params).getParams());
		getLogger().debug("params: {}", req.getParams());
		if (callBack != null) {
			callBack.call();
		}
	}

	protected SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req) {		
		String keyword = req.getParams().get("keyword", "*:*");
		//TODO standardize
		keyword = keyword.toLowerCase().trim();
		
		List<String> catTrending;
		if (!keyword.equals("*:*") && trendingModel != null) {
			catTrending = trendingModel.getTrending(keyword);
			getLogger().debug("trendings: {}", catTrending);
		} else {
			catTrending = null;
		}
		return SearchUtils.search(paramsBuilder, keyword, catTrending);	
	}

	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object ... others) throws Exception {
		
	}
	
	public void close() {
		if (trendingModel != null) {
			try {
				trendingModel.close();
			} catch (Exception e) {
				getLogger().error("", e);
			}
		}
	}
	
	public static final String P_SORT = "sort";
	public static final String P_ORDER = "order";
	public static final String P_OFFSET = "offset";
	public static final String P_LIMIT = "limit";
}
