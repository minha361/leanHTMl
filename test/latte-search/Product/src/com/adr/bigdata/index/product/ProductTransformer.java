package com.adr.bigdata.index.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;

import com.adr.bigdata.search.index.ConvertStringToString;

public class ProductTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		for (Map<String, String> field : context.getAllEntityFields()) {
			// isTrue transform
			String trueIs = field.get("trueIs");
			if (trueIs != null) {
				String columnName = field.get(DataImporter.COLUMN);
				String value = "" + row.get(columnName);
				if (trueIs.equalsIgnoreCase(value)) {
					row.put(columnName, true);
				} else {
					row.put(columnName, false);
				}
			}

			// utc transform
			String utc = field.get("utc");
			if (utc != null) {
				int timezone;
				try {
					timezone = Integer.parseInt(utc) * 3600000;
				} catch (Exception e) {
					timezone = 7 * 3600000;
				}
				String columnName = field.get(DataImporter.COLUMN);
				Object value = row.get(columnName);
				if (value != null) {
					if (value instanceof Date) {
						row.put(columnName, ((Date) value).getTime() - timezone);
					} else {
						if (value instanceof Long) {
							row.put(columnName, value);
						} else {
							throw new IllegalArgumentException("Khong hieu sao khong phai Date ma cung khong phai Long");
						}
					}
				} else {
					row.put(columnName, 0);
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

			// convert int to list<int> for is isVisible
			if ("true".equalsIgnoreCase(field.get("isVisible"))) {
				String columnName = field.get(DataImporter.COLUMN);

				Object value = row.get(columnName);
				if (value == null) {
					value = 0;
				} else {
					if (value instanceof Collection) {
						// do nothing
					} else {
						value = getListBits((Integer) value);
					}
				}
				row.put(columnName, value);
			}
		}
		return row;
	}

	private List<Integer> getListBits(int n) {
		List<Integer> result = new ArrayList<Integer>();
		int i = 0;
		int a = 1 << i;
		while (a <= n) {
			if ((n & a) == a) {
				result.add(a);
			}
			i++;
			a = 1 << i;
		}
		return result;
	}
}
