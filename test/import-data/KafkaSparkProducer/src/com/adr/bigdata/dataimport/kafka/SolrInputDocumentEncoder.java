package com.adr.bigdata.dataimport.kafka;

import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.solr.common.SolrInputDocument;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

public class SolrInputDocumentEncoder implements Encoder<SolrInputDocument> {

	public SolrInputDocumentEncoder (VerifiableProperties verifiableProperties) {
		
	}
	
	@Override
	public byte[] toBytes(SolrInputDocument obj) {
	    ByteArrayOutputStream b = new ByteArrayOutputStream();
	    ObjectOutputStream o = null;
		try {
			o = new ObjectOutputStream(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			o.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return b.toByteArray();
	}

}
