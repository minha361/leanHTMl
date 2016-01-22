package com.adr.bigdata.indexingrd.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.WPMAndVisibleBean;
import com.adr.bigdata.indexingrd.db.sql.mappers.BrandMapper;
import com.adr.bigdata.indexingrd.db.sql.mappers.WPMAndVisibleMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class BrandDAO extends AbstractDAO {

	@SqlQuery("select Id as BrandId, BrandName, BrandStatus, BrandLogos as BrandImage, Description, UpdatedDate from Brand")
	@Mapper(BrandMapper.class)
	public abstract List<BrandBean> getAllBrands();
	
	@SqlQuery("select Id as BrandId, BrandName, BrandStatus, BrandLogos as BrandImage, Description, UpdatedDate from Brand where Id=:brandId")
	@Mapper(BrandMapper.class)
	public abstract BrandBean getBrand(@Bind("brandId") int brandId);	

	private static final String BY_BRAND_ID = "select WH_PI.Id, WH_PI.IsVisible\n"
			+ "from \n"
			+ "	(select Id from Product where BrandId=:brandId) as P\n"
			+ "	inner join (select Id, ProductId from ProductItem) as P_I on P_I.ProductId=P.Id\n"
			+ "	inner join (select Id, ProductItemId from Warehouse_ProductItem_Mapping) as WH_PI on WH_PI.ProductItemId=P_I.Id";
	
	@SqlQuery(BY_BRAND_ID)
	@Mapper(WPMAndVisibleMapper.class)
	public abstract List<WPMAndVisibleBean> getWPMByBrandId(@Bind("brandId") int brandId);
}
