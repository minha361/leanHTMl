package com.adr.bigdata.index.product;

public class HtmlUtil {
	public static String trimHtml(String htmlString) {		
        String noHTMLString = htmlString.replaceAll("\\<.*?\\>", " "); 
        noHTMLString = noHTMLString.replaceAll("\\n", " ");   
        noHTMLString = noHTMLString.replaceAll("\\s+", " ");
        return noHTMLString;		
	}
}
