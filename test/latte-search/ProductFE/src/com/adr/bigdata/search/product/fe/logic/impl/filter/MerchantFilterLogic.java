package com.adr.bigdata.search.product.fe.logic.impl.filter;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.impl.CommonFilterLogic;
import com.adr.bigdata.search.product.fe.utils.FacetUtils;
import com.adr.bigdata.search.product.fe.utils.ResponseWriterUtils;
import com.adr.bigdata.search.product.fe.utils.SearchUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.eventdriven.Callable;

public class MerchantFilterLogic extends CommonFilterLogic {

	public MerchantFilterLogic(ModelFactory modelFactory) throws Exception {
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
		ResponseWriterUtils.writeBrandFacet(rsp, brandModel);
		ResponseWriterUtils.writeFeaturedFacet(rsp);
		
		//write merchant
		int mcId = req.getParams().getInt(P_MERCHANT);
		MerchantBean mcBean = merchantModel.getMerchant(mcId);
		rsp.add("merchantName", mcBean.getName());
		rsp.add("merchantShortInfo", mcBean.getInfo());
		rsp.add("merchantImage", mcBean.getImage());
		rsp.add("merchantId", mcBean.getId());

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
		paramsBuilder = FacetUtils.facetByBrand(paramsBuilder);
		paramsBuilder = FacetUtils.facetByCategory(paramsBuilder);
		paramsBuilder = FacetUtils.facetPromotion(paramsBuilder, extra);
		paramsBuilder = FacetUtils.facetNew(paramsBuilder);
		return paramsBuilder;
	}
}
