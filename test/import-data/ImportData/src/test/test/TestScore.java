/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.nhb.common.data.PuObject;

/**
 * @author ndn
 *
 */
public class TestScore extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		List<PuObject> pis = new ArrayList<PuObject>();

//		PuObject pi;

		JSONArray arr = (JSONArray) JSONValue.parse("[{\"warehouseProductItemMappingId\":1644734,\"score\":0.0,\"jsonScore\":\"{\\\"0\\\":\\\"0\\\",\\\"4\\\":\\\"147200.00000000\\\",\\\"8\\\":\\\"97200.00000000\\\"}\",\"districtsJson\":null,\"updateTime\":1438310680251},{\"warehouseProductItemMappingId\":1644735,\"score\":0.296875,\"jsonScore\":\"{\\\"0\\\":\\\"0.296875\\\",\\\"4\\\":\\\"103500.00000000\\\",\\\"8\\\":\\\"53500.00000000\\\"}\",\"districtsJson\":null,\"updateTime\":1438310680251}]");
		for (Object obj : arr) {
			PuObject pu = PuObject.fromJSON(((JSONObject) obj).toJSONString());
			pis.add(pu);
		}
		
//		pi = PuObject
//				.fromJSON("[12,[{\"warehouseProductItemMappingId\":1644686,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734},{\"warehouseProductItemMappingId\":1644687,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734},{\"warehouseProductItemMappingId\":1644688,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734},{\"warehouseProductItemMappingId\":1644689,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734},{\"warehouseProductItemMappingId\":1644690,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734},{\"warehouseProductItemMappingId\":1644691,\"score\":0.0,\"jsonScore\":\"\\\"0\\\":\\\"2700.00000000\\\",\\\"8\\\":\\\"2700.00000000\\\",\\\"4\\\":\\\"-47300.00000000\\\",\",\"districtsJson\":null,\"updateTime\":1438252899734}]]");
//		pis.add(pi);
		PuObject pris = new PuObject("list", pis);
		getLogicProcessor(12).execute(pris);

		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
