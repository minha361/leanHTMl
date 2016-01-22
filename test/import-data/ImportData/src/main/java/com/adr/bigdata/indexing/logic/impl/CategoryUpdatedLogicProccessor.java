package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.CategoryModel.CAT_ID;
import static com.adr.bigdata.indexing.models.impl.CategoryModel.CAT_PARENT;
import static com.adr.bigdata.indexing.models.impl.CategoryModel.CAT_STATUS;
import static com.adr.bigdata.indexing.models.impl.CategoryModel.CAT_UPDATE_TIME;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.CategoryModel;
import com.nhb.common.data.PuObject;

public class CategoryUpdatedLogicProccessor extends BaseLogicProcessor {
	private CategoryModel categoryModel;

	@Override
	public void execute(PuObject category) throws Exception {
		long start = System.currentTimeMillis();
		if (categoryModel == null) {
			categoryModel = getModel(CategoryModel.class);
		}

		Integer categoryId = category.getInteger(CAT_ID);
		Integer cParent = category.getInteger(CAT_PARENT);
		Integer cStatus = category.getInteger(CAT_STATUS);
		Long updateTime = category.getLong(CAT_UPDATE_TIME);
		if (categoryId == null || cParent == null || cStatus == null || updateTime == null) {
			getLogger().error("missing fields");
			return;
		}

		// Check + update cache
		Map<Integer, CategoryBean> cats;
		CategoryBean categoryBean = categoryModel.getCategoryByCache(categoryId);
		if (categoryBean != null && categoryBean.getUpdateTime() >= updateTime) {
			getLogger().info("categoryId: " + categoryId + " has been upadated by a newer");
			return;
		} else {
			List<CategoryBean> beans = categoryModel.getAllCategories();
			for (CategoryBean bean : beans) {
				if (bean.getId() == categoryId) {
					bean.setUpdateTime(updateTime);
				}
			}
			cats = categoryModel.updateCategoryToCache(beans);
		}

		// Get warehouseProductItemIds from DB
		List<WarehouseProductItemCategoryBean> warehouseProductItemmappingIds = categoryModel
				.getWarehouseProductItemCategoryBeansByCat(categoryId);
		if (warehouseProductItemmappingIds == null) {
			getLogger().error("can't find warehouseProductItemmappingIds with categoryId: " + categoryId);
			return;
		}

		if (cats.isEmpty()) {
			for (CategoryBean cat : categoryModel.getAllCategories()) {
				cats.put(cat.getId(), cat);
			}
		}

		categoryModel.updateToSolr(warehouseProductItemmappingIds, cats);
		
		//dirty code
		try {
			URL url = new URL(getCategoryCoreImport());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.getContent();
		} catch (IOException  e) {
			getLogger().error("Error when update category-info core", e);
		}
		
		getProfillingLogger().debug(
				"time proccess category " + categoryId + " is " + (System.currentTimeMillis() - start));
	}

}
