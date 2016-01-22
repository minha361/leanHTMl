package com.mario.consumer.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mario.consumer.config.LifecycleHandlerConfig;
import com.mario.consumer.config.RequestHandlerConfig;
import com.mario.consumer.gateway.GatewayConfig;
import com.mario.consumer.solr.SolrClientConfig;
import com.nhb.common.Loggable;
import com.nhb.common.db.sql.SQLDataSourceConfig;
import com.nhb.common.utils.FileSystemUtils;

public final class ExtensionManager implements Loggable {

	private String extensionsFolder = System.getProperty(
			"application.extensionsFolder", "extensions");
	private Map<String, ExtensionLoader> extensionLoaderByName;
	private boolean loaded = false;

	public boolean isLoaded() {
		return this.loaded;
	}

	public void load() throws Exception {
		File file = new File(
				FileSystemUtils.createAbsolutePathFrom(extensionsFolder));
		if (file.exists() && file.isDirectory()) {
			this.extensionLoaderByName = new HashMap<String, ExtensionLoader>();
			File[] children = file.listFiles();
			for (File ext : children) {
				if (ext.isDirectory()
						&& !ext.getName().equalsIgnoreCase("__lib__")) {
					ExtensionLoader loader = new ExtensionLoader(ext);
					loader.load();
					this.extensionLoaderByName.put(loader.getName(), loader);
				}
			}
			this.loaded = true;
		}
	}

	public <T> T newInstance(String extensionName, String className) {
		if (extensionName != null && extensionName.trim().length() > 0) {
			ExtensionLoader loader = this.extensionLoaderByName
					.get(extensionName);
			if (loader != null) {
				if (className != null && className.trim().length() > 0) {
					try {
						return loader.newInstance(className.trim());
					} catch (Exception e) {
						getLogger()
								.error("cannot create new instance for class name: {}, {}",
										className, extensionName, e);
					}
				} else {
					getLogger().error("class name cannot be empty");
				}
			} else {
				getLogger().error("Extension loader cannot be found");
			}
		} else {
			getLogger().error("no extension is loaded");
		}
		return null;
	}

	public List<SQLDataSourceConfig> getDataSourceConfigs() {
		if (this.isLoaded()) {
			List<SQLDataSourceConfig> results = new ArrayList<SQLDataSourceConfig>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				results.addAll(loader.getConfigReader()
						.getSQLDataSourceConfig());
			}
			return results;
		}
		return null;
	}

	public List<LifecycleHandlerConfig> getLifecycleEntityConfigs() {
		if (this.isLoaded()) {
			List<LifecycleHandlerConfig> results = new ArrayList<LifecycleHandlerConfig>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				results.addAll(loader.getConfigReader()
						.getLifecycleHandlerConfigs());
			}
			return results;
		}
		return null;
	}

	public List<GatewayConfig> getGatewayConfigs() {
		if (this.isLoaded()) {
			List<GatewayConfig> results = new ArrayList<GatewayConfig>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				results.addAll(loader.getConfigReader().getGatewayConfigs());
			}
			return results;
		}
		return null;
	}

	public List<RequestHandlerConfig> getRequestHandlerConfigs() {
		if (this.isLoaded()) {
			List<RequestHandlerConfig> results = new ArrayList<RequestHandlerConfig>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				results.addAll(loader.getConfigReader().getRequestHandlers());
			}
			return results;
		}
		return null;
	}

	public List<SolrClientConfig> getSolrClientConfigs() {
		if (this.isLoaded()) {
			List<SolrClientConfig> results = new ArrayList<SolrClientConfig>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				results.addAll(loader.getConfigReader().getSolrClientConfigs());
			}
			return results;
		}
		return null;
	}
}
