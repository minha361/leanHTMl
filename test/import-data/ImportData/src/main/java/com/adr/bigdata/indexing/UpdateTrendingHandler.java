package com.adr.bigdata.indexing;

import java.util.Map;

import com.adr.bigdata.indexing.models.impl.ModelFactory;
import com.adr.bigdata.indexing.models.impl.TrendingKeywordModel;
import com.mario.consumer.cache.CacheWrapper;
import com.mario.consumer.entity.handler.BaseStartupHandler;
import com.mario.consumer.schedule.ScheduledCallback;
import com.nhb.common.data.PuObjectRO;

public class UpdateTrendingHandler extends BaseStartupHandler {
	private String dataSourceName;
	private ModelFactory modelFactory;
	private long deltaTimeUpdateTrending;

	@Override
	public void init(PuObjectRO initParams) {
		dataSourceName = initParams.getString("dataSourceName");
		try {
			deltaTimeUpdateTrending = initParams.getInteger("deltaTimeUpdateTrending");
		} catch (Exception e) {
			deltaTimeUpdateTrending = 60 * 1000;
			getLogger().warn("deltaTimeUpdateTrending is not found. Set to 60 seconds to default");
		}
		this.modelFactory = ModelFactory.newInstance(getApi().getDatabaseAdapter(this.dataSourceName),
				new CacheWrapper() {

					@Override
					public <K, V> Map<K, V> getCacheMap(String name) {
						return getApi().getCacheMap(name);
					}
				}, 1000);
	}

	@Override
	public void onStart() throws Exception {
		getApi().getScheduler().scheduleAtFixedRate(0, deltaTimeUpdateTrending, 
				new ScheduledCallback() {
					@Override
					public void call() {
						try {
							modelFactory.getModel(TrendingKeywordModel.class).trending();
							getLogger().info("updated trending cache");
						} catch (Exception e) {
							getLogger().error("trendingModel run error", e);
						}
					}
				});
	}

}
