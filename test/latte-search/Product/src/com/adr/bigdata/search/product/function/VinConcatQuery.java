package com.adr.bigdata.search.product.function;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.LiteralValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class VinConcatQuery extends ValueSourceParser {
	public int max = 8;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList args) {
		Object o = args.get("max");
		if (o != null) {
			try {
				max = Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				
			}
		}
		super.init(args);
	}

	@Override
	public ValueSource parse(FunctionQParser parser) throws SyntaxError {
		String[] arr = new String[max];
		int size = 0;
		
		while (parser.hasMoreArguments() && size <= max) {
			arr[size] = parser.parseArg();
			size ++;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arr[i]);
		}
		
		return new LiteralValueSource(sb.toString());
	}

}
