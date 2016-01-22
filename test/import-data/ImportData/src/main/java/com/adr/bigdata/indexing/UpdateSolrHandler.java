package com.adr.bigdata.indexing;

import org.jsoup.Jsoup;

import com.mario.consumer.entity.handler.BaseStartupHandler;
import com.mario.consumer.schedule.ScheduledCallback;
import com.nhb.common.data.PuObjectRO;

public class UpdateSolrHandler extends BaseStartupHandler {

	private String solrPath;
	private long deltaTimeScanDB;

	@Override
	public void init(PuObjectRO initParams) {
		solrPath = initParams.getString("solrPath");
		try {
			deltaTimeScanDB = initParams.getInteger("deltaTimeScanDB");
		} catch (Exception e) {
			deltaTimeScanDB = 60 * 60 * 1000;
			getLogger().warn("deltaTimeScanDB is not found. Set to 1 hours to default");
		}
	}

	@Override
	public void onStart() throws Exception {
		getApi().getScheduler().scheduleAtFixedRate(deltaTimeScanDB, deltaTimeScanDB, new ScheduledCallback() {
			@Override
			public void call() {
				try {
					Jsoup.connect(solrPath).get();
					getLogger().info("retrying full-import: " + solrPath);
				} catch (Exception e) {
					getLogger().error("error when start full-import: " + solrPath, e);
				}
			}
		});

	}

}
