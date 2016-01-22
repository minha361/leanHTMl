package com.adr.bigdata.indexing.models;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

public class SolrModel extends CachedModel {
	private ConcurrentUpdateSolrClient solrClient;
	private int numDocPerRequest = 100000;
	private boolean commit;
	private boolean waitFlush;
	private boolean waitSearcher;
	private boolean softCommit;
	private int timeZoneGap;

	public int getTimeZoneGap() {
		return timeZoneGap;
	}

	public void setTimeZoneGap(int timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

	public boolean isCommit() {
		return commit;
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

	protected boolean isWaitFlush() {
		return waitFlush;
	}

	public void setWaitFlush(boolean waitFlush) {
		this.waitFlush = waitFlush;
	}

	protected boolean isWaitSearcher() {
		return waitSearcher;
	}

	public void setWaitSearcher(boolean waitSearcher) {
		this.waitSearcher = waitSearcher;
	}

	protected boolean isSoftCommit() {
		return softCommit;
	}

	public void setSoftCommit(boolean softCommit) {
		this.softCommit = softCommit;
	}

	public int getNumDocPerRequest() {
		return numDocPerRequest;
	}

	public void setNumDocPerRequest(int numDocPerRequest) {
		this.numDocPerRequest = numDocPerRequest;
	}

	public ConcurrentUpdateSolrClient getSolrClient() {
		return solrClient;
	}

	public void setSolrClient(ConcurrentUpdateSolrClient solrClient) {
		this.solrClient = solrClient;
	}
}
