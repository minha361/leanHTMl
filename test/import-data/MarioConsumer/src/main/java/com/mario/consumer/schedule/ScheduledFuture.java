package com.mario.consumer.schedule;

public interface ScheduledFuture {

	public long getId();

	public void cancel();

	public long getRemainingTimeToNextOccurrence();

	public long getStartTime();
}
