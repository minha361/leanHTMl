package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemTypeBean;

public class WarehouseProductItemTypeMapper implements ResultSetMapper<WarehouseProductItemTypeBean> {

	@Override
	public WarehouseProductItemTypeBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		WarehouseProductItemTypeBean bean = new WarehouseProductItemTypeBean();
		bean.setWhpimId(rs.getInt("Id"));
		bean.setProductItemType(rs.getInt("ProductItemType"));
		bean.setProductItemPolicy(rs.getInt("ProductItemPolicy"));
		return bean;
	}

}
