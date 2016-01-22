package com.adr.extractlog.util;

import java.util.ArrayList;

public class DictinaryUtilTest {
	public static void main(String[] agrs){
		DictionaryUtil dictionaryUtil = new DictionaryUtil();
		dictionaryUtil.add("xin chao");
		dictionaryUtil.add("xin chao ban");
		dictionaryUtil.add("xin chao ba");
		dictionaryUtil.add("xin");
		dictionaryUtil.add("con ga beo");
		dictionaryUtil.add("xin chao");
		dictionaryUtil.add("xin ty");
		dictionaryUtil.add("con");
		ArrayList<String> testDic = new ArrayList<String>();
		dictionaryUtil.checkKeywordFromLog(testDic, "xin v".split(" "));
//		testDic.addAll(testDic);//diplicate
		for(String xxx : testDic){
			System.out.println(xxx);
		}
		String keyword = "";
		while (keyword.contains(" ")) {
			String[] words = keyword.split(" ");
			// dictionaryUtil.checkKeywordFromLog(strings, words);
			keyword = keyword.substring(keyword.indexOf(" ") + 1).trim();
		}
	}
}
