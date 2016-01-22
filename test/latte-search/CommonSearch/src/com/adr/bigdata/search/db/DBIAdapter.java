package com.adr.bigdata.search.db;

import org.skife.jdbi.v2.DBI;

import com.adr.bigdata.search.db.dao.AbstractDAO;

public class DBIAdapter extends SQLDbAdapter {

	private DBI dbi;


	public DBI getDBI() {
		if (this.dbi == null) {
			this.dbi = new DBI(this.getDataSource());
		}
		return dbi;
	}

	public <T extends AbstractDAO> T openDAO(Class<T> daoClass) {
		return this.getDBI().open(daoClass);
	}
}
