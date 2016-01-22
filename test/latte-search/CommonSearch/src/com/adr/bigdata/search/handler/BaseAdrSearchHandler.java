package com.adr.bigdata.search.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

import com.adr.bigdata.search.handler.worker.HandlerWorkerPool;
import com.adr.bigdata.search.handler.worker.SolrParamsEvent;
import com.adr.bigdata.search.vo.Tupple2;
import com.nhb.common.Loggable;
import com.nhb.eventdriven.Callable;

/**
 * Lớp cơ sở cho tất cả các <b>Custom Handler</b> phục vụ cho các mục đích
 * nghiệp vụ tại <b>Adayroi.com</b> <br />
 * Bạn cần implement hàm <code>handleRequestBody</code>, hàm này mặc định thường
 * gọi tới hàm xử lí tại lớp cho, tuy nhiên thực tế cho thấy đôi khi gọi hàm của
 * lớp cha cho tốc độ chậm hơn so với gọi handler <b>select</b> nên bạn có thể
 * custom tại đây
 * 
 * @author noind
 *
 */
public abstract class BaseAdrSearchHandler extends SearchHandler implements Loggable {
	private int poolSize = 4;
	private int poolBufferSize = 256;
	private HandlerWorkerPool pool = null;

	protected SolrCore core;

	@Override
	public void inform(SolrCore core) {
		this.core = core;
		core.addCloseHook(new CloseHook() {
			@Override
			public void preClose(SolrCore sc) {
				if (pool != null) {
					pool.stop();
				}
			}

			@Override
			public void postClose(SolrCore sc) {
			}
		});
		super.inform(core);
	}
	
