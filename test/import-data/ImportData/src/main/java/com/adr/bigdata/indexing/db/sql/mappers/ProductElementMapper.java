package com.adr.bigdata.indexing.db.sql.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.ProductElementBean;

public class ProductElementMapper implements ResultSetMapper<ProductElementBean> {

	@Override
	public ProductElementBean map(int i, ResultSet r, StatementContext arg2) throws SQLException {

		ProductElementBean bean = new ProductElementBean();
		bean.setProductItemWarehouseId(r.getInt("ProductItemWarehouseId"));
		bean.setProductId(r.getInt("ProductId"));
		bean.setProductItemId(r.getInt("ProductItemId"));
		bean.setBrandId(r.getInt("BrandId"));
		bean.setBrandname(r.getString("BrandName"));
		bean.setCatId(r.getInt("CategoryId"));
		bean.setWarehouseId(r.getInt("WarehouseId"));
		bean.setMcId(r.getInt("MerchantId"));
		bean.setMcName(r.getString("MerchantName"));
		bean.setMerchantProductItemSKU(r.getString("MerchantProductItemSKU"));
		bean.setDiscountPercent(r.getDouble("DiscountPercent"));

		Timestamp startTimeDiscount = r.getTimestamp("StartDateDiscount");
		if (startTimeDiscount == null)
			bean.setStartTimeDiscount(0);
		bean.setStartTimeDiscount(startTimeDiscount.getTime());

		Timestamp finishTimeDiscount = r.getTimestamp("FinishDateDiscount");
		if (finishTimeDiscount == null)
			bean.setFinishTimeDiscount(0);
		bean.setFinishTimeDiscount(finishTimeDiscount.getTime());

		bean.setBarcode(r.getString("Barcode"));
		bean.setCountSell(r.getInt("CountSell"));
		bean.setCountView(r.getInt("CountView"));
		bean.setOriginalPrice(r.getDouble("OriginalPrice"));
		bean.setSellPrice(r.getDouble("SellPrice"));
		bean.setProductItemName(r.getString("ProductItemName"));

		Timestamp createTime = r.getTimestamp("CreateTime");
		if (createTime == null)
			bean.setCreateTime(0);
		bean.setCreateTime(createTime.getTime());

		bean.setHot(r.getBoolean("IsHot"));
		bean.setNew(r.getBoolean("IsNew"));
		bean.setPromotion(r.getBoolean("IsPromotion"));
		bean.setQuantity(r.getInt("Quantity"));
		bean.setCatStatus(r.getInt("CategoryStatus"));
		bean.setBrandStatus(r.getInt("BrandStatus"));
		bean.setMcStatus(r.getInt("MerchantStatus"));
		bean.setWarehouseStatus(r.getInt("WarehouseStatus"));
		bean.setProductItemStatus(r.getInt("ProductItemStatus"));
		bean.setMcProductItemStatus(r.getInt("MerchantProductItemStatus"));

		return bean;
	}

}
