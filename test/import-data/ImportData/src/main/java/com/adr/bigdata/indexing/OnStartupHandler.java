package com.adr.bigdata.indexing;

import java.util.Map;

import com.adr.bigdata.indexing.models.impl.InitModel;
import com.adr.bigdata.indexing.models.impl.ModelFactory;
import com.mario.consumer.cache.CacheWrapper;
import com.mario.consumer.entity.handler.BaseStartupHandler;
import com.mario.consumer.schedule.ScheduledCallback;
import com.nhb.common.data.PuObjectRO;

public class OnStartupHandler extends BaseStartupHandler {

	private String dataSourceName;
	private ModelFactory modelFactory;
	private long deltaTimeUpdateCache;
	
	@Override
	public void init(PuObjectRO initParams) {
		dataSourceName = initParams.getString("dataSourceName");

		try {
			deltaTimeUpdateCache = initParams.getInteger("deltaTimeUpdateCache");
		} catch (Exception e) {
			deltaTimeUpdateCache = 5 * 60 * 1000;
			getLogger().warn("deltaTimeUpdateCache is not found. Set to 5 minutes to default");
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
		this.modelFactory.getModel(InitModel.class).init(getApi());

		getApi().getScheduler().scheduleAtFixedRate(deltaTimeUpdateCache, deltaTimeUpdateCache,
				new ScheduledCallback() {
					@Override
					public void call() {
						try {
							modelFactory.getModel(InitModel.class).init(getApi());
							getLogger().info("updated cache");
						} catch (Exception e) {
							getLogger().error("initModel run error", e);
						}
					}
				});
	}
}
