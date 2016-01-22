package com.adr.bigdata.search.handler.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.adr.bigdata.search.handler.BaseAdrSearchHandler;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.nhb.common.Loggable;

public class HandlerWorkerPool implements Loggable, ExceptionHandler {
	private int workerPoolSize = 4;
	private int ringBufferSize = 16;
	private WorkerPool<SolrParamsEvent> workerPool;
	private RingBuffer<SolrParamsEvent> ringBuffer;
	private BaseAdrSearchHandler searchHandler = null;

	public HandlerWorkerPool(int workerPoolSize, int ringBufferSize, BaseAdrSearchHandler searchHandler) {
		if (workerPoolSize <= 0 || ringBufferSize < 0 || (ringBufferSize & 1) != 0) {
			throw new IllegalArgumentException("workerPoolSize > 0, ringBufferSize > 0 and 2^n format");
		}
		if (searchHandler == null) {
			throw new IllegalArgumentException("searchHandler can not be null");
		}
		this.ringBufferSize = ringBufferSize;
		this.workerPoolSize = workerPoolSize;
		this.searchHandler = searchHandler;
	}

	public synchronized void start() {
		HandlerWorker[] workers = new HandlerWorker[workerPoolSize];
		for (int i = 0; i < workerPoolSize; i++) {
			workers[i] = new HandlerWorker(searchHandler);
		}
		RingBuffer<SolrParamsEvent> _ringBuffer = RingBuffer.createMultiProducer(new EventFactory<SolrParamsEvent>() {
			public SolrParamsEvent newInstance() {
				return new SolrParamsEvent();
			}
		}, ringBufferSize);
		this.workerPool = new WorkerPool<>(_ringBuffer, _ringBuffer.newBarrier(), this, workers);
		_ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
		this.ringBuffer = this.workerPool.start(new ThreadPoolExecutor(workerPoolSize, workerPoolSize, 10,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
	}

	public synchronized void stop() {
		if (this.workerPool.isRunning()) {
			this.workerPool.drainAndHalt();
			this.ringBuffer = null;
		}
	}

	public void publish(SolrParamsEvent evt) {
		final long sequence = this.ringBuffer.next();
		try {
			SolrParamsEvent event = this.ringBuffer.get(sequence);
			event.setCallBack(evt.getCallBack());
			event.setCore(evt.getCore());
			event.setHandler(evt.getHandler());
			event.setParams(evt.getParams());
		} finally {
			this.ringBuffer.publish(sequence);
		}
	}

	@Override
	public void handleEventException(Throwable arg0, long arg1, Object arg2) {
	}

	@Override
	public void handleOnShutdownException(Throwable arg0) {
	}

	@Override
	public void handleOnStartException(Throwable arg0) {
	}
}
