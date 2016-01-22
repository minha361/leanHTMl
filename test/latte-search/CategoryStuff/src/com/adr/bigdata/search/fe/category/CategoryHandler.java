package com.adr.bigdata.search.fe.category;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;

import com.adr.bigdata.search.db.model.category.CategoryTreeModel;
import com.adr.bigdata.search.model.ModelFactory;
import com.google.common.base.Strings;
import com.nhb.common.Loggable;

public class CategoryHandler extends SearchHandler implements Loggable {
	private AtomicInteger waitToUpdates = new AtomicInteger(0);
	private CategoryTreeModel catTreeModel;
	private Timer time;

	@Override
	public void inform(SolrCore core) {
		core.addCloseHook(new CloseHook() {

			@Override
			public void preClose(SolrCore arg0) {
				if (time != null) {
					time.purge();
					time.cancel();
					time = null;
				}
				ModelFactory.shutdown(core.getName());
				getLogger().info("closing solr core...{}", this.getClass());
			}

			@Override
			public void postClose(SolrCore arg0) {
				getLogger().info("closed solr core...{}", this.getClass());
			}
		});

		try {
			catTreeModel = ModelFactory.getInstance(core.getName()).getModel(CategoryTreeModel.class, core);
		} catch (Exception e1) {
			System.err.println("Error when creating catTreeModel");
			e1.printStackTrace();
		}

		this.time = new Timer();
		time.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (waitToUpdates.get() > 0) {
					waitToUpdates.set(0);
					SolrQueryRequestBase req = new SolrQueryRequestBase(core, new ModifiableSolrParams()) {
					};
					try {
						runImport(req);
					} catch (Exception e) {
						getLogger().error("error when full import category", e);
					} finally {
						req.close();
					}
				}
			}
		}, 5000, 10000);
	}

	@Override
	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		rsp.setHttpCaching(false);
		SolrParams params = req.getParams();
		String cmd = params.get("cmd");
		if (Strings.isNullOrEmpty(cmd)) {
			rsp.add("status", "cmd là gì vậy???");
		} else {
			if (cmd.equalsIgnoreCase(IMPORT)) {
				this.waitToUpdates.incrementAndGet();
				rsp.add("status", "Đợi một lúc nhá");
			} else if (cmd.equalsIgnoreCase(STATUS)) {
				rsp.add("status", "Đang có " + this.waitToUpdates.get() + " thằng đứng đợi đòi update");
			}
		}
	}

	private void runImport(SolrQueryRequest req) throws Exception {
		getLogger().info("full-importing category tree");
		for (SolrInputDocument doc : catTreeModel.getAllCategoryTreeDocs()) {
			AddUpdateCommand cmd = new AddUpdateCommand(req);
			cmd.solrDoc = doc;
			req.getCore().getUpdateHandler().addDoc(cmd);
		}
	}

	public static final String IMPORT = "import";
	public static final String STATUS = "status";
}
