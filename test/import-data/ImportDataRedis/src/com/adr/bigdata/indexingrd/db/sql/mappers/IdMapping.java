package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class IdMapping implements ResultSetMapper<Integer> {

	@Override
	public Integer map(int i, ResultSet rs, StatementContext arg2)
			throws SQLException {
		return rs.getInt("Id");
	}

}
