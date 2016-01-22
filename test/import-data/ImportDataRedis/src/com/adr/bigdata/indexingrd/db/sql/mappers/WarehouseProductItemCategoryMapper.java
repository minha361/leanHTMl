package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;

public class WarehouseProductItemCategoryMapper implements ResultSetMapper<WarehouseProductItemCategoryBean> {

	@Override
	public WarehouseProductItemCategoryBean map(int i, ResultSet rs,
			StatementContext arg2) throws SQLException {
		WarehouseProductItemCategoryBean bean = new WarehouseProductItemCategoryBean();
		bean.setId(rs.getInt("Id"));
		bean.setCategoryId(rs.getInt("CategoryId"));
		return bean;
	}

}
