package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import com.adr.bigdata.indexing.db.sql.beans.CommisionBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemCategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemMappingPromotionBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseProductItemTypeBean;
import com.adr.bigdata.indexing.db.sql.mappers.IdMapping;
import com.adr.bigdata.indexing.db.sql.mappers.ProductItemInfoUpdateMapper;
import com.adr.bigdata.indexing.db.sql.mappers.WarehouseProductItemCategoryMapper;
import com.adr.bigdata.indexing.db.sql.mappers.WarehouseProductItemMappingIdPromotionStatusMapper;
import com.adr.bigdata.indexing.db.sql.mappers.WarehouseProductItemTypeMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;
import com.adr.bigdata.indexing.db.sql.mappers.CommsionMapper;

@UseStringTemplate3StatementLocator
public abstract class WarehouseProductItemMappingIdDAO extends AbstractDAO {
	private static final String BY_BRAND_ID = "select WH_PI.Id\n"
			+ "from \n"
			+ "	(select Id from Product where BrandId=:brandId) as P\n"
			+ "	inner join (select Id, ProductId from ProductItem) as P_I on P_I.ProductId=P.Id\n"
			+ "	inner join (select Id, ProductItemId from Warehouse_ProductItem_Mapping) as WH_PI on WH_PI.ProductItemId=P_I.Id";

	private static final String BY_CATEGORY_ID = "with HierarchyCTE (ID, ParentID) as (\n"
			+ "	select id, ParentCategoryId\n" + "	from dbo.Category\n" + "	where id = :categoryId\n" + "	union all\n"
			+ "	select Category.id, Category.ParentCategoryId\n" + "	from dbo.Category\n"
			+ "		inner join HierarchyCTE on Category.ParentCategoryId = hierarchycte.id\n" + ")\n"
			+ "select WH_PI.Id, HierarchyCTE.ID as CategoryId\n" + "from \n" + "	HierarchyCTE \n"
			+ "	inner join (select Id, CategoryId from Product) as P on P.CategoryId=HierarchyCTE.ID\n"
			+ "	inner join (select Id, ProductId from ProductItem) as P_I on P_I.ProductId=P.Id\n"
			+ "	inner join Warehouse_ProductItem_Mapping as WH_PI on WH_PI.ProductItemId=P_I.Id";

	private static final String BY_ATTRIBUTE_ID = "select WH_PI.Id\n" + "from \n"
			+ "	(select ProductItemId from Product_Attribute_Mapping where AttributeId=:attributeId) as PAM\n"
			+ "	inner join Warehouse_ProductItem_Mapping as WH_PI on PAM.ProductItemId=WH_PI.ProductItemId";

	private static final String BY_ATTRIBUTE_ID_ATTRIBUTE_VALUE_ID = "select WH_PI.Id\n"
			+ "from \n"
			+ "	(select ProductItemId from Product_Attribute_Mapping where AttributeId=:attributeId and AttributeValueId=:attributeValueId) as PAM\n"
			+ "	inner join Warehouse_ProductItem_Mapping as WH_PI on PAM.ProductItemId=WH_PI.ProductItemId";

	private static final String BY_MERCHANT_ID = "select Id from Warehouse_ProductItem_Mapping where MerchantId=:merchantId";

	private static final String BY_WAREHOUSE_ID = "select WH_PI.Id, P_I.ProductItemType, P_I.ProductItemPolicy from Warehouse_ProductItem_Mapping as WH_PI inner join ProductItem as P_I on WH_PI.ProductItemId=P_I.Id where WarehouseId=:warehouseId";

