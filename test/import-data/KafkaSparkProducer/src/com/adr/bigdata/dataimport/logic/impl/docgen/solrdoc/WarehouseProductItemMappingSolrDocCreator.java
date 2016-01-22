package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.utils.StatusesTool;

public class WarehouseProductItemMappingSolrDocCreator {
	
	public SolrInputDocument createSolrDocument(int whpimId, Integer whId, Integer piId, Integer mcId,
			Integer quantity, String mcSKU, Double sellPrice, Double oriPrice, Integer mcPIStatus, Integer safetyStock, Integer isVisible) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, whpimId);

		if (whId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", whId);
			doc.addField(SolrFields.WAREHOUSE_ID, fs);
		}

		if (piId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", piId);
			doc.addField(SolrFields.PRODUCT_ITEM_ID, fs);
		}

		if (mcId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", mcId);
			doc.addField(SolrFields.MERCHANT_ID, fs);
		}

		if (quantity != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", quantity);
			doc.addField(SolrFields.QUANTITY, fs);
		}

		if (mcSKU != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", mcSKU);
			doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_SKU, fs);
		}

		if (sellPrice != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", sellPrice);
			doc.addField(SolrFields.SELL_PRICE, fs);
		}

/*		if (oriPrice != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", oriPrice);
			doc.addField(SolrFields.ORIGINAL_PRICE, fs);
		}*/

		if (mcPIStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", mcPIStatus);
			doc.addField("merchant_product_item_status", fs);
		}

/*		if (safetyStock != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", safetyStock);
			doc.addField(SolrFields.SAFETY_STOCK, fs);
		}*/
		
		if (isVisible != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", StatusesTool.getListBits(isVisible));
			doc.addField(SolrFields.VISIBLE, fs);
			Map<String, Object> os = new HashMap<String, Object>();
			os.put("set", isVisible);
			doc.addField("on_site", os);
		}


//		if (safetyStock != null) {
//			doc.addField(SolrFields.SAFETY_STOCK, new SingleMap("set", safetyStock));
//		}

/*		if (isVisible != null) {
			doc.addField(SolrFields.VISIBLE, new SingleMap("set", StatusesTool.getListBits(isVisible)));
			doc.addField(SolrFields.ON_SITE, new SingleMap("set", isVisible));
		}*/
		
		
		return doc;
	}

}
