package test;

import com.adr.bigdata.indexing.models.impl.BrandModel;
import com.nhb.common.data.PuObject;

public class TestBrandModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject brand = new PuObject();
		brand.setInteger(BrandModel.BRAND_ID, 1102);
		brand.setInteger(BrandModel.BRAND_STATUS, 0);
		brand.setString(BrandModel.BRAND_NAME, "test1xxxx");
		brand.setString(BrandModel.BRAND_IMAGE, "img_test1");
		brand.setLong(BrandModel.BRAND_UPDATE_TIME, 345L);

		getLogicProcessor(1).execute(brand);
		System.out.println("total time: " + (System.currentTimeMillis() - start));

	}
}
