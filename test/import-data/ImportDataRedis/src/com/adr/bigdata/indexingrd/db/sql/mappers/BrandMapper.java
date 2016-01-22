package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;

public class BrandMapper implements ResultSetMapper<BrandBean> {

	@Override
	public BrandBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		BrandBean bean = new BrandBean();
		bean.setId(rs.getInt("BrandId"));
		bean.setName(rs.getString("BrandName"));
		bean.setStatus(rs.getInt("BrandStatus"));
		bean.setImage(rs.getString("BrandImage"));
		bean.setDescription(rs.getString("Description"));
		bean.setUpdateTime(rs.getTimestamp("UpdatedDate").getTime());
		return bean;
	}

}
