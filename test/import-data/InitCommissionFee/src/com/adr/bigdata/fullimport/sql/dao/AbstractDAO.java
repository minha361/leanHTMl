package com.adr.bigdata.fullimport.sql.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.adr.bigdata.fullimport.sql.mapper.SQLMapper;

public abstract class AbstractDAO {
	private Statement stmt;

	protected <T> List<T> getList(String sql, Class<? extends SQLMapper<T>> clazz)
			throws InstantiationException, IllegalAccessException, SQLException {
		SQLMapper<T> mapper = clazz.newInstance();
		List<T> result = new ArrayList<>();

		try (ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				result.add(mapper.map(0, rs));
			}
		}

		return result;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

}
