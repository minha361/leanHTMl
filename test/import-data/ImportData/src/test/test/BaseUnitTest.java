package test;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.logic.LogicProcessor;
import com.adr.bigdata.indexing.logic.impl.LogicProcessorFactory;
import com.adr.bigdata.indexing.models.impl.ModelFactory;
import com.nhb.common.db.sql.DBIAdapter;
import com.nhb.common.db.sql.SQLDataSourceManager;
import com.nhb.common.utils.Initializer;

public class BaseUnitTest {
	static {
		Initializer.bootstrap(BaseUnitTest.class);
	}

	private static ModelFactory modelFactory = initModelFactory();
	protected static ConcurrentUpdateSolrClient solrClient = new ConcurrentUpdateSolrClient(
			"http://localhost:8983/solr/product", 1000, 8);

	private static ModelFactory initModelFactory() {
		SQLDataSourceManager datasourceManager = new SQLDataSourceManager();
		datasourceManager.registerDataSource("test", "conf/db.dev.properties", "db.sqlserver.");
		DBIAdapter dbiAdapter = new DBIAdapter(datasourceManager, "test");
		ModelFactory modelFactory = ModelFactory.newInstance(dbiAdapter, null, 100000);
		return modelFactory;
	}

	protected static BaseLogicProcessor getLogicProcessor(int type) {
		LogicProcessor logicProcessor = LogicProcessorFactory.getLogicProcessor(type);
		System.out.println(logicProcessor);
		if (logicProcessor instanceof BaseLogicProcessor) {
			BaseLogicProcessor baseLogicProcessor = (BaseLogicProcessor) logicProcessor;
			baseLogicProcessor.setSolrClient(solrClient);
			baseLogicProcessor.setModelFactory(modelFactory);
			baseLogicProcessor.setTimeZoneGap(7);
			baseLogicProcessor.setCommit(false);
			baseLogicProcessor.setWaitFlush(true);
			baseLogicProcessor.setWaitSearcher(true);
			baseLogicProcessor.setSoftCommit(true);
			return baseLogicProcessor;
		}
		return null;
	}
}
