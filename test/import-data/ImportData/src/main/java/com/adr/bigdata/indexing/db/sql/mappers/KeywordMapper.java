package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;

public class KeywordMapper implements ResultSetMapper<KeywordBean> {

	@Override
	public KeywordBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		KeywordBean bean = new KeywordBean();
		bean.setId(rs.getInt("Id"));
		bean.setKeyword(rs.getString("Name").toLowerCase());
		bean.setUrl(rs.getString("Link"));
		return bean;
	}

}
