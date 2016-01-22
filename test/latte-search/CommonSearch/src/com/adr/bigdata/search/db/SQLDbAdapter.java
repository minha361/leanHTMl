package com.adr.bigdata.search.db;

import javax.sql.DataSource;

import com.nhb.common.Loggable;

public abstract class SQLDbAdapter implements Loggable {

	private String dataSourceName = "default";
	private DataSource dataSource;

	public SQLDbAdapter() {
	}

	public SQLDbAdapter(String name, DataSource dataSource) {
		this.dataSourceName = name;
		this.dataSource = dataSource;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
