package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.AttributeValueModel.ATTV_ATTRIBUTE_ID;
import static com.adr.bigdata.indexingrd.models.impl.AttributeValueModel.ATTV_ID;
import static com.adr.bigdata.indexingrd.models.impl.AttributeValueModel.ATTV_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.AttributeValueModel.ATTV_UPDATE_TIME;
import static com.adr.bigdata.indexingrd.models.impl.AttributeValueModel.ATTV_VALUE;

import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.AttributeValueModel;
import com.nhb.common.data.PuObject;

public class AttributeValueUpdatedProccessor extends BaseLogicProcessor {
	private AttributeValueModel attributeValueModel;

	@Override
	public void execute(PuObject attributeValue) throws Exception {
		long start = System.currentTimeMillis();
		if (attributeValueModel == null) {
			attributeValueModel = getModel(AttributeValueModel.class);
		}

		Integer attributeValueId = attributeValue.getInteger(ATTV_ID);
		Integer attributeId = attributeValue.getInteger(ATTV_ATTRIBUTE_ID);
		String value = attributeValue.getString(ATTV_VALUE);
		Long updateTime = attributeValue.getLong(ATTV_UPDATE_TIME);
		Integer status = attributeValue.getInteger(ATTV_STATUS);
		if (attributeValueId == null || attributeId == null || value == null || status == null || updateTime == null) {
			getLogger().error("missing fields");
			return;
		}

		
		getProfillingLogger().debug(
				"time proccess attributeValue " + attributeValueId + " of attribute " + attributeId + " is "
						+ (System.currentTimeMillis() - start));
	}

}
