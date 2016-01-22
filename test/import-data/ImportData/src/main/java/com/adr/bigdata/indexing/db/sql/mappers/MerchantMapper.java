package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;

public class MerchantMapper implements ResultSetMapper<MerchantBean> {

	@Override
	public MerchantBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		MerchantBean bean = new MerchantBean();
		bean.setId(rs.getInt("MerchantId"));
		bean.setName(rs.getString("MerchantName"));
		bean.setStatus(rs.getInt("MerchantStatus"));
		bean.setImage(rs.getString("MerchantImage"));
		bean.setInfo(rs.getString("Info"));
		bean.setUpdateTime(rs.getTimestamp("UpdatedDate").getTime());
		return bean;
	}

}
