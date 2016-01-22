package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ATT_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ATT_NAME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ATT_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ATT_VALUE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ATT_VALUE_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_BARCODE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_BRAND_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_BRAND_NAME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_BRAND_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_CATEGORY_PATH;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_CATEGORY_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_CAT_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_COMMISION_FEE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_CREATE_TIME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_FRESH_FOOD_TYPE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_IMAGE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_IS_NOT_APPLY_COMMISION;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_IS_VISIBLE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_MC_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_MC_NAME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_MC_PI_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_MC_SKU;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_MERCHANT_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_ORI_PRICE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRICE_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRODUCT_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRODUCT_ITEM_NAME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRODUCT_ITEM_POLICY;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRODUCT_ITEM_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PRODUCT_ITEM_TYPE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_PROVINCE_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_QUANTITY;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_SAFETY_STOCK;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_SELL_PRICE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_SOLR_FE_PRODUCT_ATTRIBUTE;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_UPDATE_TIME;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_VAT_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_WAREHOUSE_STATUS;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_WEIGHT;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_WH_ID;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_WH_PI_MAPPING;
import static com.adr.bigdata.indexingrd.models.impl.ProductItemModel.PI_WH_PI_MAPPING_ID;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexingrd.APIFields;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.BrandModel;
import com.adr.bigdata.indexingrd.models.impl.MerchantModel;
import com.adr.bigdata.indexingrd.models.impl.ProductItemModel;
import com.adr.bigdata.indexingrd.models.impl.WarehouseProductItemMappingModel;
import com.adr.bigdata.indexingrd.vos.AttributeValueVO;
import com.adr.bigdata.indexingrd.vos.ProductItemVO;
import com.adr.bigdata.indexingrd.vos.ProductItemWarehouseProductItemMappingVO;
import com.nhb.common.data.PuObject;

public class ProductItemUpdatedLogicProccessor extends BaseLogicProcessor {
	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private ProductItemModel productItemModel;
	private BrandModel brandModel;
	private MerchantModel merchantModel;
	private WarehouseProductItemMappingModel warehouseProductItemMappingModel;

	@Override
	public void execute(PuObject data) throws Exception {
		long start = System.currentTimeMillis();
		if (productItemModel == null) {
			productItemModel = getModel(ProductItemModel.class);
		}
		if (brandModel == null) {
			brandModel = getModel(BrandModel.class);
		}

		if (merchantModel == null) {
			merchantModel = getModel(MerchantModel.class);
		}
		
		if (warehouseProductItemMappingModel == null) {
			warehouseProductItemMappingModel = getModel(WarehouseProductItemMappingModel.class);
		}

		Collection<PuObject> list = data.getPuObjectArray(APIFields.LIST);
		PuObject element = list.iterator().next();
		if (element.getPuObjectArray(ProductItemModel.PI_WH_PI_MAPPING) != null) {
			// new
			newProductItem(list);
		} else {
			// update
			updateProductItem(list);
		}
		getProfillingLogger().debug(
				"time proccess " + list.size() + " productItem: " + (System.currentTimeMillis() - start));
	}

