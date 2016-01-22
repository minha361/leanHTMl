package com.mario.consumer.entity.message;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.apache.commons.io.IOUtils;

import com.nhb.common.Loggable;
import com.nhb.common.data.PuObject;

public class BaseMessageDeserializer implements MessageDeserializer, Loggable {

	private static final JSONParser jsonParser = new JSONParser(0);

	@Override
	public Message deserialize(Object data) throws Exception {
		if (data instanceof InputStream) {
			return deserialize(IOUtils.toByteArray((InputStream) data));
		} else if (data instanceof String) {
			getLogger().debug("trying to parse json string: " + data);
			JSONArray jsonArray = (JSONArray) jsonParser.parse((String) data);
			if (jsonArray.size() > 1) {
				int type = safeLongToInt(((Long) jsonArray.get(0)).longValue());
				Object obj = jsonArray.get(1);
				JSONObject jsonObj = null;
				if (obj instanceof JSONObject) {
					jsonObj = (JSONObject) obj;
				} else if (obj instanceof JSONArray) {
					jsonObj = new JSONObject();
					jsonObj.put("list", (JSONArray) obj);
				}
				return new BaseMessage(type, PuObject.fromJSONObject(jsonObj));
			}
		} else if (data instanceof byte[]) {
			return deserialize(new String((byte[]) data));
		} else if (data instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) data;
			if (request.getMethod().equalsIgnoreCase("post")) {
				return deserialize(request.getInputStream());
			} else if (request.getMethod().equalsIgnoreCase("get")) {

				Map<String, String> map = new HashMap<String, String>();
				Enumeration<String> enumeration = request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String key = enumeration.nextElement();
					String values = request.getParameter(key);
					map.put(key, values);
				}

				BaseMessage message = new BaseMessage();
				int type = 0;
				if (map.containsKey("type")) {
					try {
						type = Integer.valueOf((String) map.get("type"));
					} catch (Exception ex) {
						getLogger().error("cannot get type as integer, use default 0", ex);
					}
				}
				PuObject puo = null;
				if (map.containsKey("data")) {
					puo = PuObject.fromJSON((String) map.get("data"));
				}

				if (puo == null) {
					puo = new PuObject(map);
				}

				message.setData(puo);
				message.setType(type);
				return message;
			}
		}
		return null;
	}

	protected static int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}
}
