package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingPromotionBean;
import com.nhb.common.Loggable;

public class WarehouseProductItemMappingIdPromotionStatusMapper implements
		ResultSetMapper<WarehouseProductItemMappingPromotionBean>, Loggable {

	@Override
	public WarehouseProductItemMappingPromotionBean map(int i, ResultSet rs, StatementContext arg2)
			throws SQLException {
		getLogger().debug("user.timezone", System.getProperty("user.timezone"));
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		WarehouseProductItemMappingPromotionBean bean = new WarehouseProductItemMappingPromotionBean();
		bean.setWarehouseProductItemMappingId(rs.getInt("WarehouseProductItemMappingId"));
		bean.setPromotionStatus(rs.getInt("PromotionStatus") == 1);
		bean.setPromotionMappingStatus(rs.getInt("PromotionProductItemStatus") == 1);
		try {
			bean.setStartDateDiscount(new Date(rs.getTimestamp("StartDate", cal).getTime()));
		} catch (Exception e) {
			bean.setStartDateDiscount(new Date(0));
		}
		try {
			bean.setFinishDateDiscount(new Date(rs.getTimestamp("FinishDate", cal).getTime()));
		} catch (Exception e) {
			bean.setFinishDateDiscount(new Date(0));
		}
		try {
			bean.setPromotionPrice(rs.getDouble("PromotionPrice"));
		} catch (Exception e) {
			bean.setPromotionPrice(0);
		}
		return bean;
	}

}
