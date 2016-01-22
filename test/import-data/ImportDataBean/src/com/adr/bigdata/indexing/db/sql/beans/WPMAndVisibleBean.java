package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

public class WPMAndVisibleBean extends AbstractBean {
	private static final long serialVersionUID = -6253367948509496813L;

	private int wpmid;
	private int isVisible;

	public int getWpmid() {
		return wpmid;
	}

	public void setWpmid(int wpmid) {
		this.wpmid = wpmid;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

}
