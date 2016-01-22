package com.mario.consumer.schedule.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mario.consumer.schedule.Scheduler;

public final class SchedulerFactory {

	private final int scheduledExecutorServicePoolSize = 8;
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
			scheduledExecutorServicePoolSize, new ThreadFactoryBuilder().setNameFormat("Scheduled Thread #%d").build());

	private static final SchedulerFactory instance = new SchedulerFactory();

	public static SchedulerFactory getInstance() {
		return instance;
	}

	private SchedulerFactory() {
		// do nothing
	}

	public Scheduler newSchedulerInstance() {
		return new SchedulerImpl(scheduledExecutorService);
	}

	public void stop() {
		ScheduledEventExecutor.getInstance().stop();
	}
}
