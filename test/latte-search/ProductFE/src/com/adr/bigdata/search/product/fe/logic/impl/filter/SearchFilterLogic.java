package com.adr.bigdata.search.product.fe.logic.impl.filter;

import java.util.Collection;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.impl.CommonFilterLogic;
import com.adr.bigdata.search.product.fe.utils.FacetUtils;
import com.adr.bigdata.search.product.fe.utils.ResponseWriterUtils;
import com.adr.bigdata.search.product.fe.utils.SearchUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.eventdriven.Callable;

public class SearchFilterLogic extends CommonFilterLogic {

	public SearchFilterLogic(ModelFactory modelFactory) throws Exception {
		super(modelFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object... others) throws Exception {
		ResponseWriterUtils.writeMaxPrice(rsp);
		
		Collection<AttributeCategoryMappingBean> atts = (Collection<AttributeCategoryMappingBean>) others[0];
		ResponseWriterUtils.writeAttributeFacet(rsp, atts, attributeModel);
		ResponseWriterUtils.writeBrandFacet(rsp, brandModel);
		ResponseWriterUtils.writeMerchantFacet(rsp, merchantModel);
		ResponseWriterUtils.writeFeaturedFacet(rsp);
		ResponseWriterUtils.writeCategoryFacet(rsp, categoryModel);
		
		rsp.getValues().remove("facet_counts");
		rsp.getValues().remove("response");
		rsp.getValues().remove("stats");
	}

	@Override
	protected SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req) {
		String keyword = req.getParams().get(P_KEYWORD);
		//TODO standardize
		keyword = keyword.toLowerCase().trim();
		
		return SearchUtils.search(paramsBuilder, keyword, null);	
	}

	@Override
	protected SolrParamsBuilder facet(SolrParamsBuilder paramsBuilder, SolrQueryRequest req, Callable callBack) throws Exception {
		Integer catId = req.getParams().getInt(P_CATEGORY);
		Collection<AttributeCategoryMappingBean> atts = null;
		if (catId != null) {
			atts = categoryModel.getFilterAttributes(catId);
			for (AttributeCategoryMappingBean att : atts) {
				paramsBuilder = FacetUtils.facetByAttributeInt(paramsBuilder, att.getAttributeId());
			}
		}
		if (callBack != null) {
			callBack.call(atts);
		}
		
		String extra = req.getCore().getCoreDescriptor().getCoreProperty(VinHandler.EXTRA_PROMOTION, "");
		paramsBuilder = FacetUtils.facetByCategory(paramsBuilder);
		paramsBuilder = FacetUtils.facetByBrand(paramsBuilder);
		paramsBuilder = FacetUtils.facetByMerchant(paramsBuilder);
		paramsBuilder = FacetUtils.facetPromotion(paramsBuilder, extra);
		paramsBuilder = FacetUtils.facetNew(paramsBuilder);
		return paramsBuilder;
	}

}
