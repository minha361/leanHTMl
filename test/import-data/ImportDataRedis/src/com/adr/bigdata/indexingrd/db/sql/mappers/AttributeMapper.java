package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.AttributeBean;

public class AttributeMapper implements ResultSetMapper<AttributeBean> {

	@Override
	public AttributeBean map(int i, ResultSet r, StatementContext arg2)
			throws SQLException {
		AttributeBean bean = new AttributeBean();
		bean.setProductItemId(r.getInt("ProductItemId"));
		bean.setId(r.getInt("AttributeId"));
		bean.setValue(r.getString("Value"));
		bean.setValueId(r.getInt("AttributeValueId"));
		bean.setActive(r.getBoolean("AttributeStatus"));
		return bean;
	}

}
