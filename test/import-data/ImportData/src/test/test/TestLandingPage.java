/**
 * 
 */
package test;

import com.nhb.common.data.PuObject;

/**
 * @author ndn
 *
 */
public class TestLandingPage extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		PuObject obj = PuObject.fromJSON("{\"landingPageId\":2, \"landingPageGroupId\":3, \"productItemId\":null, \"updateTime\":2345345678}");

		getLogicProcessor(15).execute(obj);

		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
