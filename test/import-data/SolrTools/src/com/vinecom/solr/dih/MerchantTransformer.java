package com.vinecom.solr.dih;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MerchantTransformer extends Transformer {
	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private final DateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.sss");

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		Logger logger = LoggerFactory.getLogger(getClass());
		for (Map<String, String> field : context.getAllEntityFields()) {
			// xmlSplitString
			String xmlSplitString = field.get("xmlSplitString");
			if (xmlSplitString != null) {
				String columnName = field.get(DataImporter.COLUMN);
				String value = (String) row.get(columnName);
				logger.debug("parse xml string with tag {}, columnName {}, value {}", xmlSplitString, columnName, value);
				if (value != null) {
					List<String> values = new ArrayList<String>();
					try {
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(new InputSource(new StringReader("<root>" + value + "</root>")));
						NodeList list = doc.getElementsByTagName(xmlSplitString);
						for (int i = 0; i < list.getLength(); i++) {
							Node node = list.item(i);
							values.add(node.getNodeValue());
						}
					} catch (ParserConfigurationException | SAXException | IOException e) {
						e.printStackTrace();
					}

					logger.debug("parsing result: {}", values);
					row.put(columnName, values);
				}
			}

			// xmlSplitDate
			String xmlSplitDate = field.get("xmlSplitDate");
			if (xmlSplitDate != null) {
				String columnName = field.get(DataImporter.COLUMN);
				String value = (String) row.get(columnName);
				logger.debug("parse xml date with tag {}, columnName {}, value {}", xmlSplitDate, columnName, value);
				if (value != null) {
					List<Date> values = new ArrayList<Date>();
					try {
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(new InputSource(new StringReader("<root>" + value + "</root>")));
						NodeList list = doc.getElementsByTagName(xmlSplitDate);
						for (int i = 0; i < list.getLength(); i++) {
							Node node = list.item(i);
							values.add(df.parse(node.getTextContent().replaceAll("T", " ")));
						}
					} catch (ParserConfigurationException | SAXException | IOException | DOMException | ParseException e) {
						e.printStackTrace();
					}

					logger.debug("date parsing result: {}", values);
					row.put(columnName, values);
				}
			}

			// convert to builtInUnicode
			if ("true".equalsIgnoreCase(field.get("builtInUnicode"))) {
				String columnName = field.get(DataImporter.COLUMN);
				String value = "" + row.get(columnName);

				value = ConvertStringToString.reviseStringFromSQL(value);
				if (value != null && !value.isEmpty()) {
					value = ConvertStringToString.decodeSumaryToNormal(value);
				}

				row.put(columnName, value);
			}
		}
		return row;
	}

}
