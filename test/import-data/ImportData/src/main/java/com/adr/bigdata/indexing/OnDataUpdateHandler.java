package com.adr.bigdata.indexing;

import java.util.Map;

import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.logic.LogicProcessor;
import com.adr.bigdata.indexing.logic.impl.LogicProcessorFactory;
import com.adr.bigdata.indexing.models.impl.ModelFactory;
import com.mario.consumer.cache.CacheWrapper;
import com.mario.consumer.entity.handler.BaseRequestHandler;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

public class OnDataUpdateHandler extends BaseRequestHandler {

	private int numDocPerRequest;
	private String dataSourceName;
	private ModelFactory modelFactory;
	private ConcurrentUpdateSolrClient solrClient = null;
	private boolean commit;
	private boolean waitFlush;
	private boolean waitSearcher;
	private boolean softCommit;
	private int timeZoneGap = 7;
	private String categoryCoreImport;

	@Override
	public void init(PuObjectRO initParams) {
		commit = initParams.getBoolean("commit");
		waitFlush = initParams.getBoolean("waitFlush");
		waitSearcher = initParams.getBoolean("waithSearcher");
		softCommit = initParams.getBoolean("softCommit");
		try {
			timeZoneGap = initParams.getInteger("timeZoneGap");
		} catch (Exception e) {
			getLogger().warn("", e);
		}
		dataSourceName = initParams.getString("dataSourceName");
		numDocPerRequest = initParams.getInteger("numDocPerRequest");
		this.modelFactory = ModelFactory.newInstance(this.getApi().getDatabaseAdapter(this.dataSourceName),
				new CacheWrapper() {

					@Override
					public <K, V> Map<K, V> getCacheMap(String name) {
						return getApi().getCacheMap(name);
					}
				}, this.numDocPerRequest);

		this.solrClient = this.getApi().getSolrClient(initParams.getString("solrConfigName"));

		if (this.solrClient == null) {
			throw new RuntimeException("solr client is missing");
		} else {
			this.solrClient.setRequestWriter(new BinaryRequestWriter());
		}
		
		this.categoryCoreImport = initParams.getString("categoryCoreImport");
	}

	@Override
	public Object onRequest(int type, PuObject data) throws Exception {
		LogicProcessor processor = LogicProcessorFactory.getLogicProcessor(type);
		if (processor instanceof BaseLogicProcessor) {
			((BaseLogicProcessor) processor).setModelFactory(this.modelFactory);
			((BaseLogicProcessor) processor).setSolrClient(this.solrClient);
			((BaseLogicProcessor) processor).setCommit(commit);
			((BaseLogicProcessor) processor).setWaitFlush(waitFlush);
			((BaseLogicProcessor) processor).setWaitSearcher(waitSearcher);
			((BaseLogicProcessor) processor).setSoftCommit(softCommit);
			((BaseLogicProcessor) processor).setTimeZoneGap(timeZoneGap);
			((BaseLogicProcessor) processor).setCategoryCoreImport(categoryCoreImport);
		}
		assert processor != null;
		processor.execute(data);
		return null;
	}
}
