package com.mario.consumer.extension;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mario.consumer.config.LifecycleHandlerConfig;
import com.mario.consumer.config.RequestHandlerConfig;
import com.mario.consumer.config.ScheduleHandlerConfig;
import com.mario.consumer.config.XmlConfigReader;
import com.mario.consumer.gateway.GatewayConfig;
import com.mario.consumer.gateway.GatewayType;
import com.mario.consumer.solr.SolrClientConfig;
import com.nhb.common.data.PuObject;
import com.nhb.common.db.sql.SQLDataSourceConfig;

class ExtensionConfigReader extends XmlConfigReader {

	private String extensionName;

	@Override
	protected void read(Document document) throws Exception {
		this.extensionName = ((Node) xPath.compile("/mario/name").evaluate(
				document, XPathConstants.NODE)).getTextContent();
		if (extensionName == null || extensionName.trim().length() == 0) {
			throw new RuntimeException("extension cannot be empty");
		}
		try {
			this.readGatewayConfigs((Node) xPath.compile("/mario/gateways")
					.evaluate(document, XPathConstants.NODE));
		} catch (Exception ex) {
			getLogger().warn(
					"no gateways config has found in extension.xml of extension named: '"
							+ this.extensionName + "'");
		}
		this.readLifecycleHandlerConfigs((Node) xPath
				.compile("/mario/handlers").evaluate(document,
						XPathConstants.NODE));
		try {
			this.readDataSourceConfig((Node) xPath
					.compile("/mario/datasources").evaluate(document,
							XPathConstants.NODE));
		} catch (Exception ex) {
			getLogger().warn(
					"no datasource config has found in extension.xml of extension named: '"
							+ this.extensionName + "'");
		}

		try {
			this.readSolrConfigs((Node) xPath.compile("/mario/solrs").evaluate(
					document, XPathConstants.NODE));
		} catch (Exception ex) {
			getLogger().warn(
					"no solr client configurated for extension named: '"
							+ this.extensionName + "'");
		}
	}

	private List<RequestHandlerConfig> requestHandlers;
	private List<ScheduleHandlerConfig> scheduleHandlers;
	private List<LifecycleHandlerConfig> lifeCycleHandlers;
	private List<GatewayConfig> gatewayConfigs;
	private List<SolrClientConfig> solrClientConfigs;
	private List<SQLDataSourceConfig> sqlDatasourceConfigs;

	private void readGatewayConfigs(Node node) throws XPathExpressionException {
		this.gatewayConfigs = new ArrayList<GatewayConfig>();
		NodeList list = (NodeList) xPath.compile("*").evaluate(node,
				XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			GatewayType type = GatewayType.fromName(item.getNodeName());
			if (type != null) {
				GatewayConfig config = new GatewayConfig();
				config.setType(type);
				config.setExtensionName(this.extensionName);
				try {
					config.setDeserializerClassName(((Node) xPath.compile(
							"deserializer").evaluate(item, XPathConstants.NODE))
							.getTextContent());
				} catch (NullPointerException nullPointerEx) {
					// do nothing
				} catch (Exception ex) {
					getLogger().warn(
							"deserializer doesn't indicated, ignore...", ex);
				}
				config.setName(((Node) xPath.compile("name").evaluate(item,
						XPathConstants.NODE)).getTextContent());
				Node variables = (Node) xPath.compile("variables").evaluate(
						item, XPathConstants.NODE);
				config.setInitParams(PuObject.fromXml(variables));
				gatewayConfigs.add(config);
			} else {
				getLogger().warn("gateway type not found: {}",
						item.getNodeName());
			}
		}
	}

	private void readSolrConfigs(Node node) throws XPathExpressionException {
		this.solrClientConfigs = new ArrayList<SolrClientConfig>();
		NodeList list = (NodeList) xPath.compile("*").evaluate(node,
				XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			SolrClientConfig config = new SolrClientConfig();
			config.setExtensionName(this.extensionName);
			config.setName(((Node) xPath.compile("name").evaluate(item,
					XPathConstants.NODE)).getTextContent());
			config.setSolrUrl(((Node) xPath.compile("url").evaluate(item,
					XPathConstants.NODE)).getTextContent());
			try {
				config.setQueueSize(Integer.valueOf(((Node) xPath.compile(
						"queuesize").evaluate(item, XPathConstants.NODE))
						.getTextContent()));
			} catch (Exception ex) {
				getLogger().warn(
						"solr client's queue size doesn't configurated");
			}
			try {
				config.setQueueSize(Integer.valueOf(((Node) xPath.compile(
						"numthread").evaluate(item, XPathConstants.NODE))
						.getTextContent()));
			} catch (Exception ex) {
				getLogger().warn(
						"solr client's num thread doesn't configurated");
			}

			this.solrClientConfigs.add(config);
		}
	}

