package com.adr.bigdata.search.product.fe.logic.impl.filter;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.impl.CommonFilterLogic;
import com.adr.bigdata.search.product.fe.utils.FacetUtils;
import com.adr.bigdata.search.product.fe.utils.ResponseWriterUtils;
import com.adr.bigdata.search.product.fe.utils.SearchUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.eventdriven.Callable;

public class BrandFilterLogic extends CommonFilterLogic {

	public BrandFilterLogic(ModelFactory modelFactory) throws Exception {
		super(modelFactory);
	}

	@Override
	protected SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req) {
		return SearchUtils.search(paramsBuilder, null, null);
	}

	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object... others) throws Exception {
		ResponseWriterUtils.writeMaxPrice(rsp);
		
		ResponseWriterUtils.writeCategoryFacet(rsp, categoryModel);
//		ResponseWriterUtils.writeMerchantFacet(rsp, merchantModel);
		ResponseWriterUtils.writeFeaturedFacet(rsp);

		// write merchant
		int brId = req.getParams().getInt(P_BRAND);
		BrandBean brBean = brandModel.getBrand(brId);
		rsp.add("brandName", brBean.getName());
		rsp.add("brandShortInfo", brBean.getDescription());
		rsp.add("brandImage", brBean.getImage());
		rsp.add("brandId", brBean.getId());

		rsp.getValues().remove("facet_counts");
		rsp.getValues().remove("response");
		rsp.getValues().remove("stats");
	}

	@Override
	protected SolrParamsBuilder facet(SolrParamsBuilder paramsBuilder, SolrQueryRequest req, Callable callBack) throws Exception {
		if (callBack != null) {
			callBack.call();
		}
		String extra = req.getCore().getCoreDescriptor().getCoreProperty(VinHandler.EXTRA_PROMOTION, "");
		paramsBuilder = FacetUtils.facetByCategory(paramsBuilder);
//		paramsBuilder = FacetUtils.facetByMerchant(paramsBuilder);
		paramsBuilder = FacetUtils.facetPromotion(paramsBuilder, extra);
		paramsBuilder = FacetUtils.facetNew(paramsBuilder);
		return paramsBuilder;
	}
}
