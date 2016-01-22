package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.adr.bigdata.indexing.db.sql.vos.AttributeAttributeValueVO;
import com.nhb.common.data.PuObject;
import com.nhb.common.db.sql.beans.AbstractBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemBean;
import com.adr.bigdata.indexing.db.sql.models.ProductItemModel;

public class PuObjectToProductItemBeanConverter implements
		PuObjectToBeanConverterInterface {

	@Override
	public AbstractBean convert(PuObject productItem) {
		// TODO Auto-generated method stub
		ProductItemBean bean = new ProductItemBean();
		int productItemId = productItem.getInteger(ProductItemModel.PI_ID);
		int productId = productItem.getInteger(ProductItemModel.PI_PRODUCT_ID);
		int brandId = productItem.getInteger(ProductItemModel.PI_BRAND_ID);
		String brandName = productItem.getString(ProductItemModel.PI_BRAND_NAME);
		if (brandName == null)
			throw new NullPointerException();

		int categoryId = productItem.getInteger(ProductItemModel.PI_CAT_ID);
		String barcode = productItem.getString(ProductItemModel.PI_BARCODE);
		if (barcode == null)
			throw new NullPointerException();

		String productItemName = productItem.getString(ProductItemModel.PI_PRODUCT_ITEM_NAME);
		if (productItemName == null)
			throw new NullPointerException();

		int productItemStatus = productItem.getInteger(ProductItemModel.PI_PRODUCT_ITEM_STATUS);
		int freshFoodType = productItem.getInteger(ProductItemModel.PI_FRESH_FOOD_TYPE);
		double weight = productItem.getDouble(ProductItemModel.PI_WEIGHT);
		String sCreateTime = productItem.getString(ProductItemModel.PI_CREATE_TIME);
		if (sCreateTime == null)
			throw new NullPointerException();

		int brandStatus = productItem.getInteger(ProductItemModel.PI_BRAND_STATUS);
		String categoryName = productItem.getString(ProductItemModel.PI_CATEGORY_NAME);
		int categoryStatus = productItem.getInteger(ProductItemModel.PI_CATEGORY_STATUS);
		List<Long> categoryPath = (List<Long>) productItem.getLongArray(ProductItemModel.PI_CATEGORY_PATH);
		if (categoryPath == null)
			throw new NullPointerException();
		int productItemType = productItem.getInteger(ProductItemModel.PI_PRODUCT_ITEM_TYPE);
		String image = productItem.getString(ProductItemModel.PI_IMAGE);

		Long updateTime = productItem.getLong(ProductItemModel.PI_UPDATE_TIME);
		if (updateTime == null) {
			getLogger().error("updateTime of ProductItem: " + productId + " is missing");
			return null;
		}

		/* Get Attributes */
		Collection<PuObject> puAtts = productItem.getPuObjectArray(ProductItemModel.PI_SOLR_FE_PRODUCT_ATTRIBUTE);
		List<AttributeAttributeValueVO> atts = new ArrayList<AttributeAttributeValueVO>();
		if (puAtts == null) {
			getLogger().debug("atts is null");
		} else {
			for (PuObject puAtt : puAtts) {
				AttributeAttributeValueVO vo = new AttributeAttributeValueVO();
				vo.setAttributeId(puAtt.getInteger(ProductItemModel.PI_ATT_ID));
				vo.setAttributeName(puAtt.getString(ProductItemModel.PI_ATT_NAME));
				if (vo.getAttributeName() == null)
					throw new NullPointerException();
				vo.setAttributeStatus(puAtt.getInteger(ProductItemModel.PI_ATT_STATUS));
				if (vo.getAttributeStatus() == null)
					throw new NullPointerException();
				vo.setAttributeValue(puAtt.getString(ProductItemModel.PI_ATT_VALUE));
				if (vo.getAttributeValue() == null)
					throw new NullPointerException();
				vo.setAttributeValueId(puAtt.getInteger(ProductItemModel.PI_ATT_VALUE_ID));
				if (vo.getAttributeValueId() == null)
					throw new NullPointerException();
				atts.add(vo);
			}
		}
		/* End: Get Attributes */

		/* Get warehouses */
		Collection<PuObject> puWarehouses = productItem.getPuObjectArray(ProductItemModel.PI_WH_PI_MAPPING);
		if (puWarehouses == null) {
			getLogger().error("warehouseProductItemMappings of " + productItemId + " is null");
			return null;
		}

		return bean;
	}

}
