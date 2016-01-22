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
public class LandingPageTransformer extends Transformer {

	@Override
	public Object transformRow(Map<String, Object> row, Context arg1) {
		if (row.containsKey("LandingPageId") && row.containsKey("LandingPageGroupId")) {
			Object landingPagePriority = row.get("LandingPagePriority");
			row.put("landing_page_" + row.get("LandingPageId") + "_order", landingPagePriority);
			row.put("landing_page_group_" + row.get("LandingPageGroupId") + "_order", landingPagePriority);

			row.remove("LandingPageId");
			row.remove("LandingPageGroupId");
			row.remove("LandingPagePriority");
		}

		return row;
	}

}
