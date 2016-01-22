package com.nhb.common.data;

import java.io.IOException;
import java.util.Map.Entry;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

public class PuObjectTemplate extends AbstractTemplate<PuObject> {

	private static final PuObjectTemplate instance = new PuObjectTemplate();

	public static PuObjectTemplate getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(Packer pk, PuObject target, boolean required) throws IOException {
		if (target == null) {
			if (required) {
				throw new MessageTypeException("Attempted to write null");
			}
			pk.writeNil();
			return;
		}
		pk.writeArrayBegin(target.size() * 3);
		for (Entry<String, PuDataWrapper> entry : target) {
			pk.write(entry.getKey());
			pk.write(entry.getValue().getType().getTypeId());
			if (entry.getValue().getType() != PuDataType.NULL) {
				entry.getValue().getType().getTemplate().write(pk, entry.getValue().getData());
			} else {
				pk.writeNil();
			}
		}
		pk.writeArrayEnd();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PuObject read(Unpacker u, PuObject to, boolean required) throws IOException {
		if (!required && u.trySkipNil()) {
			return null;
		}
		PuObject result = to == null ? new PuObject() : to;
		int n = u.readArrayBegin();
		for (int i = 0; i < n; i += 3) {
			String key = u.readString();
			PuDataType dataType = PuDataType.fromId(u.readByte());
			Object data = null;
			if (dataType != PuDataType.NULL) {
				data = dataType.getTemplate().read(u, null);
			}
			result.set(key, data, dataType);
		}
		u.readArrayEnd();
		return result;
	}

}
