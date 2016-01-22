/**
 * 
 */
package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;

/**
 * @author ndn
 *
 */
public class AttributeValueMeasureUnitDisplayMapper implements ResultSetMapper<AttributeValueMeasureUnitDisplayBean> {

	@Override
	public AttributeValueMeasureUnitDisplayBean map(int i, java.sql.ResultSet rs, StatementContext arg2)
			throws SQLException {
		AttributeValueMeasureUnitDisplayBean bean = new AttributeValueMeasureUnitDisplayBean();
		bean.setAttributeId(rs.getInt("AttributeId"));
		bean.setValueId(rs.getInt("AttributeValueId"));
		bean.setValue(rs.getString("Value"));
		bean.setDisplayRatio(rs.getDouble("DisplayRatio"));
		bean.setDisplayUnitName(rs.getString("DisplayUnitName"));
		return bean;
	}

}
