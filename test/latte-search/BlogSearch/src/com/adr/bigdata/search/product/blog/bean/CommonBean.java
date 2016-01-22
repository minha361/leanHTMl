/**
 * 
 */
package com.adr.bigdata.search.product.blog.bean;

import org.apache.solr.request.SolrQueryRequest;

import com.adr.bigdata.search.product.blog.util.BlogParams;

/**
 * @author minhvv2
 *
 */
public class CommonBean {
	private String[] catIds;
	private String cityId;
	private String districtId;
	private String highlightType;

	public static CommonBean createFromSolrRequest(SolrQueryRequest req) {
		CommonBean bean = new CommonBean();
		bean.catIds = req.getParams().getParams(BlogParams.CAT_ID);
		bean.cityId = req.getParams().get(BlogParams.CITY_ID);
		bean.districtId = req.getParams().get(BlogParams.DISTRICT_ID);
		bean.highlightType = req.getParams().get(BlogParams.HIGHLIGHT_TYPE);
		return bean;
	}

	private CommonBean() {

	}

	public String[] getCatIds() {
		return catIds;
	}

	public void setCatIds(String[] catIds) {
		this.catIds = catIds;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getHighlightType() {
		return highlightType;
	}

	public void setHighlightType(String highlightType) {
		this.highlightType = highlightType;
	}
}
