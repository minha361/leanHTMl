package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.ViewBean;

public class ProductViewMapper implements SQLMapper<ViewBean> {

	@Override
	public ViewBean map(int i, ResultSet rs) throws SQLException {
		ViewBean bean = new ViewBean();
		bean.setProductItemId(rs.getInt("ProductItemId"));
		bean.setViewCount(rs.getInt("ViewCount"));
		return bean;
	}

}
