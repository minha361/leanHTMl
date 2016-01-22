/**
 * 
 */
package com.adr.bigdata.indexingrd.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.LandingPageStatusBean;

/**
 * @author ndn
 *
 */
public class LandingPageStatusMapper implements ResultSetMapper<LandingPageStatusBean> {

	@Override
	public LandingPageStatusBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		LandingPageStatusBean bean = new LandingPageStatusBean();
		bean.setWarehouseProductItemMappingId(rs.getInt("Id"));
		bean.setStatus(rs.getInt("Status"));
		bean.setPriority(rs.getInt("Priority"));
		bean.setlStatus(rs.getInt("LStatus"));
		return bean;
	}

}
