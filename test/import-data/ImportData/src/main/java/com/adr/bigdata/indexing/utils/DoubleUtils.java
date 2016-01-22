/**
 * 
 */
package com.adr.bigdata.indexing.utils;

import java.text.DecimalFormat;

/**
 * @author ndn
 *
 */
public class DoubleUtils {
	private static DecimalFormat formater = new DecimalFormat("0.###");

	public static String formatDouble(Double val) {
		return formater.format(val);
	}
}