package com.adr.bigdata.fullimport.sql.bean;

public class CategoryBean {
	private int id;
	private String name;
	private int[] path;
	private int status;
	private String[] tree;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getPath() {
		return path;
	}

	public void setPath(int[] path) {
		this.path = path;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String[] getTree() {
		return tree;
	}

	public void setTree(String[] tree) {
		this.tree = tree;
	}

}
