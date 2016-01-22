package com.nhb.common.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.w3c.dom.Node;

import com.nhb.eventdriven.impl.BaseEventDispatcher;

@SuppressWarnings("unchecked")
public class PuObject extends BaseEventDispatcher implements PuObjectRW {

	private static final long serialVersionUID = 4261365785439596694L;
	private static final MessagePack msgpack = new MessagePack();
	private static final JSONParser jsonParser = new JSONParser(0);

	private Map<String, PuDataWrapper> dataHolder;
	private byte[] msgpackCache = null;

	public PuObject() {
		this.dataHolder = new ConcurrentHashMap<String, PuDataWrapper>();
	}

	public PuObject(PuObject holder) {
		this();
		this.addAll(holder);
	}

	public PuObject(Object... keyValues) {
		this();
		this.addAll(keyValues);
	}

	public PuObject(Map<?, ?> map) {
		this();
		this.addAll(map);
	}

	protected PuDataWrapper wrapData(Object data) {
		Object obj = data;
		if (obj != null) {
			PuDataWrapper result = new PuDataWrapper();
			if (obj.getClass().isArray() && Array.getLength(obj) > 0) {
				obj = Arrays.asList(obj);
			}
			if (obj instanceof Collection<?>
					&& ((Collection<?>) obj).size() > 0) {
				result = new PuDataWrapper(data,
						PuDataType.getArrayTypeForElementType(PuDataType
								.fromObject(((Collection<?>) obj).iterator()
										.next())));
			} else if (obj instanceof Map<?, ?>) {
				result = new PuDataWrapper(new PuObject((Map<?, ?>) obj),
						PuDataType.PUOBJECT);
			} else {
				PuDataType dataType = PuDataType.fromObject(obj);
				if (dataType != null) {
					result = new PuDataWrapper(obj, dataType);
				} else {
					throw new RuntimeException(
							"data type not found for object: " + data);
				}
			}
			return result;
		} else {
			return new PuDataWrapper(null, PuDataType.NULL);
		}
	}

	public PuObject(String xml) {
		this();
		this.fromXml(xml);
	}

	PuObject(Node node) {
		this();
		PuObjectXmlHelper.parseXML(node, this);
	}

	private synchronized void updateCache(byte[] newByte) {
		this.msgpackCache = newByte;
	}

	@Override
	public Iterator<Entry<String, PuDataWrapper>> iterator() {
		return this.dataHolder.entrySet().iterator();
	}

