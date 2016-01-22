package test;

import java.util.Arrays;

import com.adr.bigdata.indexing.models.impl.PromotionProductItemMappingModel;
import com.nhb.common.data.PuObject;

public class TestPromotionProductItemMappingModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject promotionProductItemMapping1 = new PuObject();
		promotionProductItemMapping1.setInteger(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_ID, 2820);
		promotionProductItemMapping1.setInteger(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_STATUS, 0);
		promotionProductItemMapping1.setLong(PromotionProductItemMappingModel.PPM_WH_PI_MAPPING_UPDATE_TIME, 1L);
		promotionProductItemMapping1.setDouble(PromotionProductItemMappingModel.PPM_PROMOTION_PRICE, 100.002);

		PuObject promotionProductItemMappings = new PuObject("list", Arrays.asList(promotionProductItemMapping1));
		getLogicProcessor(8).execute(promotionProductItemMappings);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