	/**
	 * Hàm này dùng để thêm các tham số defaults và mặ định cho nó tiện
	 * 
	 */
	protected SolrParams wrapDefaultsAndAppends(SolrParams params) {
		SolrParams result = params;
		if (defaults != null) {
			result = SolrParams.wrapDefaults(result, defaults);
		}
		if (appends != null) {
			result = SolrParams.wrapAppended(result, appends);
		}
		return result;
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì, Cái api này nó sẽ trả về
	 * response, thường dùng để thực hiện các request trung gian. <br />
	 * 
	 * Khi <code>coreName</code> null, thì gọi đến chính core hiện tại
	 */
	public SolrQueryResponse execute(String coreName, String handlerName, SolrParams params) {
		SolrCore otherCore;
		if (coreName == null) {
			otherCore = this.core;
		} else {
			otherCore = this.core.getCoreDescriptor().getCoreContainer().getCore(coreName);
			if (otherCore == null) {
				throw new SolrException(ErrorCode.SERVER_ERROR, "The core: " + coreName + " is not exists");
			}
			if (otherCore.isClosed()) {
				throw new SolrException(ErrorCode.SERVER_ERROR, "The core: " + coreName + " is already closed");
			}
		}
		SolrQueryRequest req = new LocalSolrQueryRequest(otherCore, params);
		SolrQueryResponse rsp = new SolrQueryResponse();

		try {
			SolrRequestHandler handler = otherCore.getRequestHandler(handlerName);
			if (handler == null) {
				throw new SolrException(ErrorCode.SERVER_ERROR, "The handler: " + handlerName + " is not exists");
			}
			handler.handleRequest(req, rsp);
		} finally {
			if (coreName != null) {
				if (otherCore != null) {
					otherCore.close();
					otherCore = null;
				}
			}
			if (req != null) {
				req.close();
				req = null;
			}
		}
		return rsp;
	}
	
	/**
	 * Gọi đến một handler bất kì của một core bất kì, Cái api này nó sẽ trả về
	 * response, thường dùng để thực hiện các request trung gian. <br />
	 * 
	 * Nhớ phải đóng <code>req</code> khi không dùng nữa <br />
	 * 
	 * Khi <code>coreName</code> null, thì gọi đến chính core hiện tại
	 */
	public Tupple2<SolrQueryResponse, SolrQueryRequest> executeReReq(String coreName, String handlerName, 
			SolrParams params) {
		SolrCore otherCore;
		if (coreName == null) {
			otherCore = this.core;
		} else {
			otherCore = this.core.getCoreDescriptor().getCoreContainer().getCore(coreName);
			if (otherCore == null) {
				throw new SolrException(ErrorCode.SERVER_ERROR, "The core: " + coreName + " is not exists");
			}
			if (otherCore.isClosed()) {
				throw new SolrException(ErrorCode.SERVER_ERROR, "The core: " + coreName + " is already closed");
			}
		}
		SolrQueryRequest req = new LocalSolrQueryRequest(otherCore, params);
		SolrQueryResponse rsp = new SolrQueryResponse();

		try {
			SolrRequestHandler handler = otherCore.getRequestHandler(handlerName);
			if (handler == null) {
				req.close();
				throw new SolrException(ErrorCode.SERVER_ERROR, "The handler: " + handlerName + " is not exists");
			}
			handler.handleRequest(req, rsp);
		} finally {
			if (coreName != null) {
				if (otherCore != null) {
					otherCore.close();
					otherCore = null;
				}
			}
		}
		return new Tupple2<SolrQueryResponse, SolrQueryRequest>(rsp, req);
	}

	/**
	 * Gọi đến một handler bất kì của một chính core hiện tại. Nhớ đóng
	 * SolrQueryRequest <code>req</code> nếu bạn tạo ra nó
	 * 
	 * @return
	 */
	public void execute(String handlerName, SolrQueryRequest req, SolrQueryResponse rsp) {
		SolrRequestHandler handler = this.core.getRequestHandler(handlerName);
		if (handler == null) {
			throw new SolrException(ErrorCode.SERVER_ERROR, "The handler: " + handlerName + " is not exists");
		}		
		handler.handleRequest(req, rsp);
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì, chạy song song với một
	 * tập tham số. <br />
	 * Lần đầu tiên gọi đến hàm này thì pool mới được tạo. <br />
	 * Khi <code>coreName</code> null, thì gọi đến chính core hiện tại
	 * 
	 * @throws InterruptedException
	 */
	public List<SolrQueryResponse> executeParallel(String coreName, String handlerName, List<SolrParams> params)
			throws InterruptedException {
		if (this.pool == null) {
			this.pool = new HandlerWorkerPool(this.poolSize, this.poolBufferSize, this);
			this.pool.start();
		}

		List<SolrQueryResponse> responses = new CopyOnWriteArrayList<>();
		CountDownLatch latch = new CountDownLatch(params.size());
		List<SolrParamsEvent> evts = new ArrayList<>(params.size());
		for (SolrParams param : params) {
			SolrParamsEvent evt = new SolrParamsEvent();
			evt.setCore(coreName);
			evt.setHandler(handlerName);
			evt.setParams(param);
			evt.setCallBack(new Callable() {
				@Override
				public void call(Object... args) {
					SolrQueryResponse result = (SolrQueryResponse) args[0];
					if (result != null) {
						responses.add(result);
					}
					latch.countDown();
				}
			});
			evts.add(evt);
		}
		for (SolrParamsEvent evt : evts) {
			this.pool.publish(evt);
		}

		if (latch.getCount() != 0) {
			latch.await();
		}

		return responses;
	}

	/**
	 * Đọc nhanh một file trong thư mục <b>conf</b> của một <b>core</b>
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	protected final List<String> getResource(SolrResourceLoader resourceLoader, String pathFile)
			throws UnsupportedEncodingException, IOException {
		if (resourceLoader == null) {
			throw new RuntimeException("The 'inform' method must be call before me");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceLoader.openResource(pathFile), "UTF-8"));
		List<String> res = new ArrayList<>();
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				res.add(line);
			}
		}

		getLogger().info("Loaded resource from {} with {} lines", pathFile, res.size());

		return res;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getPoolBufferSize() {
		return poolBufferSize;
	}

	public void setPoolBufferSize(int poolBufferSize) {
		this.poolBufferSize = poolBufferSize;
	}

}
