package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.AttributeMappingBean;

public class AttributeMappingMapper implements SQLMapper<AttributeMappingBean> {

	@Override
	public AttributeMappingBean map(int i, ResultSet rs) throws SQLException {
		AttributeMappingBean bean = new AttributeMappingBean();
		bean.setAttributeId(rs.getInt("AttributeId"));
		bean.setProductItemId(rs.getInt("ProductItemId"));
		bean.setValue(rs.getString("Value"));
		bean.setAttributeValueId(rs.getInt("AttributeValueId"));
		bean.setAttributeName(rs.getString("AttributeName"));
		return bean;
	}

}
