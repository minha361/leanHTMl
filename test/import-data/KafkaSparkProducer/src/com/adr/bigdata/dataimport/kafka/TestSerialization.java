package com.adr.bigdata.dataimport.kafka;

import org.apache.solr.common.SolrInputDocument;

public class TestSerialization {
	public static void main(String[] args) {
		SolrInputDocument ex = new SolrInputDocument();
		ex.setField("title", "title xxx");
		ex.setField("content", "content xxx");
		SolrInputDocumentEncoder encoder = new SolrInputDocumentEncoder(null);
		byte[] objBytes = encoder.toBytes(ex);
		SolrInputDocumentDecoder decoder = new SolrInputDocumentDecoder(null);
		SolrInputDocument obj = (SolrInputDocument) decoder.fromBytes(objBytes);
		System.out.println(obj.getFieldValue("title"));
		System.out.println(obj.getFieldValue("content"));
	}
}
