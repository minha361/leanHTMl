package com.mario.consumer.api;

import com.hazelcast.core.HazelcastInstance;
import com.mario.consumer.schedule.impl.SchedulerFactory;
import com.mario.consumer.solr.SolrClientManager;
import com.nhb.common.db.sql.SQLDataSourceManager;

public final class MarioApiFactory {

	private SQLDataSourceManager sqlDataSourceManager;
	private SchedulerFactory schedulerFactory;
	private HazelcastInstance hazelcast;
	private SolrClientManager solrClientManager;

	public MarioApiFactory(SQLDataSourceManager sqlDataSourceManager, SchedulerFactory schedulerFactory,
			HazelcastInstance hazelcast, SolrClientManager solrClientManager) {
		this.sqlDataSourceManager = sqlDataSourceManager;
		this.schedulerFactory = schedulerFactory;
		this.hazelcast = hazelcast;
		this.solrClientManager = solrClientManager;
	}

	public MarioApi newApi() {
		return new MarioApiImpl(this.sqlDataSourceManager, this.schedulerFactory.newSchedulerInstance(),
				this.hazelcast, this.solrClientManager);
	}
}
