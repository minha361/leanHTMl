package com.adr.bigdata.search.handler.collectiongroup;

import java.util.Arrays;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.utils.SolrParamsBuilder;
import com.adr.bigdata.search.vo.Tupple2;
import com.nhb.common.Loggable;

public class CollectionGroupByRuleRequestHandler extends SearchHandler implements Loggable {
	
	private static final String PARAM_FINAL_PRICE = "finalPrice";
	private static final String PARAM_PERCENT_PROMO = "percentPromo";
	
	private static final String PRICE_FILTER_PATTERN = "({!frange l=%s u=%s}$finalPrice)";
	private static final String IS_PROMO_FILTER_PATTERN = "({!frange l=1 u=1000}$isPromoValue)";
	
	private static final String RANK_PRICE = "price";
	private static final String RANK_PROMOTION = "promo";
	
	private static final long TIME_DAY_SECOND = 24 * 60 * 60 * 1000;
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		log.debug("CollectionGroupByRuleRequestHandler: {}", req);
		
		//get request params
		SolrParams param = req.getParams();
		String type = param.get("type");
		getLogger().debug("type==========================: {}", type);
		String[] categoryIds = param.getParams("catid");
		String[] merchantIds = param.getParams("mcid");
		String[] brandIds = param.getParams("brandid");
		String[] prices = param.getParams("price");
		String[] atts = param.getParams("att");
		String rank = param.get("rank");
		String order = param.get("order");
		
//		String offset = param.get("offset", "0");
//		String limit = param.get("limit", "24");
		String offset = param.get("offset");
		String limit = param.get("limit");
		
		
//		String priceParam = param.get(PARAM_FINAL_PRICE);
//		String percentPromoParam = param.get(PARAM_PERCENT_PROMO);
		
		
		Long now = System.currentTimeMillis();
		Long mintime = null;
		ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams(param);
		SolrParamsBuilder builder = new SolrParamsBuilder(modifiableSolrParams);
		
		Tupple2<String, String> timeDuration = null;
		switch (type) {
		case "1"://hang moi ve theo tuan
			mintime = now - 7 * TIME_DAY_SECOND;
			getLogger().debug("1==========================: {}", type);
			break;
		case "2"://month
			mintime = now - 30 * TIME_DAY_SECOND;
			getLogger().debug("2==========================: {}", type);
			break;
		case "3"://quarter
			mintime = now - 90 * TIME_DAY_SECOND;
			getLogger().debug("3==========================: {}", type);
			break;
		case "4"://year
			mintime = now - 360 * TIME_DAY_SECOND;
			getLogger().debug("4==========================: {}", type);
			break;
		case "5"://is promotion
//			builder.keyword(String.format(IS_PROMO_FILTER_PATTERN, isPromoParam));
			builder.filterWithExistField(IS_PROMO_FILTER_PATTERN);
			break;

		default:
			log.error("CollectionGroupByRuleRequestHandler type value : " + param);
			break;
		}
		
		if(mintime != null){
			timeDuration = new Tupple2<String, String>(mintime.toString(), now.toString());
			builder.filterRange(ProductFields.CREATE_TIME, timeDuration);
		}
		
		builder
		.filter(ProductFields.CATEGORY_PATH, categoryIds)
		.filter(ProductFields.MERCHANT_ID, merchantIds)
		.filter(ProductFields.BRAND_ID, brandIds);
		
		if(atts != null && atts.length > 0){
			for(String att: atts){
				String[] mAtt = att.split("-");
				if(mAtt.length > 1){
					String[] attValues= Arrays.copyOfRange(mAtt, 1, mAtt.length);
					builder.filter(ProductFields.attrInt(Integer.valueOf(mAtt[0])), attValues);
				} else {
					log.error("CollectionGroupByRuleRequestHandler att value : " + param);
				}
				
			}
		}
		
		//create final price caution
		if(prices != null && prices != null){
			//final price query
			String[] priceRanges = new String[prices.length];
			for(int i = 0; i < prices.length; i++){
				String price = prices[i];
				String[] mPrice = price.split("-");
				if(mPrice.length > 1){
					String minPrice = mPrice[0];
					String maxPrice = mPrice[1];
					priceRanges[i] = String.format(PRICE_FILTER_PATTERN, minPrice, maxPrice);
					//builder.getParams().add(CommonParams.FQ, priceRanges[i]);
				} else {
					log.error("CollectionGroupByRuleRequestHandler price value : " + param);
				}
			}
			builder.filterWithExistField(priceRanges);
		}
		
		//how to sort list
		if(rank != null && order != null){
			if(rank.equals(RANK_PRICE)){
				builder.add(CommonParams.SORT,  "$"+PARAM_FINAL_PRICE + " " + order);
				//builder.sort(priceParam, order);
			} else if(rank.equals(RANK_PROMOTION)){
				builder.add(CommonParams.SORT, "$"+PARAM_PERCENT_PROMO + " " + order);
			}
		}
		
		
		
		builder.add(CommonParams.ROWS, limit);
		builder.add(CommonParams.START, offset);
		
		req.setParams(builder.getParams());
		getLogger().debug("query: {}", builder.getParams());
		super.handleRequest(req, rsp);
	}
}
