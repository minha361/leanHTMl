package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class KeywordBean extends AbstractBean {
	private static final long serialVersionUID = -1835972801750757949L;

	private int id;
	private String keyword;
	private String url;
	private int status = 1;
	private long updateTime;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
