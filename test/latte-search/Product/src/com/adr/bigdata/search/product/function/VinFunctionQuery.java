package com.adr.bigdata.search.product.function;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.ValueSourceParser;

public class VinFunctionQuery extends ValueSourceParser  {
	@Override
	public ValueSource parse(FunctionQParser fp) {
		return new VinValueSource();
	}
}
