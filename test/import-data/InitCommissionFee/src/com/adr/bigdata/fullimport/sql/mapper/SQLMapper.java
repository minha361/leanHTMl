package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.Loggable;

public interface SQLMapper<T> extends Loggable {
	T map(int i, ResultSet rs) throws SQLException;
}
