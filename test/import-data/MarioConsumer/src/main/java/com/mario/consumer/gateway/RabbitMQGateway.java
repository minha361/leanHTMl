package com.mario.consumer.gateway;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.mario.consumer.entity.message.BaseMessage;
import com.mario.consumer.entity.message.Message;
import com.mario.consumer.statics.Fields;
import com.nhb.common.data.PuObjectRO;
import com.nhb.eventdriven.Callable;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

class RabbitMQGateway extends AbstractGateway {

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private boolean autoReconnect = true;

	private ConnectionFactory factory;
	private Channel channel;

	private String host;
	private int port = 5672;
	private String userName;
	private String password;
	private String queueName;
	private String exchangeName = null;
	private boolean autoAck;
	private int maxRetryCount = 10;

	private class RetryMessageHandler implements WorkHandler<Message> {

		private RabbitMQGateway gateway;

		private RetryMessageHandler(RabbitMQGateway gateway) {
			this.gateway = gateway;
		}

		@Override
		public void onEvent(Message mes) throws Exception {
			mes.increaseRetryCount();
			if (mes.getRetryCount() > maxRetryCount) {
				getErrorLogger().error(
						"Stop retry message with type {} -> data: {}",
						mes.getType(), mes.getData());
				if (mes.getCallback() != null) {
					mes.getCallback().call(true);
				}
			} else {
				BaseMessage baseMessage = new BaseMessage();
				baseMessage.clone(mes);
				this.gateway.dispatchEvent("request", Fields.GATEWAY,
						getName(), Fields.MESSAGE, baseMessage);
			}
		}
	}

	private int retryMessageRingBufferSize = 1024 * 1024;
	private String threadNamePattern = "Retry Worker #%d";
	private RingBuffer<Message> retryRingBuffer;
	private WorkerPool<Message> retryWorkerPool;

	@Override
	protected void init(PuObjectRO initParams) {
		if (initParams != null) {
			if (initParams.variableExists(Fields.HOST)) {
				this.host = initParams.getString(Fields.HOST);
			}
			if (initParams.variableExists(Fields.PORT)) {
				this.port = initParams.getInteger(Fields.PORT);
			}
			if (initParams.variableExists(Fields.QUEUE_NAME)) {
				this.queueName = initParams.getString(Fields.QUEUE_NAME);
			}
			if (initParams.variableExists(Fields.AUTO_ACK)) {
				this.autoAck = initParams.getBoolean(Fields.AUTO_ACK);
			}
			if (initParams.variableExists(Fields.USERNAME)) {
				this.userName = initParams.getString(Fields.USERNAME);
			}
			if (initParams.variableExists(Fields.PASSWORD)) {
				this.password = initParams.getString(Fields.PASSWORD);
			}
			if (initParams.variableExists(Fields.AUTO_RECONNECT)) {
				this.autoReconnect = initParams
						.getBoolean(Fields.AUTO_RECONNECT);
			}
			if (initParams.variableExists(Fields.MAX_RETRY_COUNT)) {
				this.maxRetryCount = initParams
						.getInteger(Fields.MAX_RETRY_COUNT);
			}
			if (initParams.variableExists(Fields.EXCHANGE)) {
				this.exchangeName = initParams.getString(Fields.EXCHANGE);
			}
		}
		if (this.host == null || this.host.trim().length() == 0) {
			throw new RuntimeException("rabbitmq's host cannot be empty");
		}
		if (queueName == null || queueName.trim().length() == 0) {
			throw new RuntimeException("rabbitmq's queueName cannot be empty");
		}

		RingBuffer<Message> ringbuffer = RingBuffer.createMultiProducer(
				new EventFactory<Message>() {

					@Override
					public Message newInstance() {
						return new BaseMessage();
					}
				}, retryMessageRingBufferSize);

		this.retryWorkerPool = new WorkerPool<Message>(ringbuffer,
				ringbuffer.newBarrier(), new ExceptionHandler() {

					@Override
					public void handleOnStartException(Throwable arg0) {
						getLogger().error("error while start retrying message",
								arg0);
					}

					@Override
					public void handleOnShutdownException(Throwable arg0) {
						getLogger().error(
								"error while shutdown retrying message", arg0);
					}

					@Override
					public void handleEventException(Throwable arg0, long arg1,
							Object arg2) {
						getLogger().error(
								"error while handling retry message: " + arg2,
								arg0);
					}
				}, new RetryMessageHandler[] { new RetryMessageHandler(this) });
	}

