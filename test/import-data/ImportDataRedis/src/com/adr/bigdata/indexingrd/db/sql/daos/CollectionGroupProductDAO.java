/**
 * 
 */
package com.adr.bigdata.indexingrd.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.CollectionGroupProductStatusBean;
import com.adr.bigdata.indexingrd.db.sql.mappers.CollectionGroupProductStatusMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

/**
 * @author ndn
 *
 */
public abstract class CollectionGroupProductDAO extends AbstractDAO {
	
	//LandingPage_ProductItem_Mapping
	//CollectionGroupProducts => CGP
	private static final String GET_BY_GROUP = "SELECT Id,\r\n" + 
			"       Priority,\r\n" + 
			"       Status\r\n" + 
			"FROM (SELECT CGP.WarehouseProducItemMappingId Id,\r\n" + 
			"             CGP.Priority,\r\n" + 
			"             CGP.Status Status,\r\n" + 
			"             ROW_NUMBER() OVER (PARTITION BY CGP.WarehouseProducItemMappingId ORDER BY CGP.Status DESC) num\r\n" + 
			"      FROM Adayroi_MarketingManagement.dbo.CollectionGroupProducts AS CGP\r\n" + 
			"      WHERE CGP.CollectionId = :collectionId\r\n" + 
			"      ) tmp1\r\n" + 
			"WHERE num = 1";
	
//	private static final String GET_BY_GROUP_PRODUCT_ITEM = "SELECT Id,\r\n" + 
//			"       Priority,\r\n" + 
//			"       Status,\r\n" + 
//			"       (SELECT MAX(tmp.Status)\r\n" + 
//			"        FROM CollectionGroupProducts tmp\r\n" + 
//			"        WHERE tmp1.Id = tmp.WarehouseProductItemMappingId\r\n" + 
//			"        AND   tmp.CollectionId = :collectionId) LStatus\r\n" + 
//			"FROM (SELECT LPPM.WarehouseProductItemMappingId Id,\r\n" + 
//			"             LPPM.Priority,\r\n" + 
//			"             LPPM.Status Status,\r\n" + 
//			"             ROW_NUMBER() OVER (PARTITION BY LPPM.WarehouseProductItemMappingId ORDER BY LPPM.Status DESC) num\r\n" + 
//			"      FROM CollectionGroupProducts AS LPPM\r\n" + 
//			"      WHERE LPPM.CollectionId = :collectionId\r\n" + 
//			"	   AND LPPM.ProductItemId = :productItemId) tmp1\r\n" + 
//			"WHERE num = 1";

	@SqlQuery(GET_BY_GROUP)
	@Mapper(CollectionGroupProductStatusMapper.class)
	public abstract List<CollectionGroupProductStatusBean> getByGroup(@Bind("collectionId") int collectionId);

//	@SqlQuery(GET_BY_GROUP_PRODUCT_ITEM)
//	@Mapper(CollectionGroupProductStatusMapper.class)
//	public abstract List<CollectionGroupProductStatusBean> getByGroupAndProductItem(@Bind("collectionId") int collectionId,
//			@Bind("productItemId") int productItemId);
}
