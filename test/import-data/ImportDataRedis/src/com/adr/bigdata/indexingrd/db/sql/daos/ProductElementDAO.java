package com.adr.bigdata.indexingrd.db.sql.daos;

import java.util.Collection;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.ProductElementBean;
import com.adr.bigdata.indexingrd.db.sql.mappers.ProductElementMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class ProductElementDAO extends AbstractDAO {
	
	@SqlQuery("Product_Solr_New")
	@Mapper(ProductElementMapper.class)
	public abstract Collection<ProductElementBean> getAllProductElements();
}
