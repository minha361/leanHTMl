package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.CategoryModel.CAT_ID;
import static com.adr.bigdata.indexingrd.models.impl.CategoryModel.CAT_PARENT;
import static com.adr.bigdata.indexingrd.models.impl.CategoryModel.CAT_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.CategoryModel.CAT_UPDATE_TIME;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.CategoryModel;
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
