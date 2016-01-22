package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;

public class NotApplyCommissionFeeMapper implements SQLMapper<NotApplyCommisionFeeBean> {

	@Override
	public NotApplyCommisionFeeBean map(int i, ResultSet rs) throws SQLException {
		NotApplyCommisionFeeBean bean = new NotApplyCommisionFeeBean();
		bean.setMerchantId(rs.getInt("MerchantId"));
		bean.setCategoryId(rs.getInt("CategoryId"));
		bean.setNotApplyCommissionFee(rs.getBoolean("IsApplyCommissionFeeContract"));
		return bean;
	}

}
