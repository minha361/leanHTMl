package test;

import com.nhb.common.data.PuObject;

public class TestPromotionModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		PuObject promotion = PuObject
				.fromJSON("{\"id\":1186,\"startDate\":\"2015-06-12 10:37:00\",\"finishDate\":\"2015-06-14 10:34:00\",\"status\":0,\"discountPercent\":20.0,\"updateTime\":1434080133432,\"promotionProductItemMappings\":[{\"warehouseProductItemMappingId\":371567,\"status\":1,\"updateTime\":1434080133432,\"promotionPrice\":120000.0,\"promotionPercentDiscount\":20.0}]}");
		getLogicProcessor(7).execute(promotion);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
