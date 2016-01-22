/**
 * 
 */
package com.adr.bigdata.indexing.db.sql.beans;

import com.nhb.common.db.sql.beans.AbstractBean;

/**
 * @author ndn
 *
 */
public class CollectionGroupProductStatusBean extends AbstractBean {
	private static final long serialVersionUID = 3354974761589643553L;

	private int warehouseProductItemMappingId;
	private int status;
	private int priority;

	

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getWarehouseProductItemMappingId() {
		return warehouseProductItemMappingId;
	}

	public void setWarehouseProductItemMappingId(int warehouseProductItemMappingId) {
		this.warehouseProductItemMappingId = warehouseProductItemMappingId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
