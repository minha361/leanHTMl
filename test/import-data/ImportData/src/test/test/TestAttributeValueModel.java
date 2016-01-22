package test;

import com.adr.bigdata.indexing.models.impl.AttributeValueModel;
import com.nhb.common.data.PuObject;

public class TestAttributeValueModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject attV = new PuObject();
		attV.setInteger(AttributeValueModel.ATTV_ID, 1);
		attV.setInteger(AttributeValueModel.ATTV_ATTRIBUTE_ID, 136);
		attV.setString(AttributeValueModel.ATTV_VALUE, "xx");
		attV.setLong(AttributeValueModel.ATTV_UPDATE_TIME, 23456L);

		getLogicProcessor(4).execute(attV);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
