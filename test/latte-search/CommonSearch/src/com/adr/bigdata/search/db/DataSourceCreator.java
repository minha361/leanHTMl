package com.adr.bigdata.search.db;

import java.util.Properties;

import javax.sql.DataSource;

import com.nhb.common.Loggable;

public interface DataSourceCreator extends Loggable {

	DataSource createDataSource(Properties props) throws Exception;
}
