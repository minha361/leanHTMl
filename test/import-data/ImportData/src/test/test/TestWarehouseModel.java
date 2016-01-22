package test;

import com.adr.bigdata.indexing.models.impl.WarehouseModel;
import com.nhb.common.data.PuObject;

public class TestWarehouseModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject wh = new PuObject();
		wh.setInteger(WarehouseModel.WH_ID, 245);
		wh.setInteger(WarehouseModel.WH_STATUS, 0);
		wh.setInteger(WarehouseModel.WH_CITY_ID, 11);
		wh.setLong(WarehouseModel.WH_UPDATE_TIME, 1000000L);

		getLogicProcessor(6).execute(wh);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
