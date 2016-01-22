package com.adr.bigdata.dataimport.kafka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.solr.common.SolrInputDocument;

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

public class SolrInputDocumentDecoder implements Decoder<SolrInputDocument> {

	public SolrInputDocumentDecoder (VerifiableProperties verifiableProperties) {
		
	}
	
	@Override
	public SolrInputDocument fromBytes(byte[] bytes) {
	    ByteArrayInputStream b = new ByteArrayInputStream(bytes);
	    ObjectInputStream o = null;
		try {
			o = new ObjectInputStream(b);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
			return (SolrInputDocument) o.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}

}
