/**
 * 
 */
package com.adr.bigdata.indexingrd.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.LandingPageStatusBean;
import com.adr.bigdata.indexingrd.db.sql.mappers.LandingPageStatusMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

/**
 * @author ndn
 *
 */
public abstract class LandingPageDAO extends AbstractDAO {
	/*
	SELECT Id,
	       Priority,
	       Status,
	       (SELECT MAX(tmp.StatusId)
	        FROM LandingPage_ProductItem_Mapping tmp
	        WHERE tmp1.Id = tmp.WarehouseProductItemId
	        AND   tmp.LandingPageId = 2) LStatus
	FROM (SELECT LPPM.WarehouseProductItemId Id,
	             LPPM.Priority,
	             LPPM.StatusId Status,
	             ROW_NUMBER() OVER (PARTITION BY LPPM.WarehouseProductItemId ORDER BY LPPM.StatusId DESC) num
	      FROM LandingPage_ProductItem_Mapping AS LPPM
	      WHERE LPPM.LandingPageId = 2
	      AND   LPPM.LandingPageGroupId = 3) tmp1
	WHERE num = 1
	*/
	private static final String GET_BY_GROUP = "SELECT Id,\r\n" + 
			"       Priority,\r\n" + 
			"       Status,\r\n" + 
			"       (SELECT MAX(tmp.StatusId)\r\n" + 
			"        FROM LandingPage_ProductItem_Mapping tmp\r\n" + 
			"        WHERE tmp1.Id = tmp.WarehouseProductItemId\r\n" + 
			"        AND   tmp.LandingPageId = :landingPageId) LStatus\r\n" + 
			"FROM (SELECT LPPM.WarehouseProductItemId Id,\r\n" + 
			"             LPPM.Priority,\r\n" + 
			"             LPPM.StatusId Status,\r\n" + 
			"             ROW_NUMBER() OVER (PARTITION BY LPPM.WarehouseProductItemId ORDER BY LPPM.StatusId DESC) num\r\n" + 
			"      FROM LandingPage_ProductItem_Mapping AS LPPM\r\n" + 
			"      WHERE LPPM.LandingPageId = :landingPageId\r\n" + 
			"      AND   LPPM.LandingPageGroupId = :landingPageGroupId) tmp1\r\n" + 
			"WHERE num = 1";
	/*
	SELECT Id,
	       Priority,
	       Status,
	       (SELECT MAX(tmp.StatusId)
	        FROM LandingPage_ProductItem_Mapping tmp
	        WHERE tmp1.Id = tmp.WarehouseProductItemId
	        AND   tmp.LandingPageId = 2) LStatus
	FROM (SELECT LPPM.WarehouseProductItemId Id,
	             LPPM.Priority,
	             LPPM.StatusId Status,
	             ROW_NUMBER() OVER (PARTITION BY LPPM.WarehouseProductItemId ORDER BY LPPM.StatusId DESC) num
	      FROM LandingPage_ProductItem_Mapping AS LPPM
	      WHERE LPPM.LandingPageId = 2
	      AND   LPPM.LandingPageGroupId = 3
	      AND LPPM.ProductItemId = 3333) tmp1
	WHERE num = 1
	*/
	private static final String GET_BY_GROUP_PRODUCT_ITEM = "SELECT Id,\r\n" + 
			"       Priority,\r\n" + 
			"       Status,\r\n" + 
			"       (SELECT MAX(tmp.StatusId)\r\n" + 
			"        FROM LandingPage_ProductItem_Mapping tmp\r\n" + 
			"        WHERE tmp1.Id = tmp.WarehouseProductItemId\r\n" + 
			"        AND   tmp.LandingPageId = :landingPageId) LStatus\r\n" + 
			"FROM (SELECT LPPM.WarehouseProductItemId Id,\r\n" + 
			"             LPPM.Priority,\r\n" + 
			"             LPPM.StatusId Status,\r\n" + 
			"             ROW_NUMBER() OVER (PARTITION BY LPPM.WarehouseProductItemId ORDER BY LPPM.StatusId DESC) num\r\n" + 
			"      FROM LandingPage_ProductItem_Mapping AS LPPM\r\n" + 
			"      WHERE LPPM.LandingPageId = :landingPageId\r\n" + 
			"      AND   LPPM.LandingPageGroupId = :landingPageGroupId" + 
			"	   AND LPPM.ProductItemId = :productItemId) tmp1\r\n" + 
			"WHERE num = 1";

	@SqlQuery(GET_BY_GROUP)
	@Mapper(LandingPageStatusMapper.class)
	public abstract List<LandingPageStatusBean> getByGroup(@Bind("landingPageId") int landingPageId,
			@Bind("landingPageGroupId") int landingPageGroupId);

	@SqlQuery(GET_BY_GROUP_PRODUCT_ITEM)
	@Mapper(LandingPageStatusMapper.class)
	public abstract List<LandingPageStatusBean> getByGroupAndProductItem(@Bind("landingPageId") int landingPageId,
			@Bind("landingPageGroupId") int landingPageGroupId, @Bind("productItemId") int productItemId);
}
