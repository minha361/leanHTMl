package com.adr.extractlog;

import java.util.HashSet;
import java.util.Set;

import com.adr.extractlog.bean.LogBean;
import com.google.gson.Gson;

public class KeywordProduct {
	private String keyWord;
	private long productItemId;
	private int searchAndClickCount;
	
	private String productName;

	private Set<String> brandNames = new HashSet<String>();
	private Set<String> categoryNames = new HashSet<String>();
	private Set<String> categoryPaths = new HashSet<String>();
	private Set<Long> categoryIds = new HashSet<Long>();

	private static final String ADR_SEARCH = "adrSearch%sadrPIID%d";

	private String hashString;

	public KeywordProduct(String keyWord, long productItemId, String productName) {
		this.keyWord = keyWord;
		this.productItemId = productItemId;
		this.productName = productName;
		hashString = String.format(ADR_SEARCH, keyWord, productItemId);
	}

	public void addOne(LogBean logBean) {
		searchAndClickCount++;
		brandNames.add(logBean.getBrandName());
		categoryNames.add(logBean.getCategoryName());
		categoryPaths.add(logBean.getCategoryPath());
		categoryIds.add(logBean.getCategoryId());
	}

	// public void add(int count) {
	// searchAndClickCount += count;
	// }

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof KeywordProduct) {
			if (keyWord.equals(((KeywordProduct) object).keyWord) && (productItemId == (((KeywordProduct) object).productItemId))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashString.hashCode();
	}

	public String getKeyWord() {
		return keyWord;
	}

	public long getProductItemId() {
		return productItemId;
	}

	public int getSearchAndClickCount() {
		return searchAndClickCount;
	}

	public String getBrandsJson() {
		Gson g = new Gson();
//		Person person = g.fromJson("{\"name\": \"John\"}", Person.class);
//		System.out.println(person.name); //John
//		System.out.println(g.toJson(person)); // {"name":"John"}
		return g.toJson(brandNames);
	}

	public String getCategoryNamesJson() {
		Gson g = new Gson();
		return g.toJson(categoryNames);
	}

	public String getCategoryIdsJson() {
		Gson g = new Gson();
		return g.toJson(categoryIds);
	}

	public String getCategoryPathsJson() {
		Gson g = new Gson();
		return g.toJson(categoryPaths);
	}

	public String getProductName() {
		return productName;
	}
	
	

}
