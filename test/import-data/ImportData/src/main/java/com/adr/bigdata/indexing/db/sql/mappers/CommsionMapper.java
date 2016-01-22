package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.CommisionBean;

public class CommsionMapper implements ResultSetMapper<CommisionBean> {

	@Override
	public CommisionBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		CommisionBean bean = new CommisionBean();
		bean.setCatId(rs.getInt("CategoryId"));
		bean.setWpimId(rs.getInt("Id"));
		return bean;
	}

}
