package com.adr.bigdata.fullimport.sql.bean.model;

import java.sql.Statement;

import com.adr.bigdata.fullimport.Loggable;

public abstract class AbstractSQLModel implements Loggable {
	protected Statement stmt;

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

}
