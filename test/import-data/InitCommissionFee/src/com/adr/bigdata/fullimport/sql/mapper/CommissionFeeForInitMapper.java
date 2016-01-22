package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.CommissionFeeForInitBean;

public class CommissionFeeForInitMapper implements SQLMapper<CommissionFeeForInitBean> {

	@Override
	public CommissionFeeForInitBean map(int i, ResultSet rs) throws SQLException {
		CommissionFeeForInitBean bean = new CommissionFeeForInitBean();
		bean.setId(rs.getInt("Id"));
		bean.setCategoryId(rs.getInt("CategoryId"));
		bean.setMerchantId(rs.getInt("MerchantId"));
		bean.setBrandId(rs.getInt("ProductItemId"));
		bean.setProductItemId(rs.getInt("BrandId"));
		return bean;
	}

}