	@Override
	public void start() throws Exception {

		this.retryRingBuffer = this.retryWorkerPool
				.start(new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(),
						new ThreadFactory() {

							final AtomicInteger threadNumber = new AtomicInteger(
									1);

							@Override
							public Thread newThread(Runnable r) {
								return new Thread(r, String.format(
										threadNamePattern,
										threadNumber.getAndIncrement()));
							}
						}));
		this.retryRingBuffer.addGatingSequences(this.retryWorkerPool.getWorkerSequences());

		this.factory = new ConnectionFactory();
		this.factory.setHost(this.host);
		this.factory.setPort(this.port);
		if (this.userName != null) {
			this.factory.setUsername(this.userName);
			this.factory.setPassword(this.password);
		}
		this.attemptReconnect(0l, 1000l);
	}

	private void retry(Message mes) {
		long sequence = this.retryRingBuffer.next();
		try {
			Message message = this.retryRingBuffer.get(sequence);
			message.setCallback(mes.getCallback());
			message.setData(mes.getData());
			message.setType(mes.getType());
			message.setRetryCount(mes.getRetryCount());
		} finally {
			this.retryRingBuffer.publish(sequence);
		}
	}

	private void attemptReconnect(long delay, long period) {
		if (this.isRunning.get()) {
			throw new RuntimeException(new IllegalAccessException(
					"cannot reconnect while connection still alive"));
		}
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
				getLogger().error("error while put thread into sleeping mode",
						e);
				return;
			}
		}
		try {
			connect();
		} catch (IOException ex) {
			getLogger().error("Connect fail because: {}", ex.getMessage());
			if (this.autoReconnect) {
				getLogger().info(" --> attempting to reconnect");
				attemptReconnect(period, period);
			}
		}
	}

	private synchronized void connect() throws IOException {

		if (this.isRunning.get()) {
			getLogger().warn("Service already connected", new Exception());
			return;
		}

		getLogger().info(
				"Attemping to connect to server at " + this.factory.getHost());

		Connection connection = this.factory.newConnection();
		this.channel = connection.createChannel();
		if (this.exchangeName == null || this.exchangeName.trim().isEmpty()) {
			this.channel.queueDeclare(this.queueName, false, false, false, null);
			getLogger().info("Exchange khong duoc khai bao, su dung queue: {}", this.queueName);
		} else {
			this.channel.exchangeDeclare(this.exchangeName, "fanout");
			this.channel.queueDeclare(this.queueName, false, false, false, null);
			this.channel.queueBind(this.queueName, this.exchangeName, "");
			getLogger().info("exchange: {}, queue: {}", this.exchangeName, this.queueName);
		}
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(this.queueName, this.autoAck, consumer);

		getLogger().info("Successful connect...");

		this.isRunning.set(true);
		boolean hasException = false;
		while (this.isRunning.get()) {
			try {
				final QueueingConsumer.Delivery delivery = consumer
						.nextDelivery();
				byte[] data = delivery.getBody();
				long deliveryTag = delivery.getEnvelope().getDeliveryTag();
				Message mes = null;
				try {
					mes = this.getDeserializer().deserialize(data);
				} catch (Exception ex) {
					getLogger().error("deserialize data error", ex);
				}
				if (mes != null) {
					mes.setCallback(new Callable() {

						@Override
						public void call(Object... data) {
							if (data.length > 0 && (boolean) data[0]) {
								try {
									channel.basicAck(deliveryTag, false);
								} catch (IOException e) {
									getLogger().error("cannot send ack...", e);
								}
							} else {
								getLogger()
										.warn("handling message error, last error: ",
												(data != null ? data.length > 1 ? data[1]
														: null
														: null));
								if (data.length == 3
										&& data[2] instanceof Message) {
									getLogger()
											.info(" ---> add to retry ring buffer.....");
									retry((Message) data[2]);
								}
							}
						}
					});
					dispatchEvent("request", Fields.GATEWAY, getName(),
							Fields.MESSAGE, mes);
				} else {
					getLogger().warn("sending ack on deserialize error");
					channel.basicAck(deliveryTag, false);
				}
			} catch (Exception ex) {
				getLogger().error("error", ex);
				hasException = true;
				break;
			}
		}
		this.isRunning.set(false);

		if (hasException && this.autoReconnect) {
			this.attemptReconnect(1000l, 2000l);
		} else {
			this.stop();
		}
	}

	@Override
	public void stop() throws IOException {
		this.isRunning.set(false);
		if (this.channel != null && this.channel.isOpen()) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.channel = null;
		}
		if (this.retryWorkerPool.isRunning()) {
			this.retryWorkerPool.drainAndHalt();
		}
	}
}
