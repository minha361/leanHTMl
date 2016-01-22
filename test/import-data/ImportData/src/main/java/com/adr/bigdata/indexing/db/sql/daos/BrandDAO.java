package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.mappers.BrandMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class BrandDAO extends AbstractDAO {

	@SqlQuery("select Id as BrandId, BrandName, BrandStatus, BrandLogos as BrandImage, Description, UpdatedDate from Brand")
	@Mapper(BrandMapper.class)
	public abstract List<BrandBean> getAllBrands();
	
	@SqlQuery("select Id as BrandId, BrandName, BrandStatus, BrandLogos as BrandImage, Description, UpdatedDate from Brand where Id=:brandId")
	@Mapper(BrandMapper.class)
	public abstract BrandBean getBrand(@Bind("brandId") int brandId);
}
