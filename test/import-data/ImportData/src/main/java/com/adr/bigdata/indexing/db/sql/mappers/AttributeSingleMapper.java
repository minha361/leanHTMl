package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import com.adr.bigdata.indexing.db.sql.beans.AttributeSingleBean;

public class AttributeSingleMapper implements
		ResultSetMapper<AttributeSingleBean> {

	@Override
	public AttributeSingleBean map(int i, ResultSet rs,
			StatementContext arg2) throws SQLException {
		AttributeSingleBean bean = new AttributeSingleBean();
		bean.setId(rs.getInt("AttributeId"));
		bean.setName(rs.getString("AttributeName"));
		bean.setStatus(rs.getInt("AttributeStatus"));
		return null;
	}

}
