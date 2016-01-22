/**
 * 
 */
package com.adr.bigdata.index.product;

import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

/**
 * @author ndn
 *
 */
public class CollectionGroupTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {
		if (row.containsKey("CollectionId")) {
			Object collectionPriority = row.get("CollectionPriority");
			row.put("collection_group_" + row.get("CollectionId") + "_order", collectionPriority);

			row.remove("CollectionId");
			row.remove("CollectionPriority");
		}

		return row;
	}

}
