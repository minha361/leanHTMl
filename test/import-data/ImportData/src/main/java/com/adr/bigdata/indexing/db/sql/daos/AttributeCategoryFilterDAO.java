package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.indexing.db.sql.mappers.AttributeCategoryMappingMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class AttributeCategoryFilterDAO extends AbstractDAO {

	private static final String GET_ALL_CAT_FILTER = "select AttributeId, CategoryId, FilterSpan, BaseUnitId, AttributeType, UnitName, AttributeName from (\n"
			+ "	select ACM.AttributeId, ACM.CategoryId, ACM.FilterSpan, ACM.BaseUnitId, ACM.AttributeType, M.UnitName, A.AttributeName, \n"
			+ "	row_number() over (partition by ACM.AttributeId, ACM.CategoryId order by ACM.BaseUnitId) as num\n"
			+ "	from Attribute_Category_Mapping as ACM\n"
			+ "		left join MeasureUnit as M on ACM.BaseUnitId=M.Id\n"
			+ "		inner join Attribute as A on ACM.AttributeId=A.Id\n"
			+ "	where isFilter=1\n"
			+ ") as tmp\n"
			+ "where num=1";

	@SqlQuery(GET_ALL_CAT_FILTER)
	@Mapper(AttributeCategoryMappingMapper.class)
	public abstract List<AttributeCategoryMappingBean> getAttributeCategoryMappings();
}
