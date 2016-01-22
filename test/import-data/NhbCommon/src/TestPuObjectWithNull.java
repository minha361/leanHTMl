import java.io.IOException;

import net.minidev.json.parser.ParseException;

import com.nhb.common.data.PuObject;

public class TestPuObjectWithNull {

	public static void main(String[] args) throws ParseException, IOException {
		PuObject puo = PuObject.fromJSON("{\"name\": null}");
		System.out.println(puo + " -> type: " + puo.typeOf("name"));
		byte[] bytes = puo.toMessagePack();
		System.out.println(PuObject.fromMessagePack(bytes).getString("name") == null);
	}
}
