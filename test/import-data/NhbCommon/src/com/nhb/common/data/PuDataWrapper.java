package com.nhb.common.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PuDataWrapper implements Serializable {

	private static final long serialVersionUID = -1038540720446971124L;
	private Object data;
	private PuDataType type;

	public PuDataWrapper() {
		this.type = PuDataType.NULL;
		this.data = null;
	}

	public PuDataWrapper(Object data, PuDataType type) {
		if (type == null) {
			throw new IllegalArgumentException(
					"Invalid type, null value is not allowed, object: " + data);
		}
		if (type != null && type != PuDataType.NULL && type.isArray()) {
			if (!(data instanceof Collection<?>) && !data.getClass().isArray())
				throw new IllegalArgumentException("Invalid data for type "
						+ type + ", expecting for collection of "
						+ type.getDataClass().getSimpleName());
		}
		if (data == null && type != PuDataType.NULL) {
			throw new IllegalArgumentException("cannot wrap null data");
		}
		this.setType(type);
		this.setData(data);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		if (data != null && data.getClass().isArray()) {
			this.data = arrayToCollection(data.getClass(), data);
		} else {
			this.data = data;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<T> arrayToCollection(Class<T> clazz, Object array) {
		if (array != null) {
			Collection<T> results = new ArrayList<T>();
			for (int i = 0; i < Array.getLength(array); i++) {
				results.add((T) Array.get(array, i));
			}
			return results;
		}
		return null;
	}

	public PuDataType getType() {
		return type;
	}

	public void setType(PuDataType type) {
		this.type = type;
	}

	public List<Object> toTuple() {
		return Arrays.asList(this.type.getTypeId(), this.data);
	}
}