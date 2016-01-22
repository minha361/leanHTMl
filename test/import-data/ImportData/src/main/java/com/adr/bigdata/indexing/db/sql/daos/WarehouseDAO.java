package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.mappers.WarehouseMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class WarehouseDAO extends AbstractDAO {
	
	@SqlQuery("select Id as WarehouseId, WarehouseName, WarehouseStatus, ProvinceId from Warehouse")
	@Mapper(WarehouseMapper.class)
	public abstract List<WarehouseBean> getAllWarehouse();
	
	@SqlQuery("select Id as WarehouseId, WarehouseName, WarehouseStatus, ProvinceId from Warehouse where Id=:warehouseId")
	@Mapper(WarehouseMapper.class)
	public abstract WarehouseBean getWarehouse(@Bind("warehouseId") int warehouseId);
}
