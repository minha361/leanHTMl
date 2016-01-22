package com.mario.consumer.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.mario.consumer.entity.message.BaseMessage;
import com.mario.consumer.entity.message.Message;
import com.nhb.common.Loggable;

public class RequestHandlerWorkerPool implements ExceptionHandler, Loggable {

	private String threadNamePattern = "Indexing Worker #%d";
	private WorkerPool<Message> workerPool;
	private RingBuffer<Message> ringBuffer;
	private int workerPoolSize = 1;
	private int ringBufferSize = 1024;
	private RequestHandlerFactory handlerFactory;

	public RequestHandlerWorkerPool(int workerPoolSize, int ringBufferSize) {
		if (workerPoolSize <= 0 || ringBufferSize < 0
				|| (ringBufferSize & 1) != 0) {
			throw new IllegalArgumentException(
					"worker pool size must be > 0, ringbuffer size must be > 0 and multiples of 2");
		}
		this.ringBufferSize = ringBufferSize;
		this.workerPoolSize = workerPoolSize;
	}

	public synchronized void start() {
		if (this.handlerFactory == null) {
			throw new RuntimeException("handler factory is null");
		}
		this.start(this.handlerFactory);
	}

	public synchronized void start(RequestHandlerFactory handlerFactory) {
		RequestHandlerWorker[] workers = new RequestHandlerWorker[workerPoolSize];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new RequestHandlerWorker(handlerFactory.newInstance());
		}

		RingBuffer<Message> _ringBuffer = RingBuffer.createMultiProducer(
				new EventFactory<Message>() {

					@Override
					public Message newInstance() {
						return new BaseMessage();
					}
				}, ringBufferSize);

		this.workerPool = new WorkerPool<Message>(_ringBuffer,
				_ringBuffer.newBarrier(), this, workers);
		_ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
		this.ringBuffer = this.workerPool.start(new ThreadPoolExecutor(
				workerPoolSize, workerPoolSize, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

					final AtomicInteger threadNumber = new AtomicInteger(1);

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, String.format(threadNamePattern,
								threadNumber.getAndIncrement()));
					}
				}));
	}

	public synchronized void stop() {
		if (this.workerPool.isRunning()) {
			this.workerPool.drainAndHalt();
			this.ringBuffer = null;
		}
	}

	public void publish(Message msg) {
		final long sequence = this.ringBuffer.next();
		try {
			Message event = this.ringBuffer.get(sequence);
			event.setType(msg.getType());
			event.setData(msg.getData());
			event.setCallback(msg.getCallback());
			event.setRetryCount(msg.getRetryCount());
		} finally {
			this.ringBuffer.publish(sequence);
		}
	}

	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		getLogger().error(
				"error while handling event at sequence " + sequence
						+ " --> error: ", ex);
		if (event != null && ((Message) event).getCallback() != null) {
			((Message) event).getCallback().call(false, ex, event);
		}
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		getLogger().error("error while shutting down event: ", ex);
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		getLogger().error("error while starting handle event: ", ex);
	}

	public void setHandlerFactory(RequestHandlerFactory handlerFactory) {
		this.handlerFactory = handlerFactory;
	}
}
