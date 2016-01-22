package com.adr.bigdata.search.product.fe.logic.impl.filter;

import java.util.Collection;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.fe.logic.impl.CommonFilterLogic;
import com.adr.bigdata.search.product.fe.utils.FacetUtils;
import com.adr.bigdata.search.product.fe.utils.ResponseWriterUtils;
import com.adr.bigdata.search.product.fe.utils.SearchUtils;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.eventdriven.Callable;

public class CategoryFilterLogic extends CommonFilterLogic {

	public CategoryFilterLogic(ModelFactory modelFactory) throws Exception {
		super(modelFactory);
	}

	protected SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req) {
		String keyword = req.getParams().get(P_KEYWORD, "*:*");
		return SearchUtils.search(paramsBuilder, keyword, null);
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

		rsp.getValues().remove("facet_counts");
		rsp.getValues().remove("response");
		rsp.getValues().remove("stats");
	}

	@Override
	protected SolrParamsBuilder facet(SolrParamsBuilder paramsBuilder, SolrQueryRequest req, Callable callBack)
			throws Exception {
		int catId = req.getParams().getInt(P_CATEGORY);

		CategoryBean cat = categoryModel.getCategory(catId);
		if (cat.getPath().size() > 1) {
			Collection<AttributeCategoryMappingBean> atts = categoryModel.getFilterAttributes(catId);

			for (AttributeCategoryMappingBean att : atts) {
				paramsBuilder = FacetUtils.facetByAttributeInt(paramsBuilder, att.getAttributeId());
			}

			if (callBack != null) {
				callBack.call(atts);
			}
		}
		String extra = req.getCore().getCoreDescriptor().getCoreProperty(VinHandler.EXTRA_PROMOTION, "");
		paramsBuilder = FacetUtils.facetByBrand(paramsBuilder);
		paramsBuilder = FacetUtils.facetByMerchant(paramsBuilder);
		paramsBuilder = FacetUtils.facetPromotion(paramsBuilder, extra);
		paramsBuilder = FacetUtils.facetNew(paramsBuilder);
		return paramsBuilder;
	}
}