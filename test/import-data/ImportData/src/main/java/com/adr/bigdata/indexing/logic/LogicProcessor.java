package com.adr.bigdata.indexing.logic;

import org.slf4j.Logger;

import com.nhb.common.Loggable;
import com.nhb.common.data.PuObject;

public interface LogicProcessor extends Loggable {

	void execute(PuObject data) throws Exception;

	default Logger getProfillingLogger() {
		return getLogger(System.getProperty("logger.profilling", "profilling"));
	}
}
