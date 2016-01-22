package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.CategoryBean;
import com.google.common.base.Strings;

public class CategoryMapper implements SQLMapper<CategoryBean> {

	@Override
	public CategoryBean map(int row, ResultSet rs) throws SQLException {
		CategoryBean bean = new CategoryBean();
		bean.setId(rs.getInt("Id"));
		bean.setName(rs.getString("Name"));
		bean.setStatus(rs.getInt("Status"));
		String sPath = rs.getString("Path");
		if (!Strings.isNullOrEmpty(sPath)) {
			String[] arrPath = sPath.split(",");
			int[] path = new int[arrPath.length];
			for (int i = 0; i < arrPath.length; i++) {
				try {
					path[i] = Integer.parseInt(arrPath[i]);
				} catch (NumberFormatException e) {
					getLogger().warn("There is an element in path is not an integer");
				}
			}
			bean.setPath(path);
		}
		String sTree = rs.getString("Tree");
		if (!Strings.isNullOrEmpty(sTree)) {
			bean.setTree(sTree.split(">>"));
		}
		return bean;
	}

}
