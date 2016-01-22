package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.AttributeValueModel.ATTV_ATTRIBUTE_ID;
import static com.adr.bigdata.indexing.models.impl.AttributeValueModel.ATTV_ID;
import static com.adr.bigdata.indexing.models.impl.AttributeValueModel.ATTV_UPDATE_TIME;
import static com.adr.bigdata.indexing.models.impl.AttributeValueModel.ATTV_VALUE;
import static com.adr.bigdata.indexing.models.impl.AttributeValueModel.ATTV_STATUS;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.AttributeValueModel;
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

		// Check cache
		AttributeValueBean attributeValueBean = attributeValueModel.getAttributeValueByCache(attributeValueId);
		if (attributeValueBean != null && attributeValueBean.getUpdateTime() >= updateTime) {
			getLogger().info("attributeValueId: " + attributeValueId + " has been updated by a newer");
			return;
		}

		// Get warehouseProductItemIds from DB
		List<Integer> warehouseProductItemmappingIds = attributeValueModel
				.getWarehouseProductItemmappingIdsByAttributeValueId(attributeId, attributeValueId);
		if (warehouseProductItemmappingIds == null) {
			getLogger().error("cant not get warehouseProductItemmappingIds with attributeValueId: " + attributeValueId);
			return;
		}

		attributeValueModel.updateToSolr(warehouseProductItemmappingIds, attributeId, attributeValueId, value, status);

		// If success, update to cache
		AttributeValueBean bean = new AttributeValueBean();
		bean.setId(attributeValueId);
		bean.setAttributeId(attributeId);
		bean.setValue(value);
		bean.setUpdateTime(updateTime);
		attributeValueModel.updateAttributeValueToCache(bean);
		getProfillingLogger().debug(
				"time proccess attributeValue " + attributeValueId + " of attribute " + attributeId + " is "
						+ (System.currentTimeMillis() - start));
	}

}
