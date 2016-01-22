package com.adr.bigdata.search.product.fe;

import java.util.Collection;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.indexing.db.sql.beans.AttributeCategoryMappingBean;
import com.adr.bigdata.search.product.fe.logic.impl.filter.CategoryFilterLogic;
import com.adr.bigdata.search.product.fe.model.RdCacheModel;
import com.adr.bigdata.search.product.fe.redis.RedisAppContextAdapter;
import com.adr.bigdata.search.product.fe.utils.SolrParamUtils;
import com.nhb.eventdriven.Callable;

public class CategoryFilterHandler extends VinHandler {
	private CategoryFilterLogic catLogic;
	private RdCacheModel redisModel;
	private int rdMaxKeySize = 100000;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			catLogic = new CategoryFilterLogic(modelFactory);
			redisModel = RedisAppContextAdapter.getInstance(readRdProperties(core.getCoreDescriptor()))
					.getRdCacheModel();
			rdMaxKeySize = Integer
					.valueOf(core.getCoreDescriptor().getCoreProperty("redis.catCache.maxsize", "100000"));
		} catch (Exception e) {
			getLogger().error("error when init handler", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String cacheKey = SolrParamUtils.transform(req.getParams());
		try {
			String cacheResponse = this.redisModel.get(cacheKey);
			if (cacheResponse == null) {
				//cache miss
				try {
					final Collection<AttributeCategoryMappingBean>[] atts = new Collection[1];
					catLogic.execute(req, rsp, new Callable() {
						@Override
						public void call(Object... args) {
							atts[0] = (Collection<AttributeCategoryMappingBean>) args[0];
						}
					});

					SolrQueryResponse _rsp = new SolrQueryResponse();
					super.handleRequest(req, _rsp);
					catLogic.writeRsp(req, _rsp, atts[0]);

					String result = outerString(req, _rsp);
					if (!zeroResult(_rsp) && redisModel.dbSize() < rdMaxKeySize) {
						long start = System.currentTimeMillis();
						this.redisModel.put(cacheKey, result);
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
				// cache hit
				ModifiableSolrParams solrParam = new ModifiableSolrParams(req.getParams());
				solrParam.set("vincache", true);
				SolrParams params = SolrParams.wrapAppended(solrParam, appends);
				params = SolrParams.wrapDefaults(params, defaults);
				req.setParams(params);
				rsp.add("vincache", cacheResponse);
			}
		} catch (Exception e) {
			getLogger().warn("cache fail", e);
			try {
				final Collection<AttributeCategoryMappingBean>[] atts = new Collection[1];
				catLogic.execute(req, rsp, new Callable() {
					@Override
					public void call(Object... args) {
						atts[0] = (Collection<AttributeCategoryMappingBean>) args[0];
					}
				});

				super.handleRequest(req, rsp);
				catLogic.writeRsp(req, rsp, atts[0]);
			} catch (Exception ex) {
				error(req, rsp);
				getLogger().error("", ex);
			}
		}
	}

}