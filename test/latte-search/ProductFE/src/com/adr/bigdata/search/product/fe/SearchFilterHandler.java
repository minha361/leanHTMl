package com.adr.bigdata.search.product.fe;

import java.util.Collection;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.search.product.fe.logic.impl.filter.SearchFilterLogic;
import com.adr.bigdata.search.product.fe.model.RdSearchFilterModel;
import com.adr.bigdata.search.product.fe.redis.RedisAppContextAdapter;
import com.adr.bigdata.search.product.fe.utils.SolrParamUtils;
import com.nhb.eventdriven.Callable;

public class SearchFilterHandler extends VinHandler {
	private SearchFilterLogic searchLogic;
	private RdSearchFilterModel redisModel;
	

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			redisModel = RedisAppContextAdapter.getInstance(readRdProperties(core.getCoreDescriptor()))
					.getSearchFilterModel();
			searchLogic = new SearchFilterLogic(modelFactory);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String cacheKey = SolrParamUtils.transform(req.getParams());

		try {
			String cacheResponse = this.redisModel._get(cacheKey);
			if (cacheResponse == null) {
				//cacheMiss
				try {
					final Collection<AttributeCategoryMappingBean>[] atts = new Collection[1];
					searchLogic.execute(req, rsp, new Callable() {
						@Override
						public void call(Object... args) {
							atts[0] = (Collection<AttributeCategoryMappingBean>) args[0];
						}
					});
					SolrQueryResponse _rsp = new SolrQueryResponse();
					super.handleRequest(req, _rsp);
					searchLogic.writeRsp(req, _rsp, atts[0]);
					String result = outerString(req, _rsp);
					if (!zeroResult(_rsp)) {
						long start = System.currentTimeMillis();
						this.redisModel._put(cacheKey, result);
						getLogger().debug("appending to Redis time.....{}", System.currentTimeMillis() - start);
					}

					ModifiableSolrParams params = new ModifiableSolrParams(req.getParams());
					params.set("vincache", true);
					SolrParams _params = SolrParams.wrapDefaults(params, defaults);
					_params = SolrParams.wrapAppended(_params, appends);
					req.setParams(_params);
					rsp.add("vincache", result);
				} catch (Exception e) {
					error(req, rsp);
					getLogger().error("", e);
				}
			} else {
				//cache hit
				ModifiableSolrParams solrParam = new ModifiableSolrParams(req.getParams());
				solrParam.set("vincache", true);
				SolrParams params = SolrParams.wrapAppended(solrParam, appends);
				params = SolrParams.wrapDefaults(params, defaults);
				req.setParams(params);
				rsp.add("vincache", cacheResponse);
			}

		} catch (Exception cacheEx) {
			//cache failed
			getLogger().warn("cache fail", cacheEx);
			try {
				final Collection<AttributeCategoryMappingBean>[] atts = new Collection[1];
				searchLogic.execute(req, rsp, new Callable() {
					@Override
					public void call(Object... args) {
						atts[0] = (Collection<AttributeCategoryMappingBean>) args[0];
					}
				});

				super.handleRequest(req, rsp);
				searchLogic.writeRsp(req, rsp, atts[0]);
			} catch (Exception ex) {
				error(req, rsp);
				getLogger().error("", ex);
			}
		}

	}

}
