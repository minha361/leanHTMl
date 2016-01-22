package com.adr.bigdata.indexing.db.sql.beans;

import java.util.List;

import com.nhb.common.db.sql.beans.AbstractBean;

public class CategoryBean extends AbstractBean {

	private static final long serialVersionUID = 8223144209266060235L;

	private int id;
	private int parentId;
	private String name;

	private List<Integer> path;
	private int status;
	private boolean isLeaf;
	private long updateTime = 0;

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getPath() {
		return path;
	}

	public void setPath(List<Integer> path) {
		this.path = path;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
