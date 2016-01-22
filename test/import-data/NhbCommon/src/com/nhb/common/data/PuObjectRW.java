package com.nhb.common.data;

import java.util.Collection;

public interface PuObjectRW extends PuObjectRO {
	// generic
	public void fromXml(String xml);

	public void set(String fieldName, Object value, PuDataType type);

	public void set(String fieldName, PuDataWrapper data);

	public Object remove(String fieldName);

	public void addAll(PuObjectRO source);

	// boolean
	public void setBoolean(String fieldName, Boolean value);

	public void setBooleanArray(String fieldName, Collection<Boolean> value);

	public void setBooleanArray(String fieldName, boolean[] value);

	// byte
	public void setByte(String fieldName, Byte value);

	public void setByteArray(String fieldName, Collection<Byte> value);

	public void setByteArray(String fieldName, byte[] value);

	// short
	public void setShort(String fieldName, Short value);

	public void setShortArray(String fieldName, Collection<Short> value);

	public void setShortArray(String fieldName, short[] value);

	// int
	public void setInteger(String fieldName, Integer value);

	public void setIntegerArray(String fieldName, Collection<Integer> value);

	public void setIntegerArray(String fieldName, int[] value);

	// long
	public void setLong(String fieldName, Long value);

	public void setLongArray(String fieldName, Collection<Long> value);

	public void setLongArray(String fieldName, long[] value);

	// float
	public void setFloat(String fieldName, Float value);

	public void setFloatArray(String fieldName, Collection<Float> value);

	public void setFloatArray(String fieldName, float[] value);

	// double
	public void setDouble(String fieldName, Double value);

	public void setDoubleArray(String fieldName, Collection<Double> value);

	public void setDoubleArray(String fieldName, double[] value);

	// string
	public void setString(String fieldName, String value);

	public void setStringArray(String fieldName, Collection<String> value);

	public void setStringArray(String fieldName, String[] value);

	// pu object
	public void setPuObject(String fieldName, PuObject value);

	public void setPuObjectArray(String fieldName, Collection<PuObject> value);

	public void setPuObjectArray(String fieldName, PuObject[] value);
}