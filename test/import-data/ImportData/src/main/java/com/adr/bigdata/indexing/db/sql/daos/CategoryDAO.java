package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.mappers.CategoryMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class CategoryDAO extends AbstractDAO {

	@SqlQuery("[Product_Solr_Get_All_Cat_Path]")
	@Mapper(CategoryMapper.class)
	public abstract List<CategoryBean> getAllCats();
}
