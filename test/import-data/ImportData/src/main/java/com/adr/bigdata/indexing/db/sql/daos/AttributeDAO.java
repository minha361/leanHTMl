package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.AttributeBean;
import com.adr.bigdata.indexing.db.sql.beans.AttributeSingleBean;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueBean;
import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.indexing.db.sql.mappers.AttributeMapper;
import com.adr.bigdata.indexing.db.sql.mappers.AttributeSingleMapper;
import com.adr.bigdata.indexing.db.sql.mappers.AttributeValueMapper;
import com.adr.bigdata.indexing.db.sql.mappers.AttributeValueMeasureUnitDisplayMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class AttributeDAO extends AbstractDAO {
	private static final String GET_UNIT_FOR_ATT_VALUE = "select * from (\r\n" + 
			"	select PAM.AttributeId, PAM.AttributeValueId, AV.Value, M.Ratio as DisplayRatio, M.UnitName as DisplayUnitName,\r\n" + 
			"		ROW_NUMBER() over (partition by PAM.AttributeId, PAM.AttributeValueId order by M.Ratio desc) as num\r\n" + 
			"	from Product_Attribute_Mapping as PAM\r\n" + 
			"		left join MeasureUnit as M on M.Id=PAM.UnitId\r\n" + 
			"		inner join AttributeValue as AV on PAM.AttributeValueId=AV.Id\r\n" + 
			") as tmp\r\n" + 
			"where num=1";

	@SqlQuery("[Product_Solr_Get_All_Attribute]")
	@Mapper(AttributeMapper.class)
	public abstract List<AttributeBean> getAllAtts();

	@SqlQuery("select Id as AttributeId, AttributeName, AttributeStatus from Attribute")
	@Mapper(AttributeSingleMapper.class)
	public abstract List<AttributeSingleBean> getAllAttSingles();

	@SqlQuery("select Id as AttributeId, AttributeName, AttributeStatus from Attribute where Id=:attId")
	@Mapper(AttributeSingleMapper.class)
	public abstract AttributeSingleBean getAttSingle(@Bind("attId") int attId);

	@SqlQuery("select Id as AttributeValueId, AttributeId, Value, AttributeValueStatus from AttributeValue")
	@Mapper(AttributeValueMapper.class)
	public abstract List<AttributeValueBean> getAllAttValues();

	@SqlQuery("select Id as AttributeValueId, AttributeId, Value, AttributeValueStatus from AttributeValue where Id=:attValueId")
	@Mapper(AttributeValueMapper.class)
	public abstract AttributeValueBean getAttValue(@Bind("attValueId") int attValueId);
	
	@SqlQuery(GET_UNIT_FOR_ATT_VALUE)
	@Mapper(AttributeValueMeasureUnitDisplayMapper.class)
	public abstract List<AttributeValueMeasureUnitDisplayBean> getAllDisplayUnit();
}
