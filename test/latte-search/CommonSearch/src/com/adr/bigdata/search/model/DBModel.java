package com.adr.bigdata.search.model;

import com.adr.bigdata.search.db.DBIAdapter;
import com.adr.bigdata.search.db.dao.AbstractDAO;

public abstract class DBModel implements Model {
	private DBIAdapter dbiAdapter;

	public <T extends AbstractDAO> T openDAO(Class<T> clazz) {
		return getDbiAdapter().openDAO(clazz);
	}
	
	public DBIAdapter getDbiAdapter() {
		return dbiAdapter;
	}

	public void setDbiAdapter(DBIAdapter dbiAdapter) {
		this.dbiAdapter = dbiAdapter;
	}

}
