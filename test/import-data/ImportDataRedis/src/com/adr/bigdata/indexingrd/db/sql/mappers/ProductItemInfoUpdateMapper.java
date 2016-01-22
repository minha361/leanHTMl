package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;

public class ProductItemInfoUpdateMapper implements
		ResultSetMapper<ProductItemInfoUpdateBean> {

	@Override
	public ProductItemInfoUpdateBean map(int i, ResultSet rs,
			StatementContext arg2) throws SQLException {
		ProductItemInfoUpdateBean bean = new ProductItemInfoUpdateBean();
		bean.setWhpiId(rs.getInt("Id"));
		bean.setProductItemId(rs.getInt("ProductItemId"));
		bean.setProvinceId(rs.getInt("ProvinceId"));
		return bean;
	}

}
