package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CategoryPathMaper implements ResultSetMapper<String> {

	@Override
	public String map(int i, ResultSet rs, StatementContext arg2)
			throws SQLException {
		return rs.getString("Path");
	}

}
