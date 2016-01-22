/**
 * 
 */
package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.CollectionGroupProductStatusBean;

/**
 * @author ndn
 * must update after CM update
 */
public class CollectionGroupProductStatusMapper implements ResultSetMapper<CollectionGroupProductStatusBean> {

	@Override
	public CollectionGroupProductStatusBean map(int i, ResultSet rs, StatementContext arg2) throws SQLException {
		CollectionGroupProductStatusBean bean = new CollectionGroupProductStatusBean();
		bean.setWarehouseProductItemMappingId(rs.getInt("Id"));
		bean.setStatus(rs.getInt("Status"));
		bean.setPriority(rs.getInt("Priority"));
		return bean;
	}

}
