package com.nhb.common.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

public interface PuObjectRO extends Iterable<Entry<String, PuDataWrapper>>,
		Serializable {
	// generic
	public int size();

	public String toXML();

	public String toJSON();

	public Iterator<Entry<String, PuDataWrapper>> iterator();

	public <T> T get(String fieldName);

	public boolean variableExists(String fieldName);

	public PuObject deepClone();

	public PuDataType typeOf(String field);

	// boolean
	public Boolean getBoolean(String fieldName);

	public Collection<Boolean> getBooleanArray(String fieldName);

	// byte
	public Byte getByte(String fieldName);

	public Collection<Byte> getByteArray(String fieldName);

	// short
	public Short getShort(String fieldName);

	public Collection<Short> getShortArray(String fieldName);

	// int
	public Integer getInteger(String fieldName);

	public Collection<Integer> getIntegerArray(String fieldName);

	// long
	public Long getLong(String fieldName);

	public Collection<Long> getLongArray(String fieldName);

	// float
	public Float getFloat(String fieldName);

	public Collection<Float> getFloatArray(String fieldName);

	// double
	public Double getDouble(String fieldName);

	public Collection<Double> getDoubleArray(String fieldName);

	// string
	public String getString(String fieldName);

	public Collection<String> getStringArray(String fieldName);

	// pu object
	public PuObject getPuObject(String fieldName);

	public Collection<PuObject> getPuObjectArray(String fieldName);

}