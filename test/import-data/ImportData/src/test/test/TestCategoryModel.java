package test;

import com.adr.bigdata.indexing.models.impl.CategoryModel;
import com.nhb.common.data.PuObject;

public class TestCategoryModel extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject cat = new PuObject();
		cat.setInteger(CategoryModel.CAT_ID, 2);
		cat.setInteger(CategoryModel.CAT_PARENT, 14000000);
		cat.setInteger(CategoryModel.CAT_STATUS, 1);
		cat.setLong(CategoryModel.CAT_UPDATE_TIME, 1L);
		getLogicProcessor(2).execute(cat);
		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
