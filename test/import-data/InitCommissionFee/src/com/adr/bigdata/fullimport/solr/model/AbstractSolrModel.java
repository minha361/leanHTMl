package com.adr.bigdata.fullimport.solr.model;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

public abstract class AbstractSolrModel {
	private ConcurrentUpdateSolrClient solrClient;

	public ConcurrentUpdateSolrClient getSolrClient() {
		return solrClient;
	}

	public void setSolrClient(ConcurrentUpdateSolrClient solrClient) {
		this.solrClient = solrClient;
	}

}
