package com.mario.consumer.test.db.model;

import com.nhb.common.Loggable;
import com.nhb.common.db.sql.DBIAdapter;
import com.nhb.common.db.sql.daos.AbstractDAO;

public class AbstractModel implements Loggable {

	private DBIAdapter dbAdapter;

	protected DBIAdapter getDbAdapter() {
		return dbAdapter;
	}

	void setDbAdapter(DBIAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	protected <T extends AbstractDAO> T openDAO(Class<T> daoClass) {
		assert daoClass != null;
		getLogger().error("debug ", new Exception());
		return this.dbAdapter.openDAO(daoClass);
	}
}
