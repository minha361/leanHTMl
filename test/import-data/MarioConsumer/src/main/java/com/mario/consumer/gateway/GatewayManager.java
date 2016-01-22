package com.mario.consumer.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mario.consumer.entity.handler.RequestController;
import com.mario.consumer.entity.message.Message;
import com.mario.consumer.entity.message.MessageDeserializer;
import com.mario.consumer.extension.ExtensionManager;
import com.mario.consumer.statics.Fields;
import com.nhb.common.Loggable;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEvent;

public final class GatewayManager implements Loggable {

	private static class GatewayFactory {
		public Gateway newGateway(GatewayType type) {
			if (type != null) {
				Gateway result = null;
				switch (type) {
				case HTTP:
					result = new HttpGateway();
					break;
				case RABBITMQ:
					result = new RabbitMQGateway();
					break;
				}
				return result;
			}
			return null;
		}
	}

	private final GatewayFactory gatewayFactory = new GatewayFactory();
	private final Map<String, Gateway> gatewayByName = new HashMap<String, Gateway>();
	private ExecutorService gatewayThreadPool;
	private ExtensionManager extensionManager;
	private RequestController requestController;

	public GatewayManager(ExtensionManager extMan, RequestController requestController) {
		this.extensionManager = extMan;
		this.requestController = requestController;
	}

	public void start() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<GatewayConfig> gatewayConfigs = this.extensionManager.getGatewayConfigs();

		if (gatewayConfigs == null) {
			getLogger().warn("no gateway config found");
			return;
		}

		for (GatewayConfig config : gatewayConfigs) {
			if (config.getName() == null || config.getName().trim().length() == 0) {
				getLogger().error("cannot init gateway with empty name", new Exception());
				continue;
			}
			getLogger().debug("create gateway " + config);
			Gateway gateway = this.gatewayFactory.newGateway(config.getType());
			if (gateway instanceof AbstractGateway) {
				((AbstractGateway) gateway).setExtensionName(config.getExtensionName());
				if (config.getDeserializerClassName() != null) {
					MessageDeserializer deserializer = (MessageDeserializer) Class.forName(
							config.getDeserializerClassName().trim()).newInstance();
					((AbstractGateway) gateway).setDeserializer(deserializer);
				}
			}
			gateway.init(config.getName(), config.getInitParams());
			this.gatewayByName.put(config.getName(), gateway);
		}

		this.gatewayThreadPool = Executors.newCachedThreadPool();
		// parallel starting all the gateways
		this.gatewayByName.values().forEach(gateway -> {

			gateway.addEventListener("request", new EventHandler() {

				@Override
				public void onEvent(Event event) throws Exception {
					BaseEvent baseEvent = (BaseEvent) event;
					requestController.handleRequest((String) baseEvent.get(Fields.GATEWAY),
							(Message) baseEvent.get(Fields.MESSAGE));
				}
			});

			this.gatewayThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					try {
						gateway.start();
					} catch (Exception e) {
						getLogger().error("cannot start gateway", e);
					}
				}
			});
		});
	}

	public void stop() {
		for (Gateway gateway : this.gatewayByName.values()) {
			try {
				gateway.stop();
			} catch (Exception e) {
				getLogger().error("cannot stop gateway: " + gateway.getName(), e);
				System.err.println("cannot stop gateway name: " + gateway.getName());
				e.printStackTrace();
			}
		}
		List<String> gatewayNames = new ArrayList<>(this.gatewayByName.keySet());
		for (String name : gatewayNames) {
			this.gatewayByName.remove(name);
		}

		this.gatewayThreadPool.shutdown();
		try {
			if (this.gatewayThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
				System.err.println("cannot shutdown gateway threadpool, force by calling shutdownNow() method");
				this.gatewayThreadPool.shutdownNow();
				if (this.gatewayThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
					System.err.println("cannot shutdown gateway threadpool...");
				}
			}
		} catch (InterruptedException ex) {
			getLogger().error("error while waiting for gateway thread pool to shutdown", ex);
			System.err.println("error while waiting for gateway thread pool to shutdown");
			ex.printStackTrace();
		}
	}

	public Gateway getGatewayByName(String name) {
		if (name != null && name.trim().length() > 0) {
			return this.gatewayByName.get(name.trim());
		}
		return null;
	}

	public Collection<Gateway> getGateways() {
		return this.gatewayByName.values();
	}
}
