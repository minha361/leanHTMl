package com.adr.bigdata.fullimport.sql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.adr.bigdata.fullimport.sql.bean.LandingPageBean;

public class LandingPageMapper implements SQLMapper<LandingPageBean> {

	@Override
	public LandingPageBean map(int i, ResultSet rs) throws SQLException {
		LandingPageBean bean = new LandingPageBean();
		bean.setWarehouseProductItemMappingId(rs.getInt("WarehouseProductItemId"));
		bean.setLandingPageId(rs.getInt("LandingPageId"));
		bean.setLandingPageGroupId(rs.getInt("LandingPageGroupId"));
		bean.setPriority(rs.getInt("Priority"));
		return bean;
	}

}
