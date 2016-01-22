package com.adr.bigdata.search.model;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.solr.core.SolrCore;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.adr.bigdata.search.db.DBIAdapter;
import com.adr.bigdata.search.db.DBPoolDataSourceCreator;
import com.adr.bigdata.search.db.DataSourceCreator;
import com.google.common.base.Strings;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

public final class ModelFactory implements AutoCloseable {
	private static Map<String, ModelFactory> instances = new ConcurrentHashMap<String, ModelFactory>();

	public static ModelFactory getInstance(String coreName) {
		if (!instances.containsKey(coreName) || instances.get(coreName) == null) {
			ModelFactory mf = new ModelFactory();
			instances.put(coreName, mf);
		}
		return instances.get(coreName);
	}

	public static void shutdownAll() {
		for (Entry<String, ModelFactory> e : instances.entrySet()) {
			try {
				e.getValue().close();
			} catch (Exception e1) {
				System.err.println("Error when closing ModelFacory of core " + e.getKey());
				e1.printStackTrace();
			}
		}
	}

	public static void shutdown(String coreName) {
		if (instances.containsKey(coreName) && instances.get(coreName) != null) {
			try {
				instances.get(coreName).close();
			} catch (Exception e) {
				System.err.println("Error when closing ModelFacory of core " + coreName);
				e.printStackTrace();
			}
		}
	}

	private HazelcastInstance hazelcastInstance;
	private DBIAdapter dbiAdapter;
	private String dataSourceName = "adr-sqlserver";
	private String dataSourceFile = "data-sources.xml";
	private String hazelcastConfigFile = "hazelcast-client.xml";

	public <T extends Model> T getModel(Class<T> clazz, SolrCore core) throws Exception {
		try {
			T t = clazz.newInstance();
			if (t instanceof CachedModel) {
				((CachedModel) t).setHazelcast(getHazelcastInstance(core));
			} else if (t instanceof DBModel) {
				((DBModel) t).setDbiAdapter(getDBIAdapter(core));
			} else if (t instanceof DBCachedModel) {
				((DBCachedModel) t).setDbiAdapter(getDBIAdapter(core));
				((CachedModel) t).setHazelcast(getHazelcastInstance(core));
			}
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Error initialize instance of class " + clazz, e);
		}
	}

	private DBIAdapter getDBIAdapter(SolrCore core) throws Exception {
		if (dbiAdapter == null) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			XPath xPath = XPathFactory.newInstance().newXPath();
			DataSourceCreator dataSourceCreator = new DBPoolDataSourceCreator();

			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(core.getResourceLoader().openResource(dataSourceFile));
			Properties props = loadDataSource(dataSourceName, document, xPath);
			DataSource dataSource = dataSourceCreator.createDataSource(props);

			if (dataSource != null) {
				dbiAdapter = new DBIAdapter();
				dbiAdapter.setDataSourceName(dataSourceName);
				dbiAdapter.setDataSource(dataSource);
			} else {
				throw new RuntimeException("Error when creating data-source");
			}
		}
		return dbiAdapter;
	}

	private HazelcastInstance getHazelcastInstance(SolrCore core) throws Exception {
		if (hazelcastInstance == null) {
			ClientConfig config;
			try {
				config = new XmlClientConfigBuilder(core.getResourceLoader().openResource(hazelcastConfigFile)).build();
				config.setClassLoader(core.getResourceLoader().getClassLoader());
				hazelcastInstance = HazelcastClient.newHazelcastClient(config);
			} catch (IOException e) {
				throw new RuntimeException("error while trying to create hazelcast client", e);
			}
		}
		return hazelcastInstance;
	}

	private static Properties loadDataSource(String name, Document document, XPath xPath)
			throws XPathExpressionException {
		if (Strings.isNullOrEmpty(name)) {
			throw new RuntimeException("tên pool null thì chịu");
		}
		NodeList pool = (NodeList) xPath.compile("(/pools/pool[@name='" + name + "'])[1]/child::*").evaluate(document,
				XPathConstants.NODESET);
		if (pool != null && pool.getLength() > 0) {
			Properties props = new Properties();
			for (int i = 0; i < pool.getLength(); i++) {
				props.put(pool.item(i).getNodeName(), pool.item(i).getTextContent());
			}
			return props;
		}
		return null;
	}

	@Override
	public void close() throws Exception {
		if (hazelcastInstance != null) {
			try {
				hazelcastInstance.shutdown();
			} catch (Exception e) {
				System.err.println("error while trying to shutdown hazelcast client...");
				e.printStackTrace();
			}
		}
	}
}
