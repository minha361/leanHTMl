package com.adr.bigdata.indexingrd.db.sql.daos;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexingrd.db.sql.mappers.MerchantMapper;
import com.nhb.common.db.sql.daos.AbstractDAO;

public abstract class MerchantDAO extends AbstractDAO {

	@SqlQuery("select Id as MerchantId, MerchantName, MerchantStatus, MerchantLogo as MerchantImage, MerchantDescription as Info, UpdatedDate from MerchantProfile")
	@Mapper(MerchantMapper.class)
	public abstract List<MerchantBean> getAllMerchants();
	
	@SqlQuery("select Id as MerchantId, MerchantName, MerchantStatus, MerchantLogo as MerchantImage, MerchantDescription as Info, UpdatedDate from MerchantProfile where Id=:merchantId")
	@Mapper(MerchantMapper.class)
	public abstract MerchantBean getMerchant(@Bind("merchantId") int merchantId);
}
