package com.adr.bigdata.indexing.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.KeywordBean;
import com.adr.bigdata.indexing.db.sql.mappers.KeywordMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class KeywordDAO extends AbstractDAO {

	@SqlQuery("select Id, Name, link from Keyword")
	@Mapper(KeywordMapper.class)
	public abstract List<KeywordBean> getKeywords();

}
