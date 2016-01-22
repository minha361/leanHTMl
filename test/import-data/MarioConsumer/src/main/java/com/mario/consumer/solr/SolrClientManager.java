package com.mario.consumer.solr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public final class SolrClientManager {

	private Map<String, ConcurrentUpdateSolrClient> solrClientByName;
	private Map<String, HttpSolrClient> httpSolrClientByName;
	private Map<String, SolrClientConfig> configByname;

	public SolrClientManager() {
		this.solrClientByName = new ConcurrentHashMap<String, ConcurrentUpdateSolrClient>();
		this.httpSolrClientByName = new ConcurrentHashMap<String, HttpSolrClient>();
		this.configByname = new ConcurrentHashMap<String, SolrClientConfig>();
	}

	public ConcurrentUpdateSolrClient getSolrClient(String name) {
		if (!this.solrClientByName.containsKey(name)) {
			if (this.configByname.containsKey(name)) {
				SolrClientConfig config = this.configByname.get(name);
				this.solrClientByName.put(name,
						new ConcurrentUpdateSolrClient(config.getSolrUrl(),
								config.getQueueSize(), config.getNumThreads()));
			} else {
				return null;
			}
		}
		return this.solrClientByName.get(name);
	}

	public HttpSolrClient getSolrQueryClient(String name) {
		if (!this.httpSolrClientByName.containsKey(name)) {
			if (this.configByname.containsKey(name)) {
				this.httpSolrClientByName.put(name, new HttpSolrClient(
						this.configByname.get(name).getSolrUrl()));
			} else {
				return null;
			}
		}
		return this.httpSolrClientByName.get(name);
	}

	public void register(SolrClientConfig config) {
		this.configByname.put(config.getName(), config);
	}

	public void shutdown() {
		for (ConcurrentUpdateSolrClient sc : this.solrClientByName.values()) {
			try {
				sc.close();
			} catch (Exception e) {
				System.err.println("error while shutdown solr client");
				e.printStackTrace();
			}
		}
	}
}