	@Override
	public Boolean getBoolean(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()) != 0;
		} else if (dataWrapper.getData() instanceof String) {
			return Boolean.valueOf((String) dataWrapper.getData());
		}
		return (Boolean) dataWrapper.getData();
	}

	@Override
	public Collection<Boolean> getBooleanArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<Boolean>) dataWrapper.getData();
	}

	@Override
	public Byte getByte(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null) {
			return null;
		}
		if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()).byteValue();
		} else if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()).byteValue();
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()).byteValue();
		} else if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()).byteValue();
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()).byteValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Byte.valueOf((String) dataWrapper.getData());
		}
		return (Byte) dataWrapper.getData();
	}

	@Override
	public Collection<Byte> getByteArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null) {
			return null;
		}
		return (Collection<Byte>) dataWrapper.getData();
	}

	@Override
	public Short getShort(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()).shortValue();
		} else if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()).shortValue();
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()).shortValue();
		} else if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()).shortValue();
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()).shortValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Short.valueOf((String) dataWrapper.getData());
		}
		return (Short) dataWrapper.getData();
	}

	@Override
	public Collection<Short> getShortArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<Short>) dataWrapper.getData();
	}

	@Override
	public Integer getInteger(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()).intValue();
		} else if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()).intValue();
		} else if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()).intValue();
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()).intValue();
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()).intValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Integer.valueOf((String) dataWrapper.getData());
		}
		return (Integer) dataWrapper.getData();
	}

	@Override
	public Collection<Integer> getIntegerArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null) {
			return null;
		}
		return (Collection<Integer>) dataWrapper.getData();
	}

	@Override
	public Long getLong(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()).longValue();
		} else if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()).longValue();
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()).longValue();
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()).longValue();
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()).longValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Long.valueOf((String) dataWrapper.getData());
		}
		return (Long) dataWrapper.getData();
	}

	@Override
	public Collection<Long> getLongArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<Long>) dataWrapper.getData();
	}

	@Override
	public Float getFloat(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()).floatValue();
		} else if (dataWrapper.getData() instanceof Double) {
			return ((Double) dataWrapper.getData()).floatValue();
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()).floatValue();
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()).floatValue();
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()).floatValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Float.valueOf((String) dataWrapper.getData());
		}
		return (Float) dataWrapper.getData();
	}

	@Override
	public Collection<Float> getFloatArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<Float>) dataWrapper.getData();
	}

	@Override
	public Double getDouble(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		if (dataWrapper.getData() instanceof Long) {
			return ((Long) dataWrapper.getData()).doubleValue();
		} else if (dataWrapper.getData() instanceof Integer) {
			return ((Integer) dataWrapper.getData()).doubleValue();
		} else if (dataWrapper.getData() instanceof Float) {
			return ((Float) dataWrapper.getData()).doubleValue();
		} else if (dataWrapper.getData() instanceof Short) {
			return ((Short) dataWrapper.getData()).doubleValue();
		} else if (dataWrapper.getData() instanceof Byte) {
			return ((Byte) dataWrapper.getData()).doubleValue();
		} else if (dataWrapper.getData() instanceof String) {
			return Double.valueOf((String) dataWrapper.getData());
		}
		return ((Double) dataWrapper.getData()).doubleValue();
	}

	@Override
	public Collection<Double> getDoubleArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<Double>) dataWrapper.getData();
	}

	@Override
	public String getString(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null || dataWrapper.getData() == null) {
			return null;
		}
		if (dataWrapper.getData() instanceof String) {
			return (String) dataWrapper.getData();
		}
		return dataWrapper.getData().toString();
	}

	@Override
	public Collection<String> getStringArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<String>) dataWrapper.getData();
	}

	@Override
	public PuObject getPuObject(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (PuObject) dataWrapper.getData();
	}

	@Override
	public Collection<PuObject> getPuObjectArray(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.get(fieldName);
		if (dataWrapper == null)
			return null;
		return (Collection<PuObject>) dataWrapper.getData();
	}

	@Override
	public void setBoolean(String fieldName, Boolean value) {
		this.set(fieldName, value, PuDataType.BOOLEAN);
	}

	@Override
	public void setBooleanArray(String fieldName, Collection<Boolean> value) {
		this.set(fieldName, value, PuDataType.BOOLEAN_ARRAY);
	}

	@Override
	public void setByte(String fieldName, Byte value) {
		this.set(fieldName, value, PuDataType.BYTE);
	}

	@Override
	public void setByteArray(String fieldName, Collection<Byte> value) {
		this.set(fieldName, value, PuDataType.BYTE_ARRAY);
	}

	@Override
	public void setShort(String fieldName, Short value) {
		this.set(fieldName, value, PuDataType.SHORT);
	}

	@Override
	public void setShortArray(String fieldName, Collection<Short> value) {
		this.set(fieldName, value, PuDataType.SHORT_ARRAY);
	}

	@Override
	public void setInteger(String fieldName, Integer value) {
		this.set(fieldName, value, PuDataType.INTEGER);
	}

	@Override
	public void setIntegerArray(String fieldName, Collection<Integer> value) {
		this.set(fieldName, value, PuDataType.INTEGER_ARRAY);
	}

	@Override
	public void setLong(String fieldName, Long value) {
		this.set(fieldName, value, PuDataType.LONG);
	}

	@Override
	public void setLongArray(String fieldName, Collection<Long> value) {
		this.set(fieldName, value, PuDataType.LONG_ARRAY);
	}

	@Override
	public void setFloat(String fieldName, Float value) {
		this.set(fieldName, value, PuDataType.FLOAT);
	}

	@Override
	public void setFloatArray(String fieldName, Collection<Float> value) {
		this.set(fieldName, value, PuDataType.FLOAT_ARRAY);
	}

	@Override
	public void setDouble(String fieldName, Double value) {
		this.set(fieldName, value, PuDataType.DOUBLE);
	}

	@Override
	public void setDoubleArray(String fieldName, Collection<Double> value) {
		this.set(fieldName, value, PuDataType.DOUBLE_ARRAY);
	}

	@Override
	public void setString(String fieldName, String value) {
		this.set(fieldName, value, PuDataType.STRING);
	}

	@Override
	public void setStringArray(String fieldName, Collection<String> value) {
		this.set(fieldName, value, PuDataType.STRING_ARRAY);
	}

	@Override
	public void setPuObject(String fieldName, PuObject value) {
		this.set(fieldName, value, PuDataType.PUOBJECT);
	}

	@Override
	public void setPuObjectArray(String fieldName, Collection<PuObject> value) {
		this.set(fieldName, value, PuDataType.PUOBJECT_ARRAY);
	}

	@Override
	public <T> T get(String fieldName) {
		if (this.variableExists(fieldName)) {
			return (T) this.dataHolder.get(fieldName).getData();
		}
		return null;
	}

	@Override
	public void set(String fieldName, Object value, PuDataType type) {
		if (value == null && type != PuDataType.NULL) {
			throw new RuntimeException("value cannot be null for field: `"
					+ fieldName + "` while data type != NULL");
		}
		this.set(fieldName, new PuDataWrapper(value, type));
	}

	private void _set(String fieldName, PuDataWrapper data) {
		if (fieldName == null) {
			throw new RuntimeException("field name cannot be null");
		}

		if (data == null) {
			data = new PuDataWrapper(null, PuDataType.NULL);
		}
		try {
			this.dataHolder.put(fieldName, data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.msgpackCache != null) {
			this.updateCache(null);
		}
	}

	@Override
	public void set(String fieldName, PuDataWrapper data) {
		this._set(fieldName, data);
		this.dispatchEvent(new PuObjectEvent(PuObjectEvent.CHANGE));
	}

	@Override
	public boolean variableExists(String fieldName) {
		return this.dataHolder.containsKey(fieldName);
	}

	@Override
	public Object remove(String fieldName) {
		PuDataWrapper dataWrapper = this.dataHolder.remove(fieldName);
		try {
			if (dataWrapper != null) {
				return dataWrapper.getData();
			}
			return null;
		} finally {
			if (this.msgpackCache != null) {
				this.updateCache(null);
			}
		}
	}

	@Override
	public void addAll(PuObjectRO source) {
		if (source != null) {
			for (Entry<String, PuDataWrapper> entry : source) {
				if (entry.getValue().getType() == PuDataType.PUOBJECT) {
					PuObject puObject = new PuObject();
					if (this.dataHolder.get(entry.getKey()) != null
							&& this.dataHolder.get(entry.getKey()).getType() == PuDataType.PUOBJECT) {
						puObject.addAll((PuObjectRO) this.getPuObject(entry
								.getKey()));
					}
					puObject.addAll((PuObjectRO) entry.getValue().getData());
					this.set(entry.getKey(), puObject, PuDataType.PUOBJECT);
				} else if (entry.getValue().getType() == PuDataType.PUOBJECT_ARRAY) {
					Collection<PuObject> dest = null;
					Collection<PuObject> src = (Collection<PuObject>) entry
							.getValue().getData();
					if (src != null) {
						dest = new ArrayList<PuObject>();
						for (PuObject puObjSrc : src) {
							PuObject puObjDest = new PuObject();
							puObjDest.addAll(puObjSrc);
							dest.add(puObjDest);
						}
					}
					this.set(entry.getKey(), dest, PuDataType.PUOBJECT_ARRAY);
				} else {
					this.set(entry.getKey(), entry.getValue().getData(), entry
							.getValue().getType());
				}
			}
		}
	}

	public void addAll(Map<?, ?> map) {
		if (map != null && map.size() > 0) {
			for (Entry<?, ?> entry : map.entrySet()) {
				this.set((String) entry.getKey(),
						this.wrapData(entry.getValue()));
			}
		}
	}

	public void addAll(Object... keyValues) {
		if (keyValues != null) {
			for (int i = 0; i < keyValues.length - 1; i += 2) {
				String key = (String) keyValues[i];
				Object value = keyValues[i + 1];
				this.set(key, wrapData(value));
			}
		}
	}

	private String toString(int numTab, StringBuilder _builder) {
		StringBuilder builder = _builder == null ? new StringBuilder()
				: _builder;
		String tabs = "";
		if (numTab > 0) {
			for (int i = 0; i < numTab; i++) {
				tabs += "\t";
			}
		}
		builder.append("{\n");
		int count = 0;
		for (Entry<String, PuDataWrapper> entry : this) {
			if (count > 0) {
				builder.append(",\n");
			}
			builder.append("\t")
					.append(tabs)
					.append(entry.getKey())
					.append(":")
					.append(entry.getValue().getType().getNameIgnoreArray())
					.append((entry.getValue().getType().isArray() ? "[]" : "")
							+ " = ");
			if (entry.getValue().getType().isArray()) {
				builder.append("[");
				Collection<Object> coll = (Collection<Object>) entry.getValue()
						.getData();
				boolean isFirst = true;
				if (entry.getValue().getType() == PuDataType.PUOBJECT_ARRAY) {
					for (Object obj : coll) {
						PuObject puObject = (PuObject) obj;
						if (!isFirst) {
							builder.append(",");
						}
						puObject.toString(numTab + 1, builder);
						if (isFirst) {
							isFirst = false;
						}
					}
				} else {
					for (Object obj : coll) {
						if (!isFirst) {
							builder.append(",");
						}
						builder.append(obj);
						if (isFirst) {
							isFirst = false;
						}
					}
				}
				builder.append("]");
			} else {
				if (entry.getValue().getType() == PuDataType.PUOBJECT) {
					((PuObject) entry.getValue().getData()).toString(
							numTab + 1, builder);
				} else {
					builder.append(entry.getValue().getData());
				}
			}
			count++;
		}
		builder.append("\n").append(tabs).append("}");
		return builder.toString();
	}

	@Override
	public String toString() {
		return this.toString(0, null);
	}

	@Override
	public String toXML() {
		return PuObjectXmlHelper.generateXMLFromPuObject(this);
	}

	@Override
	public void fromXml(String xml) {
		try {
			PuObjectXmlHelper.parseXml(xml, this);
		} catch (Exception e) {
			throw new RuntimeException("parse xml error", e);
		}
	}

	public static PuObject fromXml(Node node) {
		return new PuObject(node);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public PuObject deepClone() {
		PuObject puObject = new PuObject();
		for (Entry<String, PuDataWrapper> entry : this) {
			switch (entry.getValue().getType()) {
			case PUOBJECT:
				puObject.setPuObject(entry.getKey(), ((PuObject) entry
						.getValue().getData()).deepClone());
				break;
			case PUOBJECT_ARRAY:
				Collection<PuObject> src = this
						.getPuObjectArray(entry.getKey());
				Collection<PuObject> dest = new ArrayList<PuObject>();
				for (PuObject puo : src) {
					dest.add(puo.deepClone());
				}
				puObject.setPuObjectArray(entry.getKey(), dest);
				break;
			default:
				if (entry.getValue().getType().isArray()) {
					puObject.set(entry.getKey(), new ArrayList(
							(Collection) entry.getValue().getData()), entry
							.getValue().getType());
				} else {
					puObject.set(entry.getKey(), entry.getValue().getData(),
							entry.getValue().getType());
				}
				break;
			}
		}
		return puObject;
	}

	@Override
	public PuDataType typeOf(String field) {
		if (field != null && this.variableExists(field)) {
			return this.dataHolder.get(field).getType();
		}
		return null;
	}

	@Override
	public int size() {
		return this.dataHolder.size();
	}

	private JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		for (Entry<String, PuDataWrapper> entry : this) {
			switch (entry.getValue().getType()) {
			case PUOBJECT:
				json.put(entry.getKey(),
						((PuObject) entry.getValue().getData()).toJSONObject());
				break;
			case PUOBJECT_ARRAY:
				JSONArray dest = new JSONArray();
				for (PuObject puo : (Collection<PuObject>) entry.getValue()
						.getData()) {
					dest.add(puo.toJSONObject());
				}
				json.put(entry.getKey(), dest);
				break;
			default:
				json.put(entry.getKey(), entry.getValue().getData());
				break;
			}
		}
		return json;
	}

	@Override
	public String toJSON() {
		return this.toJSONObject().toString();
	}

	public static PuObject fromJSONObject(JSONObject jsonObject) {
		PuObject result = new PuObject();
		for (String key : jsonObject.keySet()) {
			Object data = jsonObject.get(key);
			if (data != null) {
				PuDataType type = null;
				if (data instanceof JSONObject) {
					data = fromJSONObject((JSONObject) data);
					type = PuDataType.PUOBJECT;
				} else if (data instanceof JSONArray) {
					JSONArray arr = (JSONArray) data;
					List<Object> list = new ArrayList<Object>();
					if (arr.size() > 0) {
						if (arr.get(0) instanceof JSONObject) {
							type = PuDataType.PUOBJECT_ARRAY;
						} else {
							type = PuDataType
									.getArrayTypeForElementType(PuDataType
											.fromObject(arr.get(0)));
							if (type == null) {
								type = PuDataType.STRING_ARRAY;
							}
						}
						if (type == PuDataType.PUOBJECT_ARRAY) {
							for (Object obj : arr) {
								list.add(fromJSONObject((JSONObject) obj));
							}
						} else {
							for (Object obj : arr) {
								list.add(obj);
							}
						}
						data = list;
					} else {
						type = PuDataType.STRING_ARRAY;
						for (Object obj : arr) {
							list.add(obj.toString());
						}
						data = list;
					}
				} else {
					type = PuDataType.fromObject(data);
					if (type == null) {
						type = PuDataType.STRING;
						data = data.toString();
					}
				}
				result.set(key, data, type);
			} else {
				result.set(key, null);
			}
		}
		return result;
	}

	public static PuObject fromJSON(String json) throws ParseException {
		if (json != null) {
			return fromJSONObject((JSONObject) jsonParser.parse(json));
		}
		return null;
	}

	public byte[] toMessagePack() throws IOException {
		if (this.msgpackCache == null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Packer packer = msgpack.createPacker(out);
			PuObjectTemplate.getInstance().write(packer, this);
			this.updateCache(out.toByteArray());
		}
		return this.msgpackCache;

	}

	public static PuObject fromMessagePack(byte[] bytes) throws IOException {
		if (bytes != null) {
			if (bytes.length == 0) {
				return new PuObject();
			}
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			Unpacker unpacker = msgpack.createUnpacker(in);
			return PuObjectTemplate.getInstance().read(unpacker, null);
		}
		return null;
	}

	@Override
	public void setBooleanArray(String fieldName, boolean[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setByteArray(String fieldName, byte[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setShortArray(String fieldName, short[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setIntegerArray(String fieldName, int[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setLongArray(String fieldName, long[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setFloatArray(String fieldName, float[] value) {
		this.set(fieldName, this.wrapData(value));

	}

	@Override
	public void setDoubleArray(String fieldName, double[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setStringArray(String fieldName, String[] value) {
		this.set(fieldName, this.wrapData(value));
	}

	@Override
	public void setPuObjectArray(String fieldName, PuObject[] value) {
		this.set(fieldName, this.wrapData(value));
	}
}