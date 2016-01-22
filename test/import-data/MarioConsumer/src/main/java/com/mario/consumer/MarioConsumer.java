package com.mario.consumer;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.mario.consumer.api.MarioApiFactory;
import com.mario.consumer.entity.EntityManager;
import com.mario.consumer.entity.handler.RequestController;
import com.mario.consumer.extension.ExtensionManager;
import com.mario.consumer.gateway.GatewayManager;
import com.mario.consumer.schedule.impl.SchedulerFactory;
import com.mario.consumer.solr.SolrClientConfig;
import com.mario.consumer.solr.SolrClientManager;
import com.nhb.common.db.sql.SQLDataSourceConfig;
import com.nhb.common.db.sql.SQLDataSourceManager;
import com.nhb.common.utils.FileSystemUtils;
import com.nhb.common.utils.Initializer;

public final class MarioConsumer {

	static {
		Initializer.bootstrap(MarioConsumer.class);
	}

	public static void main(String[] args) {
		final MarioConsumer app = new MarioConsumer();
		try {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				{
					this.setName("Shutdown Thread");
					this.setPriority(MAX_PRIORITY);
				}

				@Override
				public void run() {
					app.stop();
				}
			});
			app.start();
		} catch (Exception e) {
			System.err.println("error while starting application: ");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private MarioConsumer() {

	}

	private boolean running = false;
	private ExtensionManager extensionManager;
	private GatewayManager gatewayManager;
	private EntityManager entityManager;
	private SQLDataSourceManager sqlDataSourceManager;
	private RequestController requestController;
	private MarioApiFactory apiFactory;
	private SchedulerFactory schedulerFactory;
	private HazelcastInstance hazelcast;
	private SolrClientManager solrClientManager;

	private void start() throws Exception {
		if (this.running) {
			throw new IllegalAccessException("Application is already running");
		}

		this.schedulerFactory = SchedulerFactory.getInstance();

		Config config = new XmlConfigBuilder(FileSystemUtils.createAbsolutePathFrom("conf", "hazelcast.xml")).build();
		this.hazelcast = Hazelcast.newHazelcastInstance(config);

		this.extensionManager = new ExtensionManager();
		this.extensionManager.load();

		this.solrClientManager = new SolrClientManager();
		for (SolrClientConfig solrClientConfig : this.extensionManager.getSolrClientConfigs()) {
			this.solrClientManager.register(solrClientConfig);
		}

		this.sqlDataSourceManager = new SQLDataSourceManager();
		this.apiFactory = new MarioApiFactory(this.sqlDataSourceManager, this.schedulerFactory, hazelcast,
				this.solrClientManager);

		for (SQLDataSourceConfig dataSourceConfig : this.extensionManager.getDataSourceConfigs()) {
			this.sqlDataSourceManager.registerDataSource(dataSourceConfig.getName(), dataSourceConfig.getProperties());
		}

		this.entityManager = new EntityManager(this.extensionManager, this.apiFactory);
		this.entityManager.start();

		this.requestController = new RequestController();
		this.requestController.start(this.entityManager.getGatewayToWorkersMap());

		this.gatewayManager = new GatewayManager(this.extensionManager, this.requestController);
		this.gatewayManager.start();

		this.running = true;
	}

	private void stop() {
		if (this.running) {
			this.gatewayManager.stop();
		}

		try {
			this.entityManager.stop();
		} catch (Exception e) {
			System.err.println("error while trying to stop entity manager");
			e.printStackTrace();
		}

		try {
			this.solrClientManager.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			this.requestController.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.schedulerFactory.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.hazelcast.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
