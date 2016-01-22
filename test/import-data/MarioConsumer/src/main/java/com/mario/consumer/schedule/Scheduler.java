package com.mario.consumer.schedule;

import java.util.Date;

public interface Scheduler {

	public ScheduledFuture schedule(long delay, ScheduledCallback callback);

	public ScheduledFuture schedule(Date date, ScheduledCallback callback);

	public ScheduledFuture scheduleAtFixedRate(long delay, long period, ScheduledCallback callback);

	public ScheduledFuture scheduleAtFixedRate(Date startDate, long period, ScheduledCallback callback);
}
