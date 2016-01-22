package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.CategoryBean;
import com.adr.bigdata.fullimport.sql.mapper.CategoryMapper;

public class CategoryDAO extends AbstractDAO {
	private static final String SQL = "[Product_Solr_Get_All_Cat_Path]";
	public Map<Integer, CategoryBean> allCategory() throws InstantiationException, IllegalAccessException, SQLException {
		Map<Integer, CategoryBean> result = new HashMap<>();
		for (CategoryBean bean : getList(SQL, CategoryMapper.class)) {
			result.put(bean.getId(), bean);
		}
		return result;
	}
}
