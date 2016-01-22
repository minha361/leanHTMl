package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueBean;

public class AttributeValueMapper implements ResultSetMapper<AttributeValueBean> {

	@Override
	public AttributeValueBean map(int i, ResultSet rs,
			StatementContext arg2) throws SQLException {
		AttributeValueBean bean = new AttributeValueBean();
		bean.setId(rs.getInt("AttributeValueId"));
		bean.setAttributeId(rs.getInt("AttributeId"));
		bean.setValue(rs.getString("AttributeValue"));
		bean.setStatus(rs.getInt("AttributeValueStatus"));
		return bean;
	}
	
}
