package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.ViewBean;
import com.adr.bigdata.fullimport.sql.mapper.ProductViewMapper;

public class ProductViewDAO extends AbstractDAO {
	private static final String sql = "SELECT ProductItemId, "
			+ "CASE WHEN LastUpdated >= convert (DATE,getdate ()) THEN ISNULL (Yesterday,0) ELSE ISNULL (ToDay,0) END AS ViewCount "
			+ "FROM Adayroi_Tracking.dbo.ProductViews";

	public Map<Integer, Integer> allProductView() throws InstantiationException, IllegalAccessException, SQLException {
		Map<Integer, Integer> result = new HashMap<>();
		for (ViewBean bean : getList(sql, ProductViewMapper.class)) {
			result.put(bean.getProductItemId(), bean.getViewCount());
		}
		return result;
	}
}
