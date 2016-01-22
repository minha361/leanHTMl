package com.adr.bigdata.indexing.models;

import com.nhb.common.Loggable;
import com.nhb.common.db.sql.DBIAdapter;
import com.nhb.common.db.sql.daos.AbstractDAO;

public class AbstractModel implements Loggable {
	private DBIAdapter dbAdapter;

	protected DBIAdapter getDbAdapter() {
		return dbAdapter;
	}

	public void setDbAdapter(DBIAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	protected <T extends AbstractDAO> T openDAO(Class<T> daoClass) {
		assert daoClass != null;
		return this.dbAdapter.openDAO(daoClass);
	}

}
