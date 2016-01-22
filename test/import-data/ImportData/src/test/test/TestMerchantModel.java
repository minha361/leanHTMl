package test;

import com.adr.bigdata.indexing.models.impl.MerchantModel;
import com.nhb.common.data.PuObject;

public class TestMerchantModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject mc = new PuObject();
		mc.setInteger(MerchantModel.MC_ID, 2282);
		mc.setString(
				MerchantModel.MC_NAME,
				"test_2282");
		mc.setInteger(MerchantModel.MC_STATUS, 1);
		mc.setString(
				MerchantModel.MC_IMAGE,
				"vi_imageh77kkk");
		mc.setLong(MerchantModel.MC_UPDATE_TIME, 1000L);

		getLogicProcessor(5).execute(mc);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
