package com.adr.bigdata.search.product.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class DictionaryUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7412333373195285257L;

	private int level = -1;// root is -1
	private String nodeValue;
	private String keyWord = null;

	private HashMap<String, DictionaryUtil> childs = new HashMap<String, DictionaryUtil>();

	public DictionaryUtil() {

	}

	public void add(String keyWord) {
		String[] words = keyWord.split(" ");
		if (words.length == 0) {
			return;
		}
		addChild(0, words, keyWord);
	}

	private void addChild(int level, String[] words, String keyWord) {
		if (level < words.length) {
			String childNodeValue = words[level];
			// System.out.println(words[level]);
			DictionaryUtil childNode = childs.get(childNodeValue);
			if (childNode == null) {
				childNode = new DictionaryUtil();
				childs.put(childNodeValue, childNode);
				childNode.nodeValue = childNodeValue;
				childNode.level = level;
			}
			if (level + 1 == words.length) {
				childNode.keyWord = keyWord;
			}
			childNode.addChild((level + 1), words, keyWord);
		} else {

		}
	}

	public void checkKeywordFromLog(List<String> keyWords, String[] words) {
		if (this.keyWord != null) {
			keyWords.add(this.keyWord);
		}
		if (level + 1 < words.length) {
			String childNodeValue = words[level + 1];
			DictionaryUtil childNode = childs.get(childNodeValue);
			if (childNode != null) {
				childNode.checkKeywordFromLog(keyWords, words);
			}
		}
	}

	@Override
	public int hashCode() {
		return nodeValue.hashCode();
	}

}
