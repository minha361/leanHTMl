/**
 * 
 */
package com.adr.bigdata.indexing.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexing.db.sql.beans.CollectionGroupProductStatusBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.CollectionGroupProductItemModel;
import com.nhb.common.data.PuObject;

/**
 * @author ndn
 *
 */
public class CollectionGroupProductItemUpdatedProccessor extends BaseLogicProcessor {
	private CollectionGroupProductItemModel model = null;

	@Override
	public void execute(PuObject data) throws Exception {
		if (model == null) {
			model = getModel(CollectionGroupProductItemModel.class);
		}
		
		long start = System.currentTimeMillis();

		Integer collectionGroupId = data.getInteger(CollectionGroupProductItemModel.COLLECTION_GROUP_ID);
//		Integer productItemId = data.getInteger(CollectionGroupProductItemModel.PRODUCT_ITEM_ID);
		Long updateTime = data.getLong(CollectionGroupProductItemModel.UPDATE_TIME);

		if (collectionGroupId == null || updateTime == null) {
			getLogger().error("collectionGroupProductId or updateTime is null");
			return;
		}

		List<CollectionGroupProductStatusBean> warehouseProductItemMappings;
//		if (productItemId != null && productItemId > 0) {
//			warehouseProductItemMappings = model.getWPIMIds(collectionGroupId, productItemId);
//		} else {
			warehouseProductItemMappings = model.getWPIMIds(collectionGroupId);
//		}
		Set<Integer> warehouseProductItemMappingIds = new HashSet<Integer>();
		Map<Integer, CollectionGroupProductStatusBean> mWarehouseProductItemMappings = new HashMap<Integer, CollectionGroupProductStatusBean>();
		warehouseProductItemMappings.forEach(e -> {
			warehouseProductItemMappingIds.add(e.getWarehouseProductItemMappingId());
			mWarehouseProductItemMappings.put(e.getWarehouseProductItemMappingId(), e);
		});

		// Check cache
		List<CollectionGroupProductStatusBean> updatedBeans = new ArrayList<CollectionGroupProductStatusBean>();
		Map<Integer, Long> lastUpdateTimes = model.getLastUpdateTimes(warehouseProductItemMappingIds);
		for (int warehouseProductItemMappingId : warehouseProductItemMappingIds) {
			Long lastUpdateTime = lastUpdateTimes.get(warehouseProductItemMappingId);
			if (lastUpdateTime != null && lastUpdateTime > updateTime) {
				getLogger().info("collectionGroupProduct of warehouseProductItemId: {} has been updated by a newer",
						warehouseProductItemMappingId);
			} else {
				updatedBeans.add(mWarehouseProductItemMappings.get(warehouseProductItemMappingId));
			}
		}

		model.updateToSolr(collectionGroupId, updatedBeans);
		model.updateToCache(updatedBeans, updateTime);

		getProfillingLogger().debug("total time proccess {} of collectionGroupProduct is {}", updatedBeans.size(),
				(System.currentTimeMillis() - start));
	}

}
