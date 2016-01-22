package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.TrendingKeywordBean;
import com.adr.bigdata.indexing.db.sql.mappers.TrendingKeywordMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class TrendingKeywordDAO extends AbstractDAO {
	
	private static final String sql = "select schema_field_name, field_value, status, keyword, priority from keyword_config where status=1";
	
	@SqlQuery(sql)
	@Mapper(TrendingKeywordMapper.class)
	public abstract List<TrendingKeywordBean> getAllTrending();
}
