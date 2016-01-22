package com.nhb.common.data;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class PuObjectXmlHelper {

	private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private static final XPath xPath = XPathFactory.newInstance().newXPath();
	private static final String VARIABLE = "variable";
	private static final String ENTRY = "entry";
	private static final String NAME = "name";
	private static final String TYPE = "type";

	@SuppressWarnings("unchecked")
	private static final void processXmlNode(Node node, PuObject holder) throws Exception {
		PuDataType type = PuDataType.fromName(node.getAttributes().getNamedItem(TYPE).getNodeValue());
		String name = node.getAttributes().getNamedItem(NAME).getNodeValue();
		NodeList entryList = (NodeList) xPath.compile(ENTRY).evaluate(node, XPathConstants.NODESET);
		if (type == PuDataType.PUOBJECT) {
			NodeList variableList = (NodeList) xPath.compile(VARIABLE).evaluate(node, XPathConstants.NODESET);
			if (variableList.getLength() > 0 && entryList.getLength() > 0) {
				throw new XmlParseErrorException("invalid puobject xml format");
			} else {
				PuObject childPuObject = null;
				if (variableList.getLength() > 0) {
					childPuObject = new PuObject();
					for (int i = 0; i < variableList.getLength(); i++) {
						processXmlNode(variableList.item(i), childPuObject);
					}
					holder.set(name, childPuObject, type);
				} else if (entryList.getLength() > 0) {
					Collection<PuObject> collection = new ArrayList<PuObject>();
					for (int i = 0; i < entryList.getLength(); i++) {
						childPuObject = new PuObject();
						NodeList entryContent = (NodeList) xPath.compile(VARIABLE).evaluate(entryList.item(i),
								XPathConstants.NODESET);
						for (int j = 0; j < entryContent.getLength(); j++) {
							processXmlNode(entryContent.item(j), childPuObject);
						}
						collection.add(childPuObject);
					}
					holder.set(name, collection, PuDataType.PUOBJECT_ARRAY);
				}
			}
		} else {
			if (entryList != null && entryList.getLength() > 0) {
				@SuppressWarnings("rawtypes")
				Collection collection = new ArrayList();
				Class<?> clazz = type.getDataClass();
				Constructor<?> cons = clazz.getConstructor(String.class);
				for (int i = 0; i < entryList.getLength(); i++) {
					collection.add(cons.newInstance(entryList.item(i).getTextContent().trim()));
				}
				holder.set(name, collection, PuDataType.fromName(type.getNameIgnoreArray() + PuDataType.ARRAY_SUBFIX));
			} else {
				Class<?> clazz = type.getDataClass();
				Constructor<?> cons = clazz.getConstructor(String.class);
				holder.set(name, cons.newInstance(node.getTextContent().trim()), type);
			}
		}
	}

	public static final PuObject parseXML(String xml) throws Exception {
		if (xml != null) {
			PuObject result = new PuObject();
			parseXml(xml, result);
		}
		return null;
	}

	public static final void parseXml(String xml, PuObject holder) throws Exception {
		if (xml != null && holder != null) {
			DocumentBuilder builder;
			builder = builderFactory.newDocumentBuilder();
			InputStream is = IOUtils.toInputStream(xml);
			Document document = builder.parse(is);
			NodeList content = (NodeList) xPath.compile("/" + VARIABLE + "s/" + VARIABLE).evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < content.getLength(); i++) {
				Node node = content.item(i);
				processXmlNode(node, holder);
			}
		}
	}

	/**
	 * Node name must be "Variables"
	 * 
	 * @param node
	 * @param holder
	 */
	public static final void parseXML(Node node, PuObject holder) {
		if (node != null && holder != null) {
			NodeList content;
			try {
				content = (NodeList) xPath.compile(VARIABLE).evaluate(node, XPathConstants.NODESET);
				for (int i = 0; i < content.getLength(); i++) {
					Node child = content.item(i);
					processXmlNode(child, holder);
				}
			} catch (Exception e) {
				throw new RuntimeException("parse xml error", e);
			}
		}
	}

	private static final void generateXMLContentForPuObject(PuObjectRO puObject, StringBuilder builder) {
		if (puObject != null) {
			for (Entry<String, PuDataWrapper> entry : puObject) {
				builder.append("<Variable name=\"" + entry.getKey() + "\" type=\""
						+ entry.getValue().getType().getNameIgnoreArray() + "\">");
				if (entry.getValue().getType().isArray()) {
					@SuppressWarnings("unchecked")
					Collection<Object> coll = (Collection<Object>) entry.getValue().getData();
					if (coll != null) {
						for (Object obj : coll) {
							builder.append("<Entry>");
							if (entry.getValue().getType() == PuDataType.PUOBJECT_ARRAY) {
								generateXMLContentForPuObject((PuObject) obj, builder);
							} else {
								builder.append(obj);
							}
							builder.append("</Entry>");
						}
					}
				} else {
					if (entry.getValue().getType() == PuDataType.PUOBJECT) {
						generateXMLContentForPuObject((PuObject) entry.getValue().getData(), builder);
					} else {
						builder.append(entry.getValue().getData());
					}
				}
				builder.append("</Variable>");
			}
		}
	}

	public static final String generateXMLFromPuObject(PuObjectRO puObject) {
		if (puObject != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("<Variables>");
			generateXMLContentForPuObject(puObject, builder);
			builder.append("</Variables>");
			return builder.toString();
		}
		return null;
	}
}