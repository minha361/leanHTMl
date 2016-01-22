package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class CommisionBean extends AbstractBean {
	private static final long serialVersionUID = -2822350376599672285L;
	private int catId;
	private int wpimId;

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public int getWpimId() {
		return wpimId;
	}

	public void setWpimId(int wpimId) {
		this.wpimId = wpimId;
	}

}
