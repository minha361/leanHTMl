package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;

public class CommisionFeeMapper implements SQLMapper<CommisionFeeBean> {

	@Override
	public CommisionFeeBean map(int i, ResultSet rs) throws SQLException {
		CommisionFeeBean bean = new CommisionFeeBean();
		bean.setMerchantId(rs.getInt("MerchantId"));
		bean.setEntityId(rs.getInt("EntityId"));
		bean.setTypeId(rs.getInt("TypeId"));
		bean.setValue(rs.getFloat("Value"));
		return bean;
	}

}
