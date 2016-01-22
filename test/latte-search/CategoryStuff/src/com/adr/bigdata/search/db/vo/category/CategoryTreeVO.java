package com.adr.bigdata.search.db.vo.category;

import java.util.Collections;
import java.util.List;

import net.minidev.json.JSONValue;

public class CategoryTreeVO {
	private int type;
	private int categoryId;
	private boolean isLeaf;
	private int categoryParentId;
	private String categoryName;
	private List<CategoryTreeVO> listCategorySub = Collections.emptyList();

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(int categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<CategoryTreeVO> getListCategorySub() {
		return listCategorySub;
	}

	public void setListCategorySub(List<CategoryTreeVO> listCategorySub) {
		this.listCategorySub = listCategorySub;
	}

	public String toJsonString() {
		return JSONValue.toJSONString(this);
	}
}
