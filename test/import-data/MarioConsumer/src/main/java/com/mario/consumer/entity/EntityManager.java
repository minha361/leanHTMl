package com.mario.consumer.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mario.consumer.api.MarioApiFactory;
import com.mario.consumer.config.LifecycleHandlerConfig;
import com.mario.consumer.config.RequestHandlerConfig;
import com.mario.consumer.entity.handler.OnRequestHandler;
import com.mario.consumer.entity.handler.OnStartupHandler;
import com.mario.consumer.extension.ExtensionManager;
import com.mario.consumer.worker.RequestHandlerFactory;
import com.mario.consumer.worker.RequestHandlerWorkerPool;

public final class EntityManager {

	private ExtensionManager extensionManager;
	private Map<String, List<RequestHandlerWorkerPool>> gatewayToWorkerPools;

	private List<LifeCycle> lifeCycles;
	private MarioApiFactory apiFactory;

	public EntityManager(ExtensionManager extMan, MarioApiFactory apiFactory) {
		this.extensionManager = extMan;
		this.apiFactory = apiFactory;
	}

	public void start() throws Exception {
		if (this.extensionManager.isLoaded()) {

			List<LifecycleHandlerConfig> lifecycleHandlerConfigs = this.extensionManager.getLifecycleEntityConfigs();
			Map<LifeCycle, LifecycleHandlerConfig> map = new HashMap<LifeCycle, LifecycleHandlerConfig>();
			lifecycleHandlerConfigs.forEach(config -> {
				map.put(this.extensionManager.newInstance(config.getExtensionName(), config.getHandler()), config);
			});

			this.lifeCycles = new ArrayList<LifeCycle>();
			for (Entry<LifeCycle, LifecycleHandlerConfig> entry : map.entrySet()) {
				LifeCycle lifeCycle = entry.getKey();
				if (lifeCycle instanceof Pluggable) {
					((Pluggable) lifeCycle).setApi(this.apiFactory.newApi());
				}
				lifeCycle.init(entry.getValue().getInitParams());
				this.lifeCycles.add(entry.getKey());
			}

			for (LifeCycle entry : map.keySet()) {
				if (entry instanceof OnStartupHandler) {
					((OnStartupHandler) entry).onStart();
				}
			}

			List<RequestHandlerConfig> requestHandlerConfigs = this.extensionManager.getRequestHandlerConfigs();
			Map<OnRequestHandler, RequestHandlerConfig> reqHandlerToConfig = new HashMap<OnRequestHandler, RequestHandlerConfig>();
			for (RequestHandlerConfig config : requestHandlerConfigs) {
				OnRequestHandler reqHandler = this.extensionManager.newInstance(config.getExtensionName(),
						config.getHandler());
				reqHandlerToConfig.put(reqHandler, config);
			}

			this.gatewayToWorkerPools = new HashMap<String, List<RequestHandlerWorkerPool>>();
			for (Entry<OnRequestHandler, RequestHandlerConfig> entry : reqHandlerToConfig.entrySet()) {
				if (entry.getKey() instanceof Pluggable) {
					((Pluggable) entry.getKey()).setApi(this.apiFactory.newApi());
				}
				entry.getKey().init(entry.getValue().getInitParams());
				final OnRequestHandler requestHandler = entry.getKey();
				if (entry.getValue().getNumWorkers() > 0 && entry.getValue().getBindingGateways() != null
						&& entry.getValue().getBindingGateways().size() > 0) {
					RequestHandlerWorkerPool workerPool = new RequestHandlerWorkerPool(
							entry.getValue().getNumWorkers(), entry.getValue().getRingBufferSize());
					for (String gateway : entry.getValue().getBindingGateways()) {
						if (!gatewayToWorkerPools.containsKey(gateway)) {
							gatewayToWorkerPools.put(gateway, new ArrayList<RequestHandlerWorkerPool>());
						}
						gatewayToWorkerPools.get(gateway).add(workerPool);
					}
					workerPool.setHandlerFactory(new RequestHandlerFactory() {

						@Override
						public OnRequestHandler newInstance() {
							return requestHandler;
						}
					});
				}
			}
		} else {
			throw new IllegalAccessException("cannot access extension manager while it hasn't been loaded");
		}
	}

	public Map<String, List<RequestHandlerWorkerPool>> getGatewayToWorkersMap() {
		return this.gatewayToWorkerPools;
	}

	public void stop() throws Exception {
		if (this.lifeCycles != null) {
			for (LifeCycle entry : this.lifeCycles) {
				entry.destroy();
			}
		}
	}
}