	private void readDataSourceConfig(Node node)
			throws XPathExpressionException {
		this.sqlDatasourceConfigs = new ArrayList<SQLDataSourceConfig>();
		NodeList list = (NodeList) xPath.compile("*").evaluate(node,
				XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			if (item.getNodeName().equalsIgnoreCase("sql")) {
				SQLDataSourceConfig config = new SQLDataSourceConfig();
				config.setName(((Node) xPath.compile("name").evaluate(item,
						XPathConstants.NODE)).getTextContent());
				Node variables = (Node) xPath.compile("variables").evaluate(
						item, XPathConstants.NODE);
				config.setInitParams(PuObject.fromXml(variables));
				this.sqlDatasourceConfigs.add(config);
			} else {
				getLogger().warn(
						"datasource type is not supported: "
								+ item.getNodeName());
			}
		}
	}

	private void readLifecycleHandlerConfigs(Node node)
			throws XPathExpressionException {
		// read startup config
		this.lifeCycleHandlers = new ArrayList<LifecycleHandlerConfig>();
		NodeList list = (NodeList) xPath.compile("lifecycle/entry").evaluate(
				node, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			String handlerClass = ((Node) xPath.compile("handler").evaluate(
					item, XPathConstants.NODE)).getTextContent();
			LifecycleHandlerConfig config = new LifecycleHandlerConfig(
					handlerClass);
			Node variables = (Node) xPath.compile("variables").evaluate(item,
					XPathConstants.NODE);
			config.setInitParams(PuObject.fromXml(variables));
			config.setExtensionName(this.extensionName);
			this.lifeCycleHandlers.add(config);
		}

		this.requestHandlers = new ArrayList<RequestHandlerConfig>();
		list = (NodeList) xPath.compile("request/entry").evaluate(node,
				XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			Node handler = (Node) xPath.compile("handler").evaluate(item,
					XPathConstants.NODE);
			String handlerClass = handler.getTextContent();
			RequestHandlerConfig config = new RequestHandlerConfig(handlerClass);
			config.setNumWorkers(Integer.valueOf(((Node) xPath
					.compile("worker").evaluate(item, XPathConstants.NODE))
					.getTextContent()));
			try {
				config.setRingBufferSize(Integer.valueOf(((Node) xPath.compile(
						"ringbuffersize").evaluate(item, XPathConstants.NODE))
						.getTextContent()));
			} catch (Exception ex) {
				// do nothing
			}

			NodeList gatewayNodes = ((NodeList) xPath.compile("gateways/entry")
					.evaluate(item, XPathConstants.NODESET));
			if (gatewayNodes != null && gatewayNodes.getLength() > 0) {
				List<String> gateways = new ArrayList<String>();
				for (int j = 0; j < gatewayNodes.getLength(); j++) {
					String gateway = gatewayNodes.item(j).getTextContent();
					if (gateway.trim().length() > 0) {
						gateways.add(gateway);
					}
				}
				config.setBindingGateways(gateways);
			}

			config.setInitParams(PuObject.fromXml((Node) xPath.compile(
					"variables").evaluate(item, XPathConstants.NODE)));
			config.setExtensionName(this.extensionName);
			this.requestHandlers.add(config);
		}

		this.scheduleHandlers = new ArrayList<ScheduleHandlerConfig>();
		list = (NodeList) xPath.compile("schedule/entry").evaluate(node,
				XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Node item = list.item(i);
			ScheduleHandlerConfig config = new ScheduleHandlerConfig(
					((Node) xPath.compile("handler").evaluate(item,
							XPathConstants.NODE)).getTextContent());
			config.setPeriod(Integer.valueOf(((Node) xPath.compile("interval")
					.evaluate(item, XPathConstants.NODE)).getTextContent()));
			config.setInitParams(PuObject.fromXml((Node) xPath.compile(
					"variables").evaluate(item, XPathConstants.NODE)));
			config.setExtensionName(this.extensionName);
			this.scheduleHandlers.add(config);
		}
	}

	public List<RequestHandlerConfig> getRequestHandlers() {
		return requestHandlers;
	}

	public List<ScheduleHandlerConfig> getScheduleHandlers() {
		return scheduleHandlers;
	}

	public List<LifecycleHandlerConfig> getLifecycleHandlerConfigs() {
		return lifeCycleHandlers;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public List<GatewayConfig> getGatewayConfigs() {
		return this.gatewayConfigs;
	}

	public List<SQLDataSourceConfig> getSQLDataSourceConfig() {
		return this.sqlDatasourceConfigs;
	}

	public List<SolrClientConfig> getSolrClientConfigs() {
		return solrClientConfigs;
	}

}
