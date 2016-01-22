package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import com.adr.bigdata.indexing.db.sql.models.ProductItemModel;
import com.adr.bigdata.indexing.db.sql.vos.ProductItemWarehouseProductItemMappingVO;
import com.nhb.common.data.PuObject;

public class PuObjectToProductItemWarehouseProductItemMappingVOConverter {
	 public static ProductItemWarehouseProductItemMappingVO convert(PuObject puWh) {
		ProductItemWarehouseProductItemMappingVO vo = new ProductItemWarehouseProductItemMappingVO();
		vo.setWarehouseProductItemMappingId(puWh.getInteger(ProductItemModel.PI_WH_PI_MAPPING_ID));

		vo.setMerchantId(puWh.getInteger(ProductItemModel.PI_MC_ID));
		if (vo.getMerchantId() == null)
			throw new NullPointerException();
		vo.setWarehouseId(puWh.getInteger(ProductItemModel.PI_WH_ID));
		if (vo.getWarehouseId() == null)
			throw new NullPointerException();
		vo.setMerchantName(puWh.getString(ProductItemModel.PI_MC_NAME));
		if (vo.getMerchantName() == null)
			throw new NullPointerException();
		vo.setMerchantSKU(puWh.getString(ProductItemModel.PI_MC_SKU));
		if (vo.getMerchantSKU() == null)
			throw new NullPointerException();
		vo.setOriginalPrice(puWh.getDouble(ProductItemModel.PI_ORI_PRICE));
		if (vo.getOriginalPrice() == null)
			throw new NullPointerException();
		vo.setSellPrice(puWh.getDouble(ProductItemModel.PI_SELL_PRICE));
		if (vo.getSellPrice() == null)
			throw new NullPointerException();
		vo.setQuantity(puWh.getInteger(ProductItemModel.PI_QUANTITY));
		if (vo.getQuantity() == null)
			throw new NullPointerException();
		vo.setSafetyStock(puWh.getInteger(ProductItemModel.PI_SAFETY_STOCK));
		if (vo.getSafetyStock() == null)
			throw new NullPointerException();
		vo.setMerchantProductItemStatus(puWh.getInteger(ProductItemModel.PI_MC_PI_STATUS));
		if (vo.getMerchantProductItemStatus() == null)
			throw new NullPointerException();

		//
		vo.setMerchantStatus(puWh.getInteger(ProductItemModel.PI_MERCHANT_STATUS));
		if (vo.getMerchantStatus() == null)
			throw new NullPointerException();
		vo.setWarehouseStatus(puWh.getInteger(ProductItemModel.PI_WAREHOUSE_STATUS));
		if (vo.getWarehouseStatus() == null)
			throw new NullPointerException();
		vo.setProvinceId(puWh.getInteger(ProductItemModel.PI_PROVINCE_ID));
		if (vo.getProvinceId() == null)
			throw new NullPointerException();

		int isVisible = puWh.getInteger(ProductItemModel.PI_IS_VISIBLE);
		vo.setIsVisible(isVisible);
		return vo;
	}
}
