package com.adr.bigdata.indexingrd.logic;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

import com.adr.bigdata.indexingrd.models.AbstractModel;
import com.adr.bigdata.indexingrd.models.impl.ModelFactory;
import com.nhb.common.Loggable;

public abstract class BaseLogicProcessor implements LogicProcessor, Loggable {
	public static final int ONSITE_STATUS = 511;	

	private ModelFactory modelFactory;
	private ConcurrentUpdateSolrClient solrClient;
	private boolean commit;
	private boolean waitFlush;
	private boolean waitSearcher;
	private boolean softCommit;
	private int timeZoneGap;
	private String categoryCoreImport;

	protected <T extends AbstractModel> T getModel(Class<T> clazz) {
		assert clazz != null;
		T model = this.modelFactory.getModel(clazz);
		//TODO add api here
		return model;
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public void setModelFactory(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	public ConcurrentUpdateSolrClient getSolrClient() {
		return solrClient;
	}

	public void setSolrClient(ConcurrentUpdateSolrClient solrClient) {
		this.solrClient = solrClient;
	}

	public boolean isCommit() {
		return commit;
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

	public boolean isWaitFlush() {
		return waitFlush;
	}

	public void setWaitFlush(boolean waitFlush) {
		this.waitFlush = waitFlush;
	}

	public boolean isWaitSearcher() {
		return waitSearcher;
	}

	public void setWaitSearcher(boolean waitSearcher) {
		this.waitSearcher = waitSearcher;
	}

	public boolean isSoftCommit() {
		return softCommit;
	}

	public void setSoftCommit(boolean softCommit) {
		this.softCommit = softCommit;
	}

	public void setTimeZoneGap(int timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

	public int getTimeZoneGap() {
		return timeZoneGap;
	}

	public String getCategoryCoreImport() {
		return categoryCoreImport;
	}

	public void setCategoryCoreImport(String categoryCoreImport) {
		this.categoryCoreImport = categoryCoreImport;
	}
}
