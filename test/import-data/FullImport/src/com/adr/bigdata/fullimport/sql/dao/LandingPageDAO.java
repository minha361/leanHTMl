package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.LandingPageBean;
import com.adr.bigdata.fullimport.sql.mapper.LandingPageMapper;

public class LandingPageDAO extends AbstractDAO {
	private static final String sql = "select LandingPageId, LandingPageGroupId, WarehouseProductItemId, Priority from LandingPage_ProductItem_Mapping where StatusId=1";

	public Map<Integer, List<LandingPageBean>> allLandingPage() throws InstantiationException, IllegalAccessException, SQLException {
		Map<Integer, List<LandingPageBean>> result = new HashMap<>();

		for (LandingPageBean bean : getList(sql, LandingPageMapper.class)) {
			if (result.containsKey(bean.getWarehouseProductItemMappingId())) {
				result.get(bean.getWarehouseProductItemMappingId()).add(bean);
			} else {
				List<LandingPageBean> lst = new ArrayList<>();
				lst.add(bean);
				result.put(bean.getWarehouseProductItemMappingId(), lst);
			}
		}

		return result;
	}
}
