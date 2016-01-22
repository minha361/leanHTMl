package com.adr.bigdata.fullimport.solr.model;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.fullimport.SolrFields;
import com.adr.bigdata.fullimport.solr.vo.SingleMap;
import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.CommissionFeeForInitBean;
import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;

public class UpdateSolrCommissionModel extends AbstractSolrModel {
	public boolean add(CommissionFeeForInitBean wpmBean, CommisionFeeBean commsion,
			NotApplyCommisionFeeBean notApplyCommissionBean) throws SolrServerException, IOException {

		SolrInputDocument doc = new SolrInputDocument();

		double commissionFee = commsion == null ? 0 : commsion.getValue();
		boolean apply = notApplyCommissionBean == null ? false : notApplyCommissionBean.isNotApplyCommissionFee();

		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, wpmBean.getId());
		doc.addField(SolrFields.IS_NOT_APPLY_COMMISION, new SingleMap(apply));
		doc.addField(SolrFields.COMMISION_FEE, new SingleMap(commissionFee));

		getSolrClient().add(doc);
		return true;
	}
}
