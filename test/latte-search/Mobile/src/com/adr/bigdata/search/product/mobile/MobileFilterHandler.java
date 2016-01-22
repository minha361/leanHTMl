package com.adr.bigdata.search.product.mobile;

import java.util.Collection;

import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.search.product.fe.VinHandler;
import com.adr.bigdata.search.product.mobile.logic.MobileFilterLogic;
import com.nhb.eventdriven.Callable;

public class MobileFilterHandler extends VinHandler {
	private MobileFilterLogic mobileFilterLogic;
	
	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			mobileFilterLogic = new MobileFilterLogic(modelFactory);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		try {
			final Collection<AttributeCategoryMappingBean>[] atts = new Collection[1];
			mobileFilterLogic.execute(req, rsp, new Callable() {
				@Override
				public void call(Object... args) {
					atts[0] = (Collection<AttributeCategoryMappingBean>) args[0];
				}
			});

			super.handleRequest(req, rsp);
			mobileFilterLogic.writeRsp(req, rsp, atts[0]);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}
}