	private void newProductItem(Collection<PuObject> productItems) throws SolrServerException, IOException {
		List<Integer> updatedWarehouseProductItemMappingIds = new ArrayList<Integer>();
		List<Long> updatedWarehouseProductItemMappingIdTimes = new ArrayList<Long>();
		List<Integer> updatedProductItemIds = new ArrayList<Integer>();
		List<Long> updatedProductItemIdTimes = new ArrayList<Long>();

		// Get batch last update time from cache
		Set<Integer> productItemIds = new HashSet<Integer>();
		Set<Integer> brandIds = new HashSet<Integer>();
		Set<Integer> categoryIds = new HashSet<Integer>();
		Set<Integer> merchantIds = new HashSet<Integer>();
		Set<Integer> warehouseIds = new HashSet<Integer>();
		Set<Integer> warehousePIMappingIds = new HashSet<Integer>();
		List<ProductItemVO> piVOs = new ArrayList<ProductItemVO>();
		for (PuObject productItem : productItems) {
			Integer productItemId = productItem.getInteger(PI_ID);
			Long updateTime = productItem.getLong(PI_UPDATE_TIME);
			Integer productId = productItem.getInteger(PI_PRODUCT_ID);
			Integer brandId = productItem.getInteger(PI_BRAND_ID);
			String brandName = productItem.getString(PI_BRAND_NAME);
			Integer categoryId = productItem.getInteger(PI_CAT_ID);
			String barcode = productItem.getString(PI_BARCODE);
			String productItemName = productItem.getString(PI_PRODUCT_ITEM_NAME);
			Integer productItemStatus = productItem.getInteger(PI_PRODUCT_ITEM_STATUS);
			Integer freshFoodType = productItem.getInteger(PI_FRESH_FOOD_TYPE);
			Double weight = productItem.getDouble(PI_WEIGHT);
			String sCreateTime = productItem.getString(PI_CREATE_TIME);
			Integer brandStatus = productItem.getInteger(PI_BRAND_STATUS);
			Integer categoryStatus = productItem.getInteger(PI_CATEGORY_STATUS);
			List<Long> categoryPath = (List<Long>) productItem.getLongArray(PI_CATEGORY_PATH);
			Integer productItemType = productItem.getInteger(PI_PRODUCT_ITEM_TYPE);
			Integer productItemPolicy = productItem.getInteger(PI_PRODUCT_ITEM_POLICY);
			String image = productItem.getString(PI_IMAGE);
			if (productItemId == null || productId == null || brandId == null || brandName == null
					|| brandStatus == null || categoryId == null || categoryStatus == null || categoryPath == null
					|| barcode == null || productItemName == null || productItemStatus == null || freshFoodType == null
					|| productItemType == null || weight == null || sCreateTime == null || updateTime == null
					|| productItemPolicy == null) {
				getLogger().error("some param of ProductItem: " + productId + " is missing");
				continue;
			}
			Date createDate;
			try {
				SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				createDate = df.parse(sCreateTime);
			} catch (Exception e) {
				getLogger().error("createTime is invalid format: " + sCreateTime, e);
				continue;
			}
			long createTime = createDate.getTime() - (3600000 * getTimeZoneGap());

			// Get Attributes
			Collection<PuObject> puAtts = productItem.getPuObjectArray(PI_SOLR_FE_PRODUCT_ATTRIBUTE);
			List<AttributeValueVO> atts = new ArrayList<AttributeValueVO>();
			if (puAtts == null) {
				getLogger().debug("atts is null");
			} else {
				Set<Integer> attIds = new HashSet<Integer>();
				for (PuObject puAtt : puAtts) {
					Integer attId = puAtt.getInteger(PI_ATT_ID);
					String attName = puAtt.getString(PI_ATT_NAME);
					Integer attStatus = puAtt.getInteger(PI_ATT_STATUS);
					String attValue = puAtt.getString(PI_ATT_VALUE);
					Integer attValueId = puAtt.getInteger(PI_ATT_VALUE_ID);
					if (attId == null || attName == null || attStatus == null || attValue == null || attValueId == null) {
						getLogger().error("some parameters of attribute are null");
						continue;
					}

					if (attIds.contains(attId)) {
						continue;
					}

					AttributeValueVO attValueVO = new AttributeValueVO(attId, attName, attStatus, attValueId, attValue);
					attIds.add(attId);
					atts.add(attValueVO);
				}
			}
			if (atts.isEmpty()) {
				atts = null;
			}
			// End: Get Attributes

			// Get warehouses
			Collection<PuObject> puWarehouses = productItem.getPuObjectArray(PI_WH_PI_MAPPING);
			if (puWarehouses == null) {
				getLogger().error("warehouseProductItemMappings of " + productItemId + " is null");
				continue;
			}
			List<ProductItemWarehouseProductItemMappingVO> whs = new ArrayList<ProductItemWarehouseProductItemMappingVO>();
			for (PuObject puWh : puWarehouses) {
				Integer warehouseProductItemMappingId = puWh.getInteger(PI_WH_PI_MAPPING_ID);
				Integer merchantId = puWh.getInteger(PI_MC_ID);
				Integer warehouseId = puWh.getInteger(PI_WH_ID);
				String merchantName = puWh.getString(PI_MC_NAME);
				String merchantSKU = puWh.getString(PI_MC_SKU);
				Double originalPrice = puWh.getDouble(PI_ORI_PRICE);
				Double sellPrice = puWh.getDouble(PI_SELL_PRICE);
				Integer quantity = puWh.getInteger(PI_QUANTITY);
				Integer safetyStock = puWh.getInteger(PI_SAFETY_STOCK);
				Integer merchantProductItemStatus = puWh.getInteger(PI_MC_PI_STATUS);
				Integer merchantStatus = puWh.getInteger(PI_MERCHANT_STATUS);
				Integer warehouseStatus = puWh.getInteger(PI_WAREHOUSE_STATUS);
				Integer provinceId = puWh.getInteger(PI_PROVINCE_ID);
				Integer isVisible = puWh.getInteger(PI_IS_VISIBLE);
				Integer priceStatus = puWh.getInteger(PI_PRICE_STATUS);
				Integer vatStatus = puWh.getInteger(PI_VAT_STATUS);
				//add 21_09_2015
				Integer isNotApplyCommision = puWh.getInteger(PI_IS_NOT_APPLY_COMMISION);
				Double commisionFee = puWh.getDouble(PI_COMMISION_FEE);
				if (warehouseProductItemMappingId == null || merchantId == null || warehouseId == null
						|| merchantName == null || merchantSKU == null || originalPrice == null || sellPrice == null
						|| quantity == null || safetyStock == null || merchantProductItemStatus == null
						|| merchantStatus == null || warehouseStatus == null || provinceId == null || isVisible == null
						|| vatStatus == null || priceStatus == null || isNotApplyCommision == null || commisionFee == null) {
					getLogger().error(
							"some parameters of  warehouseProductItemMapping of productItem: " + productItemId
									+ " is missing");
					continue;
				}

				ProductItemWarehouseProductItemMappingVO vo = new ProductItemWarehouseProductItemMappingVO(
						warehouseProductItemMappingId, merchantId, warehouseId, merchantName, merchantSKU,
						originalPrice, sellPrice, quantity, safetyStock, merchantProductItemStatus, merchantStatus,
						warehouseStatus, provinceId, isVisible, updateTime, priceStatus, vatStatus, isNotApplyCommision, commisionFee);

				merchantIds.add(merchantId);
				warehouseIds.add(warehouseId);
				warehousePIMappingIds.add(warehouseProductItemMappingId);
				whs.add(vo);
			}
			if (whs.isEmpty()) {
				getLogger().error("no warehouseProductItemMapping found");
				continue;
			}
			// End Get Warehouse

			productItemIds.add(productItemId);
			brandIds.add(brandId);
			categoryIds.add(categoryId);
			ProductItemVO piVO = new ProductItemVO(productItemId, productId, brandId, brandName, categoryId, barcode,
					productItemName, productItemStatus, brandStatus, categoryStatus, categoryPath, createTime,
					freshFoodType, weight, productItemType, image, atts, whs, productItemPolicy, updateTime);
			piVOs.add(piVO);
		}

		

	}

