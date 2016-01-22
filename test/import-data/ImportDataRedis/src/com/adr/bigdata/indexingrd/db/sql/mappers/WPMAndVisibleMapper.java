package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.WPMAndVisibleBean;

public class WPMAndVisibleMapper implements ResultSetMapper<WPMAndVisibleBean> {

	@Override
	public WPMAndVisibleBean map(int i, ResultSet rs, StatementContext context) throws SQLException {
		WPMAndVisibleBean bean = new WPMAndVisibleBean();
		bean.setWpmid(rs.getInt("Id"));
		bean.setIsVisible(rs.getInt("IsVisible"));
		return bean;
	}

}
