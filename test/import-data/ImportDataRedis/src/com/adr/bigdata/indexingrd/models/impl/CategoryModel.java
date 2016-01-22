package com.adr.bigdata.indexingrd.models.impl;

import java.util.List;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;
import com.adr.bigdata.indexingrd.db.sql.daos.CategoryDAO;
import com.adr.bigdata.indexingrd.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexingrd.models.RdCachedModel;

public class CategoryModel extends RdCachedModel {
	public static final String CAT_ID = "id";
	public static final String CAT_PARENT = "parent";
	public static final String CAT_STATUS = "status";
	public static final String CAT_NAME = "name";
	public static final String CAT_UPDATE_TIME = "updateTime";

	public List<CategoryBean> getAllCategories() throws Exception {
		try (CategoryDAO dao = getDbAdapter().openDAO(CategoryDAO.class)) {
			return dao.getAllCats();
		}
	}

	public List<WarehouseProductItemCategoryBean> getWarehouseProductItemCategoryBeansByCat(int categoryId)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class);) {
			return dao.getByCategoryId(categoryId);
		}
	}

	
}
