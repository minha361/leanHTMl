package com.vinecom.solr.dih;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;

public class AtomicTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> field : context.getAllEntityFields()) {
			if ("true".equalsIgnoreCase(field.get("key"))) {
				continue;
			}

			String columnName = field.get(DataImporter.COLUMN);
			Object value = row.get(columnName);
			if (value instanceof SingleMap) {
				continue;
			}
			row.put(columnName, new SingleMap("set", value));
		}

		return row;
	}

}
