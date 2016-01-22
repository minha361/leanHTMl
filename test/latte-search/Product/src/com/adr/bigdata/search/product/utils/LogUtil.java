package com.adr.bigdata.search.product.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {
	public static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
}
