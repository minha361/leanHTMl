package com.adr.bigdata.dataimport.model.sender;

import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.nhb.common.data.PuObject;

public class BrandMessageSenderModel extends CachedModel {
	public static final String BRAND_ID = "id";
	public static final String BRAND_NAME = "name";
	public static final String BRAND_STATUS = "status";
	public static final String BRAND_IMAGE = "image";
	public static final String BRAND_UPDATE_TIME = "updateTime";
	
	public void proccess(PuObject brand) {
		// send the message to the Brand topic.
		
	}
}
