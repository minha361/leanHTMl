package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import com.adr.bigdata.indexing.db.sql.vos.AttributeAttributeValueVO;
import com.nhb.common.data.PuObject;

public class PuObjectToAttributeValueVOConverter {
	public static AttributeAttributeValueVO convert(PuObject puAtt) {
		AttributeAttributeValueVO vo = new AttributeAttributeValueVO();
		vo.setAttributeId(puAtt
				.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ATT_ID));
		vo.setAttributeName(puAtt
				.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ATT_NAME));
		if (vo.getAttributeName() == null)
			throw new NullPointerException();
		vo.setAttributeStatus(puAtt
				.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ATT_STATUS));
		if (vo.getAttributeStatus() == null)
			throw new NullPointerException();
		vo.setAttributeValue(puAtt
				.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ATT_VALUE));
		if (vo.getAttributeValue() == null)
			throw new NullPointerException();
		vo.setAttributeValueId(puAtt
				.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ATT_VALUE_ID));
		if (vo.getAttributeValueId() == null)
			throw new NullPointerException();
		return vo;
	}
}
