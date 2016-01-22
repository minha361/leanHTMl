package com.mario.consumer.test;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.mario.consumer.statics.Fields;
import com.nhb.common.data.PuObject;

public class ExecuteCommandHandler extends BaseRequestHandler {

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		if (data != null) {
			if (data.variableExists(Fields.COMMAND)) {
				String cmd = data.getString(Fields.COMMAND);
				getLogger().debug("executing command: " + cmd);
				Process proc = Runtime.getRuntime().exec(
						data.getString(Fields.COMMAND));
				StringWriter sw = new StringWriter();
				IOUtils.copy(proc.getInputStream(), sw);
				return sw.toString();
			}
		}
		return null;
	}
}
