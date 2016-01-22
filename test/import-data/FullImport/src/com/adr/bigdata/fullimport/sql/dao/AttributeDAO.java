package com.adr.bigdata.fullimport.sql.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.AttributeMappingBean;
import com.adr.bigdata.fullimport.sql.mapper.AttributeMappingMapper;

public class AttributeDAO extends AbstractDAO {
	private static final String SQL = "Product_Solr_Get_All_Attribute";
	
	public List<AttributeMappingBean> allAttributes() throws InstantiationException, IllegalAccessException, SQLException {
		return getList(SQL, AttributeMappingMapper.class);
	}
	
	public Map<Integer, List<AttributeMappingBean>> allAttributesByPI() throws InstantiationException, IllegalAccessException, SQLException {
		Map<Integer, List<AttributeMappingBean>> result = new HashMap<>();
		for (AttributeMappingBean bean : getList(SQL, AttributeMappingMapper.class)) {
			if (result.containsKey(bean.getProductItemId())) {
				result.get(bean.getProductItemId()).add(bean);
			} else {
				List<AttributeMappingBean> lst = new ArrayList<>();
				lst.add(bean);
				result.put(bean.getProductItemId(), lst);
			}
		}
		return result;
	}
}
