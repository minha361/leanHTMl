package com.adr.bigdata.indexingrd.logic.impl;

import java.util.HashMap;
import java.util.Map;

import com.adr.bigdata.indexingrd.logic.LogicProcessor;
import com.nhb.common.data.PuObject;

public final class LogicProcessorFactory {
	private static final Map<Integer, LogicProcessor> map = new HashMap<Integer, LogicProcessor>();

	public static LogicProcessor getLogicProcessor(int type) {
		if (!map.containsKey(type)) {
			switch (type) {
			case 1:
				map.put(type, new BrandUpdatedLogicProcessor());
				break;
			case 2:
				map.put(type, new CategoryUpdatedLogicProccessor());
				break;
			case 3:
				map.put(type, new AttributeUpdatedProccessor());
				break;
			case 4:
				map.put(type, new AttributeValueUpdatedProccessor());
				break;
			case 5:
				map.put(type, new MerchantUpdatedLogicProccessor());
				break;
			case 6:
				map.put(type, new WarehouseUpdatedLogicProccessor());
				break;
			case 7:
				map.put(type, new PromotionUpdatedLogicProccessor());
				break;
			case 8:
				map.put(type, new PromotionProductItemMappingUpdatedLogicProccessor());
				break;
			case 9:
				map.put(type, new WarehouseProductItemMappingUpdatedProccessor());
				break;
			case 10:
				map.put(type, new ProductItemUpdatedLogicProccessor());
				break;
			case 11:
				map.put(type, new LogicProcessor() {
					@Override
					public void execute(PuObject data) throws Exception {
					}
				});
				break;
			case 12:
				map.put(type, new ScoreUpdatedProccessor());
				break;
			case 15:
				map.put(type, new LandingPageUpdatedProccessor());
				break;
			case 16:
				map.put(type, new CommisionUpdatedLogicProcessor());
				break;
			case 17:
				map.put(type, new KeywordUpdatedProccessor());
				break;
			case 18:
				map.put(type, new CollectionGroupProductItemUpdatedProccessor());
				break;
			default:
				throw new IllegalArgumentException("logic type not supported " + type);
			}
		}
		return map.get(type);
	}
}
