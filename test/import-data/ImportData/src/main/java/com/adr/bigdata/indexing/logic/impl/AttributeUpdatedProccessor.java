package com.adr.bigdata.indexing.logic.impl;

import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.nhb.common.data.PuObject;

public class AttributeUpdatedProccessor extends BaseLogicProcessor {
	// private AttributeModel attributeModel;

	@Override
	public void execute(PuObject attribute) throws Exception {
		// long start = System.currentTimeMillis();
		// if (attributeModel == null) {
		// attributeModel = getModel(AttributeModel.class);
		// }
		//
		// Integer attributeId = attribute.getInteger(ATT_ID);
		// String attName = attribute.getString(ATT_NAME);
		// Integer attStatus = attribute.getInteger(ATT_STATUS);
		// Long updateTime = attribute.getLong(ATT_UPDATE_TIME);
		// if (attributeId == null || attName == null || attStatus == null ||
		// updateTime == null) {
		// getLogger().error("missing fields");
		// return;
		// }
		// getLogger().debug("time get detail info: " +
		// (System.currentTimeMillis() - start));
		// // Check cache
		// AttributeSingleBean attributeSingleBean =
		// attributeModel.getAttributeByCache(attributeId);
		// if (attributeSingleBean != null &&
		// attributeSingleBean.getUpdateTime() >= updateTime) {
		// getLogger().info("attributeId: " + attributeId +
		// " has been updated by a newer");
		// return;
		// }
		// getLogger().debug("time check cache: " + (System.currentTimeMillis()
		// - start));
		// // Get warehouseProductItemIds from DB
		// List<Integer> warehouseProductItemmappingIds = attributeModel
		// .getWarehouseProductItemmappingIdsByAttributeId(attributeId);
		// if (warehouseProductItemmappingIds == null) {
		// getLogger().error("can't find warehouseProductItemmappingIds with attributeId: "
		// + attributeId);
		// return;
		// }
		// getLogger().debug("time get from db: " + (System.currentTimeMillis()
		// - start));
		// attributeModel.updateToSolr(warehouseProductItemmappingIds,
		// attributeId, attName, attStatus);
		// getLogger().debug("time update to solr " +
		// (System.currentTimeMillis() - start));
		// // If success, update to cache
		// AttributeSingleBean bean = new AttributeSingleBean();
		// bean.setId(attributeId);
		// bean.setName(attName);
		// bean.setStatus(attStatus);
		// bean.setUpdateTime(updateTime);
		// attributeModel.updateAttributeToCache(bean);
		// getProfillingLogger().debug("time proccess attribute " + attributeId
		// + " is: " + (System.currentTimeMillis() - start));
	}
}
