package com.mario.consumer.gateway;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.mario.consumer.entity.message.Message;
import com.mario.consumer.statics.Fields;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;
import com.nhb.eventdriven.Callable;

class HttpGateway extends AbstractGateway {

	private int port = 8889;
	private Server server;

	@Override
	protected void init(PuObjectRO initParams) {
		if (initParams != null) {
			if (initParams.variableExists(Fields.PORT)) {
				this.port = initParams.getInteger(Fields.PORT);
			}
		}
		this.server = new Server(this.port);

		this.server.setHandler(new AbstractHandler() {

			@Override
			public void handle(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				if (target.endsWith("favicon.ico")) {
					return;
				}
				HttpGateway.this.handle(request.startAsync());
			}
		});
	}

	private void handle(final AsyncContext context) {
		Message msg = null;
		try {
			msg = this.getDeserializer().deserialize(context.getRequest());
		} catch (Exception e1) {
			getLogger().error("error while deserialize request", e1);
		}
		context.getResponse().setContentType("text/plain");
		context.getResponse().setCharacterEncoding("utf-8");
		if (msg != null) {
			msg.setCallback(new Callable() {

				@Override
				public void call(Object... data) {
					try {
						String result = null;
						if (data.length > 1 && data[1] != null) {
							if (data[1] instanceof Throwable) {
								result = ExceptionUtils
										.getFullStackTrace((Throwable) data[1]);
							} else {
								if (data[1] instanceof PuObject) {
									result = ((PuObject) data[1]).toJSON();
								} else {
									result = data[1].toString();
								}
							}
						}
						if (result != null) {
							try {
								context.getResponse().getWriter()
										.append(result);
								context.getResponse().flushBuffer();
							} catch (IOException e) {
								getLogger()
										.error("error while trying to sending response to client",
												e);
							}
						}
					} finally {
						context.complete();
					}
				}
			});
			this.dispatchEvent("request", Fields.GATEWAY, getName(),
					Fields.MESSAGE, msg);
		} else {
			try {
				context.getResponse().getWriter().append("{status: 1}");
				context.getResponse().flushBuffer();
			} catch (IOException e) {
				getLogger().error("cannot write response", e);
			} finally {
				context.complete();
			}
		}
	}

	@Override
	public void start() throws Exception {
		getLogger().info("starting http server at: " + this.port);
		this.server.start();
	}

	@Override
	public void stop() throws Exception {
		if (this.server != null && this.server.isStarted()) {
			this.server.stop();
		}
	}
}
