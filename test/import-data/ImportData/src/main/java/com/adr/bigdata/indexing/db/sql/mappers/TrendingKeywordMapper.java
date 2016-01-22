package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.TrendingKeywordBean;

public class TrendingKeywordMapper implements ResultSetMapper<TrendingKeywordBean> {

	@Override
	public TrendingKeywordBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		TrendingKeywordBean bean = new TrendingKeywordBean();
		bean.setFieldName(rs.getString("schema_field_name"));
		bean.setFieldValue(rs.getString("field_value"));
		bean.setKeyword(rs.getString("keyword"));
		try {
			bean.setStatus(rs.getInt("status"));
		} catch (Exception e) {
			bean.setStatus(0);
		}
		try {
			bean.setPriority(rs.getDouble("priority"));
		} catch (Exception e) {
			bean.setPriority(1.0);
		}
		return bean;
	}

}
