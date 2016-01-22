package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.List;

import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.CommissionFeeForInitBean;
import com.adr.bigdata.fullimport.sql.mapper.CommisionFeeMapper;
import com.adr.bigdata.fullimport.sql.mapper.CommissionFeeForInitMapper;

public class CommissionFeeDAO extends AbstractDAO {
	private static final String sql = "select MerchantId, EntityId, TypeId, Value from CommissionFee where StatusId=1";
	public static final String sqlCommissionForInit = "SELECT WPM.Id,\r\n" + 
			"       P.CategoryId,\r\n" + 
			"       WPM.MerchantId,\r\n" + 
			"	   WPM.ProductItemId,\r\n" + 
			"	   P.BrandId\r\n" + 
			"FROM Warehouse_ProductItem_Mapping WPM\r\n" + 
			"  INNER JOIN ProductItem PI ON PI.Id = WPM.ProductItemId\r\n" + 
			"  INNER JOIN Product P ON P.Id = PI.ProductId";
	
	public List<CommisionFeeBean> allCommissionFee() throws InstantiationException, IllegalAccessException, SQLException {
		return getList(sql, CommisionFeeMapper.class);
	}
	
	public List<CommissionFeeForInitBean> allWPM() throws InstantiationException, IllegalAccessException, SQLException {
		return getList(sqlCommissionForInit, CommissionFeeForInitMapper.class);
	}
	
	public static void main(String[] args) {
		System.out.println(sqlCommissionForInit);
	}
}
