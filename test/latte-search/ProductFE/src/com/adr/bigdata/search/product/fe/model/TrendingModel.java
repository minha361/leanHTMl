package com.adr.bigdata.search.product.fe.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.adr.bigdata.search.model.CachedModel;
import com.adr.bigdata.search.product.fe.CacheFields;
import com.hazelcast.core.IMap;

public class TrendingModel extends CachedModel implements AutoCloseable {
	private Map<String, List<String>> trending;
	private Timer timer;

	public TrendingModel() {
		trending = new HashMap<>();
		timer = new Timer("treding scheduler");
		TrendingTask task = new TrendingTask(trending);
		timer.schedule(task, 0, 5000);
	}

	public List<String> getTrending(String keyword) {
		if (trending != null) {
			for (Entry<String, List<String>> e : trending.entrySet()) {
				if (keyword.matches(e.getKey())) {
					return e.getValue();
				}
			}
		}

		return null;
	}

	@Override
	public void close() throws Exception {
		timer.purge();
		timer.cancel();
	}

	class TrendingTask extends TimerTask {
		private Map<String, List<String>> trending;

		public TrendingTask(Map<String, List<String>> trending) {
			this.trending = trending;
		}

		@Override
		public void run() {
			IMap<String, List<String>> imap = null;
			try {
				imap = getCachedMap(CacheFields.TRENDING);
			} catch (Exception e) {
				getLogger().error("", e);
			}
			this.trending.clear();
			if (imap != null) {
				for (Entry<String, List<String>> e : imap.entrySet()) {
					trending.put(e.getKey(), e.getValue());
				}
			}
		}

	}
}
