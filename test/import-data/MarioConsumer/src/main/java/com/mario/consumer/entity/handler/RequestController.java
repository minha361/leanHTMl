package com.mario.consumer.entity.handler;

import java.util.List;
import java.util.Map;

import com.mario.consumer.entity.message.Message;
import com.mario.consumer.worker.RequestHandlerWorkerPool;

public final class RequestController {

	private Map<String, List<RequestHandlerWorkerPool>> gatewayToWorkersMap;

	public void start(Map<String, List<RequestHandlerWorkerPool>> map) {
		this.gatewayToWorkersMap = map;
		for (List<RequestHandlerWorkerPool> list : this.gatewayToWorkersMap.values()) {
			for (RequestHandlerWorkerPool workerPool : list) {
				workerPool.start();
			}
		}
	}

	public void stop() {
		for (List<RequestHandlerWorkerPool> list : this.gatewayToWorkersMap.values()) {
			for (RequestHandlerWorkerPool workerPool : list) {
				workerPool.stop();
			}
		}
	}

	public void handleRequest(String gateway, Message msg) {
		if (this.gatewayToWorkersMap.containsKey(gateway)) {
			List<RequestHandlerWorkerPool> list = this.gatewayToWorkersMap.get(gateway);
			for (RequestHandlerWorkerPool workerPool : list) {
				workerPool.publish(msg);
			}
		}
	}
}