	private void updateProductItem(Collection<PuObject> productItems) throws Exception {
		// Get batch update time from cache
		Map<Integer, ProductItemVO> piId2ProductItem = new HashMap<Integer, ProductItemVO>();
		Set<Integer> setPiIds = new HashSet<Integer>();
		Set<Integer> brandIds = new HashSet<Integer>();
		Set<Integer> categoryIds = new HashSet<Integer>();
		for (PuObject productItem : productItems) {
			Integer productItemId = productItem.getInteger(PI_ID);
			Long updateTime = productItem.getLong(PI_UPDATE_TIME);
			Integer productId = productItem.getInteger(PI_PRODUCT_ID);
			Integer brandId = productItem.getInteger(PI_BRAND_ID);
			String brandName = productItem.getString(PI_BRAND_NAME);
			Integer categoryId = productItem.getInteger(PI_CAT_ID);
			String barcode = productItem.getString(PI_BARCODE);
			String productItemName = productItem.getString(PI_PRODUCT_ITEM_NAME);
			Integer productItemStatus = productItem.getInteger(PI_PRODUCT_ITEM_STATUS);
			Integer freshFoodType = productItem.getInteger(PI_FRESH_FOOD_TYPE);
			Double weight = productItem.getDouble(PI_WEIGHT);
			String sCreateTime = productItem.getString(PI_CREATE_TIME);
			Integer brandStatus = productItem.getInteger(PI_BRAND_STATUS);
			Integer categoryStatus = productItem.getInteger(PI_CATEGORY_STATUS);
			List<Long> categoryPath = (List<Long>) productItem.getLongArray(PI_CATEGORY_PATH);
			Integer productItemType = productItem.getInteger(PI_PRODUCT_ITEM_TYPE);
			Integer productItemPolicy = productItem.getInteger(PI_PRODUCT_ITEM_POLICY);
			String image = productItem.getString(PI_IMAGE);
			if (productItemId == null || productId == null || brandId == null || brandName == null
					|| brandStatus == null || categoryId == null || categoryStatus == null || categoryPath == null
					|| barcode == null || productItemName == null || productItemStatus == null || freshFoodType == null
					|| productItemType == null || weight == null || sCreateTime == null || updateTime == null
					|| productItemPolicy == null) {
				getLogger().error("some param of ProductItem: " + productId + " is missing");
				continue;
			}
			Date createDate;
			try {
				SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				createDate = df.parse(sCreateTime);
			} catch (Exception e) {
				getLogger().error("createTime is invalid format: " + sCreateTime, e);
				continue;
			}
			long createTime = createDate.getTime() - (3600000 * getTimeZoneGap());

			// Get Attributes
			Collection<PuObject> puAtts = productItem.getPuObjectArray(PI_SOLR_FE_PRODUCT_ATTRIBUTE);
			List<AttributeValueVO> atts = new ArrayList<AttributeValueVO>();
			if (puAtts == null) {
				getLogger().debug("atts is null");
			} else {
				// FIXME check update time for attribute
				Set<Integer> attIds = new HashSet<Integer>();
				for (PuObject puAtt : puAtts) {
					Integer attId = puAtt.getInteger(PI_ATT_ID);
					String attName = puAtt.getString(PI_ATT_NAME);
					Integer attStatus = puAtt.getInteger(PI_ATT_STATUS);
					String attValue = puAtt.getString(PI_ATT_VALUE);
					Integer attValueId = puAtt.getInteger(PI_ATT_VALUE_ID);
					if (attId == null || attName == null || attStatus == null || attValue == null || attValueId == null) {
						getLogger().error("some parameters of attribute are null");
						continue;
					}

					if (attIds.contains(attId)) {
						continue;
					}

					AttributeValueVO attValueVO = new AttributeValueVO(attId, attName, attStatus, attValueId, attValue);
					attIds.add(attId);
					atts.add(attValueVO);
				}
			}
			if (atts.isEmpty()) {
				atts = null;
			}
			// End: Get Attributes

			ProductItemVO piVO = new ProductItemVO(productItemId, productId, brandId, brandName, categoryId, barcode,
					productItemName, productItemStatus, brandStatus, categoryStatus, categoryPath, createTime,
					freshFoodType, weight, productItemType, image, atts, null, productItemPolicy, updateTime);
			piId2ProductItem.put(productItemId, piVO);
			brandIds.add(brandId);
			categoryIds.add(categoryId);
			setPiIds.add(productItemId);
		}
		Map<Integer, Long> lastUpdateTimeOfProductItemCreates = null;
		Map<Integer, Long> lastUpdateTimeOfProductItemUpdates = null;
		Map<Integer, BrandBean> cachedBrandBeans = null;
		// End: Get batch update time from cache

		// Get all ProductItemIds to get info
		List<Integer> piIds = new ArrayList<Integer>();
		for (ProductItemVO piVO : piId2ProductItem.values()) {
			Long lastCreateTime = lastUpdateTimeOfProductItemCreates.get(piVO.getId());
			Long lastUpdateTime = lastUpdateTimeOfProductItemUpdates.get(piVO.getId());
			if ((lastCreateTime != null && lastCreateTime >= piVO.getUpdateTime())
					|| (lastUpdateTime != null && lastUpdateTime >= piVO.getUpdateTime())) {
				getLogger().info("productItemId: " + piVO.getId() + " has been updated by a newer");
				continue;
			}

			// Check some field from cache
			BrandBean brandBean = cachedBrandBeans.get(piVO.getBrandId());
			if (brandBean != null && brandBean.getUpdateTime() >= piVO.getUpdateTime()) {
				piVO.setBrandName(brandBean.getName());
				piVO.setBrandStatus(brandBean.getStatus());
			}
			// End: Check some field from cache

			piIds.add(piVO.getId());
		}
		// End: Get all ProductItemIds to get info

		if (!piIds.isEmpty()) {
			List<ProductItemInfoUpdateBean> piInfos = productItemModel
					.getWarehouseProductItemMappingOfProductItems(piIds);
			if (piInfos == null) {
				getLogger().error("piInfos == null");
				return;
			}
		}
	}
}
