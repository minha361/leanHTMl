package com.adr.bigdata.search.product.mobile.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.common.params.SolrParams;
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
import com.adr.bigdata.search.vo.Tupple2;
import com.nhb.eventdriven.Callable;

public class MobileFilterLogic extends CommonFilterLogic {
	

	public MobileFilterLogic(ModelFactory modelFactory) throws Exception {
		super(modelFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeRsp(SolrQueryRequest req, SolrQueryResponse rsp, Object... others) throws Exception {
		ResponseWriterUtils.writeMaxPrice(rsp);
		
		Collection<AttributeCategoryMappingBean> atts = (Collection<AttributeCategoryMappingBean>) others[0];
		
		SolrParams params = req.getParams();
		String[] brandIds = params.getParams(P_BRAND);
		String[] merchantIds = params.getParams(P_MERCHANT);
		String[] attFilters = params.getParams(P_ATTRIBUTE);
		
		int[] selectedMcIds = string2int(merchantIds);
		int[] selectedBrandIds = string2int(brandIds);
		Tupple2<Integer, Integer>[] selectedAtts = string2tupple(attFilters);
		ResponseWriterUtils.writeAttributeFacet(rsp, atts, attributeModel, selectedAtts);
		ResponseWriterUtils.writeBrandFacet(rsp, brandModel, selectedBrandIds);
		ResponseWriterUtils.writeMerchantFacet(rsp, merchantModel, selectedMcIds);
		ResponseWriterUtils.writeFeaturedFacet(rsp);
		rsp.getValues().add("listCategory", null);
		rsp.getValues().remove("facet_counts");
		rsp.getValues().remove("response");
		rsp.getValues().remove("stats");
	}
	
	private int[] string2int(String[] ss) {
		if (ss == null) {
			return new int[0];
		}
		int[] rs = new int[ss.length];
		for (int i = 0; i < ss.length; i++) {
			rs[i] = Integer.parseInt(ss[i]);
		}
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	private Tupple2<Integer, Integer>[] string2tupple(String[] ss) {
		if (ss == null) {
			return new Tupple2[0];
		}
		List<Tupple2<Integer, Integer>> rs = new ArrayList<>();
		for (String s : ss) {
			String[] es = s.split("-");
			if (es.length > 1) {
				int first = Integer.parseInt(es[0]);
				for (int i = 1; i < es.length; i++) {
					rs.add(new Tupple2<Integer, Integer>(first, Integer.parseInt(es[i])));
				}
			}
		}
		Tupple2<Integer, Integer>[] arr = new Tupple2[rs.size()];
		rs.toArray(arr);
		return arr;
	}

	@Override
	protected SolrParamsBuilder facet(SolrParamsBuilder paramsBuilder, SolrQueryRequest req, Callable callBack)
			throws Exception {
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
		paramsBuilder = FacetUtils.facetByBrand(paramsBuilder);
		paramsBuilder = FacetUtils.facetByMerchant(paramsBuilder);
		paramsBuilder = FacetUtils.facetPromotion(paramsBuilder, extra);
		paramsBuilder = FacetUtils.facetNew(paramsBuilder);
		return paramsBuilder;
	}

	@Override
	protected SolrParamsBuilder search(SolrParamsBuilder paramsBuilder, SolrQueryRequest req) {
		String keyword = req.getParams().get(P_KEYWORD, "*:*");
		//TODO standardize
		keyword = keyword.toLowerCase().trim();
		
		return SearchUtils.search(paramsBuilder, keyword, null);	
	}

}