	private static final String BY_PROMOTION_ID = "with WPIM as (select WarehouseProductItemMappingId from Promotion_ProductItem_Mapping as PPM where PPM.PromotionId=:promotionId)\r\n" + 
			"select * from\r\n" + 
			"	(select *, ROW_NUMBER() over (partition by WarehouseProductItemMappingId order by PromotionProductItemStatus desc, PromotionStatus desc, StartDate asc) as num from\r\n" + 
			"		(select PPM.WarehouseProductItemMappingId, PRO.StartDate, PRO.FinishDate, PPM.PromotionPrice,\r\n" + 
			"			case when PPM.PromotionProductItemStatus=1 and PPM.RemainQuantity>0 then 1 else 0 end as PromotionProductItemStatus,\r\n" + 
			"			case when PRO.PromotionStatus=2 then 1 else 0 end as PromotionStatus\r\n" + 
			"		from WPIM inner join Promotion_ProductItem_Mapping as PPM on PPM.WarehouseProductItemMappingId=WPIM.WarehouseProductItemMappingId\r\n" + 
			"			inner join Promotions as PRO on PRO.Id=PPM.PromotionId\r\n" + 
			"		where PRO.FinishDate>GETDATE()\r\n" + 
			"		) as tmp1\r\n" + 
			"	) as tmp2\r\n" + 
			"where num=1";

	private static final String BY_PRODUCT_ITEM_ID = "select Id from Warehouse_ProductItem_Mapping where ProductItemId=:productItemId";

	private static final String GET_PI_INFO_WHEN_UPDATE = "select WH_PI.ProductItemId, WH_PI.Id, W.ProvinceId from Warehouse_ProductItem_Mapping as WH_PI inner join Warehouse as W on WH_PI.WarehouseId=W.Id where WH_PI.ProductItemId in (<piIds>)";

	private static final String BY_MERCHANT_ID_AND_CAT_IDS = "select WPM.Id, P.CategoryId \r\n" + 
			"from Warehouse_ProductItem_Mapping WPM \r\n" + 
			"	inner join ProductItem P_I on P_I.Id=WPM.ProductItemId\r\n" + 
			"	inner join Product P on P.Id=P_I.ProductId\r\n" + 
			"where WPM.MerchantId=:merchantId AND P.CategoryId IN (<catIds>)";
	
	@SqlQuery(BY_BRAND_ID)
	@Mapper(IdMapping.class)
	public abstract List<Integer> getByBrandId(@Bind("brandId") int brandId);

	@SqlQuery(BY_ATTRIBUTE_ID)
	@Mapper(IdMapping.class)
	public abstract List<Integer> getByAttributeId(@Bind("attributeId") int attributeId);

	@SqlQuery(BY_ATTRIBUTE_ID_ATTRIBUTE_VALUE_ID)
	@Mapper(IdMapping.class)
	public abstract List<Integer> getByAttributeIdAndAttributeValueId(@Bind("attributeId") int attributeId,
			@Bind("attributeValueId") int attributeValueId);

	@SqlQuery(BY_MERCHANT_ID)
	@Mapper(IdMapping.class)
	public abstract List<Integer> getByMerchantId(@Bind("merchantId") int categoryId);

	@SqlQuery(BY_WAREHOUSE_ID)
	@Mapper(WarehouseProductItemTypeMapper.class)
	public abstract List<WarehouseProductItemTypeBean> getByWarehouseId(@Bind("warehouseId") int categoryId);

	@SqlQuery(BY_PROMOTION_ID)
	@Mapper(WarehouseProductItemMappingIdPromotionStatusMapper.class)
	public abstract List<WarehouseProductItemMappingPromotionBean> getByPromotionId(
			@Bind("promotionId") int promotionId);

	@SqlQuery(BY_PRODUCT_ITEM_ID)
	@Mapper(IdMapping.class)
	public abstract List<Integer> getByProductItemId(@Bind("productItemId") int categoryId);

	@SqlQuery(BY_CATEGORY_ID)
	@Mapper(WarehouseProductItemCategoryMapper.class)
	public abstract List<WarehouseProductItemCategoryBean> getByCategoryId(@Bind("categoryId") int categoryId);

	@SqlQuery(GET_PI_INFO_WHEN_UPDATE)
	@Mapper(ProductItemInfoUpdateMapper.class)
	public abstract List<ProductItemInfoUpdateBean> getPIInfoWhenUpdate(@BindIn("piIds") List<Integer> piIds);
	
	@SqlQuery(BY_MERCHANT_ID_AND_CAT_IDS)
	@Mapper(CommsionMapper.class)
	public abstract List<CommisionBean> getByMerchantAndCategories(@Bind("merchantId") int merchantId, @BindIn("catIds") List<Integer> catIds);
}
