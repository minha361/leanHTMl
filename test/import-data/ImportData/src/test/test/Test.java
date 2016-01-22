package test;

import java.io.BufferedReader;
import java.io.FileReader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.nhb.common.data.PuObject;

public class Test extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		FileReader reader = new FileReader("data/data.dat");
		BufferedReader br = new BufferedReader(reader);
		String curLine;

		try {
			while ((curLine = br.readLine()) != null) {
				JSONArray arr = (JSONArray) JSONValue.parse(curLine);
				int type = (Integer) arr.get(0);
				Object data = arr.get(1);

				String sData;
				if (data instanceof JSONArray) {
					sData = "{\"list\":" + ((JSONArray) data).toJSONString() + "}";
				} else if (data instanceof JSONObject) {
					sData = ((JSONObject) data).toJSONString();
				} else {
					throw new IllegalArgumentException("fuck");
				}
				// System.out.println(sData);
				getLogicProcessor(type).execute(PuObject.fromJSON(sData));
			}
		} finally {
			br.close();
			reader.close();
		}
	}
}
