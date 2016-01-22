package test;

import com.nhb.common.data.PuObject;

public class TestCollectionGroup extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		
		PuObject obj = PuObject.fromJSON("{\"collectionGroupId\":1, \"updateTime\":" +start +"}");

		getLogicProcessor(18).execute(obj);

		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
