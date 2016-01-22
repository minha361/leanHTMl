package com.adr.bigdata.search.product.cm;

import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.nhb.common.Loggable;

public class CMBackendHandler extends SearchHandler implements Loggable {
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		CmBackendQuery query = new CmBackendQuery(req);
		SolrParamsBuilder builder = new SolrParamsBuilder()
				.keyword(Q)
				.add("keyword", query.getKeyword())
				.add("keyword_e", "(" + query.getKeyword() + ")^100")
				.add("start", query.getStart())
				.add("rows", query.getRows())
				.add("sort", query.getSort())
				
				.filter(ProductFields.CATEGORY_PATH, query.getCategoryIds())
				.filter(ProductFields.BRAND_ID, query.getBrandIds())				
				.filter(ProductFields.CREATE_TIME, query.getDateFromTo())				
				.filter(ProductFields.WAREHOUSE_ID, query.getWarehouseIds())
				.filter(ProductFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, query.getWarehouseProductItemMappingId())
				.filter(ProductFields.MERCHANT_ID, query.getMerchantIds())
				.filter(ProductFields.PRODUCT_ITEM_ID, query.getProductItemIds())				
				.filter(ProductFields.PRODUCT_ITEM_STATUS, query.getProductItemStatuses())
				.filter(ProductFields.MERCHANT_PRODUCT_ITEM_STATUS, query.getMerchantProductItemStatuses())				
				.filter(ProductFields.PRODUCT_ITEM_TYPE, query.getProductItemTypes())	
				
				.filterWithExistField(query.getVisible())		
				.filterWithExistField(query.getApproved())
				.filterWithExistField(query.getSafetyStock())
				.filterWithExistField(query.getIsOriginal())
				.filterWithExistField(query.getInStock())
				
				.collapse(ProductFields.PRODUCT_ITEM_ID_MERCHANT_ID, null);

		req.setParams(builder.getParams());
		super.handleRequest(req, rsp);
	}
	
	private static String Q = "_query_:{!q.op=AND df=merchant_product_item_sku v=$keyword_e} \n"
			+ "_query_:{!q.op=AND df=barcode v=$keyword_e} \n" + 
			"_query_:{!df=product_item_name_cm v=$keyword}";
}
