package com.adr.bigdata.search.product.fe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.product.fe.redis.RdAppConfig;
import com.adr.bigdata.search.product.fe.redis.RdConstant;
import com.adr.bigdata.search.wt.NoShardingMappingJsonWriter;
import com.nhb.common.Loggable;

public class VinHandler extends SearchHandler implements Loggable {
	public static final String EXTRA_PROMOTION = "extraPro";
	private NoShardingMappingJsonWriter wt = new NoShardingMappingJsonWriter();

	protected ModelFactory modelFactory;

	@Override
	public void inform(SolrCore core) {
		super.inform(core);
		core.addCloseHook(new CloseHook() {
			@Override
			public void preClose(SolrCore sc) {
				modelFactory = ModelFactory.getInstance(core.getName());
				getLogger().info("closing solr core...{}", this.getClass());
			}

			@Override
			public void postClose(SolrCore sc) {
				getLogger().info("closed solr core...{}", this.getClass());
			}
		});
		modelFactory = ModelFactory.getInstance(core.getName());
	}

	protected final List<String> getResource(SolrResourceLoader resourceLoader, String pathFile)
			throws UnsupportedEncodingException, IOException {
		if (resourceLoader == null) {
			throw new RuntimeException("The 'inform' method must be call before me");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceLoader.openResource(pathFile), "UTF-8"));
		List<String> res = new ArrayList<>();
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				res.add(line);
			}
		}

		getLogger().info("Loaded resource from {} with {} lines", pathFile, res.size());

		return res;
	}

	@SuppressWarnings("unchecked")
	protected void error(SolrQueryRequest req, SolrQueryResponse rsp) {
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.add("fq", ProductFields.PRODUCT_ITEM_ID_WAREHOUSE_ID + ":0");
		params.add("q", "*:*");
		req.setParams(params);
		super.handleRequest(req, rsp);
		rsp.getValues().remove("response");
		rsp.getValues().add("status", "404");
		rsp.getValues().add("error", "Có lỗi xảy ra, kiểm tra lại tham số hoặc liên hệ với team để tìm nguyên nhân");
	}

	protected String outerString(SolrQueryRequest req, SolrQueryResponse rsp) throws IOException {
		StringWriter sw = new StringWriter();
		try {
			this.wt.write(sw, req, rsp);
			return sw.toString();
		} finally {
			sw.close();
		}
	}

	protected boolean zeroResult(SolrQueryResponse rsp) {
		Object _maxPrice = rsp.getValues().get("maxPrice");
		if (_maxPrice == null) {
			return true;
		}
		try {
			double maxPrice = (double) _maxPrice;
			if (maxPrice >= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			getLogger().error("error checking zeroResult...{}", e.getMessage());
			return true;
		}
	}

	protected RdAppConfig readRdProperties(CoreDescriptor descriptor) {
		RdAppConfig conf = new RdAppConfig();

		int timeToLive = Integer.parseInt(descriptor.getCoreProperty("redis.ttl", "120"));
		String redisHost = descriptor.getCoreProperty("redis.host", "10.220.75.78");
		int redisPort = Integer.parseInt(descriptor.getCoreProperty("redis.port", "8983"));
		int upperWaterMark = Integer.parseInt(descriptor.getCoreProperty("redis.upperWaterMark", "1024"));
		int lowerWaterMark = Integer.parseInt(descriptor.getCoreProperty("redis.lowerWaterMark", "512"));
		int acceptableSize = Integer.parseInt(descriptor.getCoreProperty("redis.acceptableSize", "512"));
		int initialSize = Integer.parseInt(descriptor.getCoreProperty("redis.initialSize", "256"));
		Map<String, Integer> cacheSizeContainer = new HashMap<String, Integer>();
		cacheSizeContainer.put(RdConstant.RD_CAT_CACHE_SIZE,
				Integer.parseInt(descriptor.getCoreProperty(RdConstant.RD_CAT_CACHE_SIZE, "100000")));
		cacheSizeContainer.put(RdConstant.RD_SEARCH_CACHE_SIZE,
				Integer.parseInt(descriptor.getCoreProperty(RdConstant.RD_SEARCH_CACHE_SIZE, "500000")));
		cacheSizeContainer.put(RdConstant.RD_SUGG_CACHE_SIZE,
				Integer.parseInt(descriptor.getCoreProperty(RdConstant.RD_SUGG_CACHE_SIZE, "500000")));

		conf.setTimeToLive(timeToLive);
		conf.setRdHost(redisHost);
		conf.setRdPort(redisPort);
		conf.setUpperWaterMark(upperWaterMark);
		conf.setLowerWaterMark(lowerWaterMark);
		conf.setAcceptableSize(acceptableSize);
		conf.setInitialSize(initialSize);
		conf.setMaxCacheSizes(cacheSizeContainer);

		return conf;
	}
}
