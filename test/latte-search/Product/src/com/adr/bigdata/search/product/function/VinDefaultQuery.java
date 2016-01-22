package com.adr.bigdata.search.product.function;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.LiteralValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class VinDefaultQuery extends ValueSourceParser {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ValueSource parse(FunctionQParser parser) throws SyntaxError {
		String first = parser.parseArg();
		String second = parser.parseArg();
		
		log.debug("first: {}, second: {}", first, second);

		String real = Strings.isNullOrEmpty(first) ? second : first;

		return new LiteralValueSource(real);
	}

}
