/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.bigdata.search.product.fe;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.product.fe.logic.impl.SuggestionLogic;
import com.adr.bigdata.search.product.fe.model.RdSuggestModel;
import com.adr.bigdata.search.product.fe.redis.RedisAppContextAdapter;
import com.adr.bigdata.search.product.fe.utils.SolrParamUtils;
import com.nhb.eventdriven.Callable;

/**
 * @author noind
 *
 */
public class BaseSuggestionHandler extends VinHandler {

	private SuggestionLogic suggestionLogic = null;
	private RdSuggestModel rdModel = null;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		try {
			suggestionLogic = new SuggestionLogic(modelFactory);
			rdModel = RedisAppContextAdapter.getInstance(readRdProperties(core.getCoreDescriptor())).getSuggestModel();
			rdModel.setTimeToLive(
					Integer.valueOf(core.getCoreDescriptor().getCoreProperty("redis.suggest.ttl", "3600")));
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected boolean zeroResult(SolrQueryResponse rsp) {
		Object suggestKeyword = rsp.getValues().get("listSuggestKeyword");
		if (suggestKeyword == null) {
			return true;
		} else if (suggestKeyword instanceof NamedList) {
			if (((NamedList) suggestKeyword).size() == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		String cacheKey = SolrParamUtils.transform(req.getParams());
		try {
			String cacheResponse = this.rdModel.get(cacheKey);
			if (cacheResponse == null) {
				//cacheMiss
				try {
					final Object[] terms = new Object[1];
					suggestionLogic.execute(req, rsp, new Callable() {
						@Override
						public void call(Object... args) {
							terms[0] = args[0];
						}
					});
					SolrQueryResponse _rsp = new SolrQueryResponse();
					super.handleRequest(req, _rsp);
					suggestionLogic.writeRsp(req, _rsp, terms[0]);
					String result = outerString(req, _rsp);
					if (!zeroResult(_rsp)) {
						this.rdModel.put(cacheKey, result);
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
			getLogger().error("fail to get from redis cache.....{}", cacheEx.getMessage());
			try {
				final Object[] terms = new Object[1];

				suggestionLogic.execute(req, rsp, new Callable() {
					@Override
					public void call(Object... args) {
						terms[0] = args[0];
					}
				});
				super.handleRequest(req, rsp);
				suggestionLogic.writeRsp(req, rsp, terms[0]);
			} catch (Exception e) {
				error(req, rsp);
				getLogger().error("", e);
			}
		}
	}

}
