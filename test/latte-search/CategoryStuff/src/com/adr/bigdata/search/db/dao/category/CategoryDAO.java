package com.adr.bigdata.search.db.dao.category;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.search.db.bean.category.CategoryBean;
import com.adr.bigdata.search.db.dao.AbstractDAO;
import com.adr.bigdata.search.db.mapper.category.CategoryMapper;

public abstract class CategoryDAO extends AbstractDAO {

	@SqlQuery("[Product_Solr_Get_All_Cat_Path]")
	@Mapper(CategoryMapper.class)
	public abstract List<CategoryBean> getAllCats();
}
