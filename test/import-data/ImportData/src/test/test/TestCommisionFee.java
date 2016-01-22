package test;

import com.nhb.common.data.PuObject;

public class TestCommisionFee extends BaseUnitTest {

	public static void main(String[] args) throws Exception {
		PuObject testData = PuObject
				.fromJSON("{\"merchantId\":12965, \"customCommissionFee\":[\r\n" + 
						"{\"categoryId\":5, \"isNotApplyCommision\":1, \"commisionFee\":10.0, \"updateTime\":123456789},\r\n" + 
						"{\"categoryId\":603, \"isNotApplyCommision\":0, \"commisionFee\":8.0, \"updateTime\":123456789}]}");
		getLogicProcessor(16).execute(testData);
	}

}
