package com.adr.bigdata.search.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;

public class CommonUltils {

	public static List<Collection<Object>> getMultiValueField(SolrIndexSearcher searcher, SolrQueryResponse rsp,
			String field) throws IOException {
		Object response = rsp.getValues().get("response");
		if (response == null) {
			return Collections.emptyList();
		}
		if (response instanceof ResultContext) {
			return getMultiValueField((ResultContext) response, searcher, field);
		}
		if (response instanceof SolrDocumentList) {
			return getMultiValueField((SolrDocumentList) response, field);
		}
		return Collections.emptyList();
	}

	private static List<Collection<Object>> getMultiValueField(ResultContext rc, SolrIndexSearcher searcher,
			String field) throws IOException {
		DocList docs = rc.docs;
		List<Collection<Object>> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Document doc = searcher.doc(id);
			IndexableField value = doc.getField(field);
			if (value != null) {
				String[] vs = doc.getValues(value.name());
				Collection<Object> values = new ArrayList<>(vs.length);
				for (String v : vs) {
					values.add(v);
				}
				result.add(values);
			}
		}
		return result;
	}

	private static List<Collection<Object>> getMultiValueField(SolrDocumentList docs, String field) {
		List<Collection<Object>> result = new ArrayList<>();
		for (SolrDocument doc : docs) {
			Collection<Object> value = doc.getFieldValues(field);
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}

	public static List<String> getField(SolrIndexSearcher searcher, SolrQueryResponse rsp, String field)
			throws IOException {
		Object response = rsp.getValues().get("response");
		if (response == null) {
			return Collections.emptyList();
		}
		if (response instanceof ResultContext) {
			return getField((ResultContext) response, searcher, field);
		}
		if (response instanceof SolrDocumentList) {
			return getField((SolrDocumentList) response, field);
		}
		return Collections.emptyList();
	}

	private static List<String> getField(SolrDocumentList docs, String field) {
		List<String> result = new ArrayList<>();
		for (SolrDocument doc : docs) {
			Object value = doc.getFieldValue(field);
			if (value != null) {
				result.add(value.toString());
			}
		}
		return result;
	}

	private static List<String> getField(ResultContext rc, SolrIndexSearcher searcher, String field)
			throws IOException {
		DocList docs = rc.docs;
		List<String> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Document doc = searcher.doc(id);
			IndexableField value = doc.getField(field);
			if (value != null) {
				result.add(value.stringValue());
			}
		}
		return result;
	}

	private static List<Map<String, Object>> getDocsAsMap(ResultContext rc, SolrIndexSearcher searcher)
			throws IOException {
		DocList docs = rc.docs;
		List<Map<String, Object>> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Map<String, Object> map = new HashMap<>();
			Document doc = searcher.doc(id);
			for (IndexableField field : doc.getFields()) {
				Object[] os = doc.getValues(field.name());
				if (os != null) {
					if (os.length > 1) {
						map.put(field.name(), os);
					} else {
						map.put(field.name(), os[0]);
					}
				}
			}
			result.add(map);
		}
		return result;
	}

	private static List<Map<String, Object>> getDocsAsMap(SolrDocumentList docs) throws IOException {
		List<Map<String, Object>> result = new ArrayList<>();
		for (SolrDocument doc : docs) {
			result.add(doc);
		}
		return result;
	}

	public static List<Map<String, Object>> getDocsAsMap(SolrIndexSearcher searcher, SolrQueryResponse rsp)
			throws IOException {
		Object response = rsp.getValues().get("response");
		if (response == null) {
			return Collections.emptyList();
		}
		if (response instanceof ResultContext) {
			return getDocsAsMap((ResultContext) response, searcher);
		}
		if (response instanceof SolrDocumentList) {
			return getDocsAsMap((SolrDocumentList) response);
		}
		return Collections.emptyList();
	}
}
