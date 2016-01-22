package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.List;

import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;
import com.adr.bigdata.fullimport.sql.mapper.NotApplyCommissionFeeMapper;

public class NotApplyCommisionFeeDAO extends AbstractDAO {
	private static String sql = "select MerchantId, CategoryId, IsApplyCommissionFeeContract from MerchantCustomCommissionFee"; 
	
	public List<NotApplyCommisionFeeBean> allNotApplyCommissionFee() throws InstantiationException, IllegalAccessException, SQLException {
		return getList(sql, NotApplyCommissionFeeMapper.class);
	}
}
