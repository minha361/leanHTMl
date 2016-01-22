package com.nhb.common.data;

import static org.msgpack.template.Templates.tList;

import java.lang.reflect.Array;

import org.msgpack.template.BooleanTemplate;
import org.msgpack.template.ByteTemplate;
import org.msgpack.template.DoubleTemplate;
import org.msgpack.template.FloatTemplate;
import org.msgpack.template.IntegerTemplate;
import org.msgpack.template.LongTemplate;
import org.msgpack.template.ShortTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;

public enum PuDataType {
	NULL(0),
	BOOLEAN(1, Boolean.class, BooleanTemplate.getInstance()),
	BYTE(2, Byte.class, ByteTemplate.getInstance()),
	SHORT(3, Short.class, ShortTemplate.getInstance()),
	INTEGER(4, Integer.class, IntegerTemplate.getInstance()),
	LONG(5, Long.class, LongTemplate.getInstance()),
	FLOAT(6, Float.class, FloatTemplate.getInstance()),
	DOUBLE(7, Double.class, DoubleTemplate.getInstance()),
	STRING(8, String.class, StringTemplate.getInstance()),
	PUOBJECT(18, PuObject.class, PuObjectTemplate.getInstance()),
	BOOLEAN_ARRAY(9, true, Boolean.class, tList(BooleanTemplate.getInstance())),
	BYTE_ARRAY(10, true, Byte.class, tList(ByteTemplate.getInstance())),
	SHORT_ARRAY(11, true, Short.class, tList(ShortTemplate.getInstance())),
	INTEGER_ARRAY(12, true, Integer.class, tList(IntegerTemplate.getInstance())),
	LONG_ARRAY(13, true, Long.class, tList(LongTemplate.getInstance())),
	FLOAT_ARRAY(14, true, Float.class, tList(FloatTemplate.getInstance())),
	DOUBLE_ARRAY(15, true, Double.class, tList(DoubleTemplate.getInstance())),
	STRING_ARRAY(16, true, String.class, tList(StringTemplate.getInstance())),
	PUOBJECT_ARRAY(17, true, PuObject.class, tList(PuObjectTemplate.getInstance()));

	public static final String ARRAY_SUBFIX = "_array";
	private byte typeId;
	private boolean isArray = false;
	private Class<?> dataType = null;
	private Template<?> template;

	private PuDataType(int typeId) {
		this.typeId = (byte) typeId;
	}

	private PuDataType(int typeId, boolean isArray, Class<?> clazz, Template<?> messagePackTemplate) {
		this.typeId = (byte) typeId;
		this.isArray = isArray;
		this.dataType = clazz;
		this.template = messagePackTemplate;
	}

	private PuDataType(int typeId, Class<?> clazz, Template<?> messagePackTemplate) {
		this.typeId = (byte) typeId;
		this.dataType = clazz;
		this.template = messagePackTemplate;
	}

	public byte getTypeId() {
		return this.typeId;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

	public Class<?> getDataClass() {
		return this.dataType;
	}

	public String getNameIgnoreArray() {
		if (this.isArray && this.getName().endsWith(ARRAY_SUBFIX)) {
			return this.getName().substring(0, this.getName().length() - ARRAY_SUBFIX.length());
		}
		return this.getName();
	}

	public boolean isArray() {
		return this.isArray;
	}

	public static PuDataType fromId(byte id) {
		if (id >= 0) {
			for (PuDataType dt : values()) {
				if (dt.getTypeId() == id) {
					return dt;
				}
			}
		}
		return null;
	}

	public static PuDataType fromObject(Object obj) {
		if (obj != null) {
			for (PuDataType type : values()) {
				if (type.getDataClass() != null) {
					if (obj.getClass().equals(type.getDataClass())) {
						return type;
					}
				}
			}

			// try again...
			for (PuDataType type : values()) {
				if (type.getDataClass() != null) {
					if (type.getDataClass().isAssignableFrom(obj.getClass())) {
						return type;
					}
				}
			}

			// try again...
			if (obj.getClass().isArray() && Array.getLength(obj) > 0) {
				PuDataType arrayType = getArrayTypeForElementType(fromObject(Array.get(obj, 0)));
				if (arrayType != null) {
					return arrayType;
				}
			}
		}
		return null;
	}

	public static PuDataType fromName(String name) {
		if (name != null) {
			for (PuDataType dt : values()) {
				if (dt.name().equalsIgnoreCase(name)) {
					return dt;
				}
			}
		}
		return null;
	}

	public static PuDataType fromClass(Class<?> clazz) {
		for (PuDataType type : values()) {
			if (type.getDataClass() != null) {
				if (clazz.equals(type.getDataClass())) {
					return type;
				}
			}
		}

		// try again...
		for (PuDataType type : values()) {
			if (type.getDataClass() != null) {
				if (type.getDataClass().isAssignableFrom(clazz)) {
					return type;
				}
			}
		}
		return null;
	}

	public static PuDataType getArrayTypeForElementType(PuDataType type) {
		if (type != null) {
			if (type.isArray) {
				return type;
			}
			for (PuDataType v : values()) {
				if (v.isArray) {
					if (type.getName().equalsIgnoreCase(v.getNameIgnoreArray())) {
						return v;
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Template getTemplate() {
		return template;
	}

}