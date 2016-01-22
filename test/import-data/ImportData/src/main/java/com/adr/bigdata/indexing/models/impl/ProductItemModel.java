package com.adr.bigdata.indexing.models.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.models.SolrModel;
import com.adr.bigdata.indexing.utils.StatusesTool;
import com.adr.bigdata.indexing.vos.AttributeValueVO;
import com.adr.bigdata.indexing.vos.ProductItemVO;
import com.adr.bigdata.indexing.vos.ProductItemWarehouseProductItemMappingVO;
import com.adr.bigdata.indexing.vos.SingleMap;
import com.hazelcast.core.IMap;

public class ProductItemModel extends SolrModel {
	public static final String PI_ID = "productItemId";
	public static final String PI_PRODUCT_ID = "productId";
	public static final String PI_BRAND_ID = "brandId";
	public static final String PI_BRAND_NAME = "brandName";
	public static final String PI_CAT_ID = "categoryId";
	public static final String PI_BARCODE = "barcode";
	public static final String PI_PRODUCT_ITEM_NAME = "productItemName";
	public static final String PI_PRODUCT_ITEM_STATUS = "productItemStatus";
	public static final String PI_FRESH_FOOD_TYPE = "freshFoodType";
	public static final String PI_WEIGHT = "weight";
	public static final String PI_CREATE_TIME = "createTime";
	public static final String PI_SOLR_FE_PRODUCT_ATTRIBUTE = "solrFeProductAttribute";
	public static final String PI_ATT_ID = "attributeId";
	public static final String PI_ATT_NAME = "attributeName";
	public static final String PI_ATT_VALUE_ID = "attributeValueId";
	public static final String PI_ATT_VALUE = "attributeValue";
	public static final String PI_ATT_STATUS = "attributeStatus";
	public static final String PI_WH_PI_MAPPING = "warehouseProductItemMapping";
	public static final String PI_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static final String PI_MC_ID = "merchantId";
	public static final String PI_WH_ID = "warehouseId";
	public static final String PI_MC_NAME = "merchantName";
	public static final String PI_MC_SKU = "merchantSKU";
	public static final String PI_ORI_PRICE = "originalPrice";
	public static final String PI_SELL_PRICE = "sellPrice";
	public static final String PI_QUANTITY = "quantity";
	public static final String PI_SAFETY_STOCK = "safetyStock";
	public static final String PI_MC_PI_STATUS = "merchantProductItemStatus";

	// ADD 07-05-2015
	public static final String PI_BRAND_STATUS = "brandStatus";
	public static final String PI_CATEGORY_NAME = "categoryName";
	public static final String PI_CATEGORY_STATUS = "categoryStatus";
	public static final String PI_PRODUCT_ITEM_TYPE = "productItemType";
	public static final String PI_CATEGORY_PATH = "categoryPath";
	public static final String PI_IMAGE = "image";
	public static final String PI_UPDATE_TIME = "updateTime";
	public static final String PI_MERCHANT_STATUS = "merchantStatus";
	public static final String PI_WAREHOUSE_STATUS = "warehouseStatus";
	public static final String PI_PROVINCE_ID = "provinceId";

	// add 16-05-2015
	public static final String PI_IS_VISIBLE = "isVisible";

	public static final String PI_VAT_STATUS = "vatStatus";
	public static final String PI_PRICE_STATUS = "priceStatus";

	// add 15-07-2015
	public static final String PI_PRODUCT_ITEM_POLICY = "productItemPolicy";
	
	// add 19-09-2015
	public static final String PI_IS_NOT_APPLY_COMMISION = "isNotApplyCommision";
	public static final String PI_COMMISION_FEE = "commisionFee";

	public void updateToCacheForUpdate(List<ProductItemInfoUpdateBean> piInfos,
			Map<Integer, ProductItemVO> piId2ProductItem) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		for (ProductItemInfoUpdateBean piInfo : piInfos) {
			ProductItemVO productItem = piId2ProductItem.get(piInfo.getProductItemId());
			map.put(productItem.getId(), productItem.getUpdateTime());
		}
		try {
			getCacheWrapper().getCacheMap(CacheFields.PRODUCT_ITEM_UPDATE).putAll(map);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	public void updateToSolrForUpdate(List<ProductItemInfoUpdateBean> piInfos,
			Map<Integer, ProductItemVO> piId2ProductItem) throws Exception {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (ProductItemInfoUpdateBean piInfo : piInfos) {
			ProductItemVO productItem = piId2ProductItem.get(piInfo.getProductItemId());
			SolrInputDocument doc = makeSolrDocumentForUpdate(piInfo.getWhpiId(), piInfo.getProvinceId(), productItem);
			docs.add(doc);

			if (docs.size() >= getNumDocPerRequest()) {
				add(docs);
				docs = new ArrayList<SolrInputDocument>();
			}
		}
		if (!docs.isEmpty()) {
			add(docs);
		}
		if (isCommit()) {
			commit();
		}
	}

	public List<ProductItemInfoUpdateBean> getWarehouseProductItemMappingOfProductItems(List<Integer> piIds)
			throws Exception {
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().openDAO(WarehouseProductItemMappingIdDAO.class)) {
			return dao.getPIInfoWhenUpdate(piIds);
		}
	}

	public List<SolrInputDocument> makeSolrDocumentForCreate(ProductItemVO piVO,
			Map<Integer, Long> lastUpdateTimeOfProductItemCreates,
			Map<Integer, Long> lastUpdateTimeOfProductItemUpdates, Map<Integer, BrandBean> cachedBrandBeans,
			Map<Integer, MerchantBean> cachedMerchantBeans, Map<Integer, Long> lastUpdateTimeOfWHPIMappings,
			List<Integer> updatedProductItemIds, List<Long> updatedProductItemIdTimes,
			List<Integer> updatedWarehouseProductItemMappingIds, List<Long> updatedWarehouseProductItemMappingIdTimes) {
		Long lastCreateTime = lastUpdateTimeOfProductItemCreates.get(piVO.getId());
		if (lastCreateTime != null && lastCreateTime >= piVO.getUpdateTime()) {
			getLogger().info("productItemId " + piVO.getId() + " has a newer CreateProductItem");
			return null;
		}

		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		boolean needUpdateProductItemInfo;
		Long lastUpdateTime = lastUpdateTimeOfProductItemUpdates.get(piVO.getId());
		if (lastUpdateTime != null && lastUpdateTime >= piVO.getUpdateTime()) {
			needUpdateProductItemInfo = false;
			getLogger().info(
					"productItemId :" + piVO.getId()
							+ " has a newer UpdateProductItemUpdate, just update warehouseMapping");
		} else {
			needUpdateProductItemInfo = true;
			BrandBean brandBean = cachedBrandBeans.get(piVO.getBrandId());
			if (brandBean != null && brandBean.getUpdateTime() >= piVO.getUpdateTime()) {
				piVO.setBrandName(brandBean.getName());
				piVO.setBrandStatus(brandBean.getStatus());
			}
			// CategoryBean categoryBean = cachedCategoryBeans.get(piVO.getCategoryId());
			// if (categoryBean != null && categoryBean.getUpdateTime() >= piVO.getUpdateTime()) {
			// piVO.setCategoryStatus(categoryBean.getStatus());
			// piVO.setCategoryPath(CommonTools.getLongsFromIntegers(categoryBean.getPath()));
			// }
		}

		for (ProductItemWarehouseProductItemMappingVO wh : piVO.getWhs()) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, wh.getWarehouseProductItemMappingId());
			if (needUpdateProductItemInfo) {
				doc.addField(SolrFields.PRODUCT_ID, new SingleMap("set", piVO.getProductId()));
				doc.addField(SolrFields.PRODUCT_ITEM_GROUP, new SingleMap("set", piVO.getProductId()));
				doc.addField(SolrFields.PRODUCT_ITEM_ID, new SingleMap("set", piVO.getId()));
				doc.addField(SolrFields.BRAND_ID, new SingleMap("set", piVO.getBrandId()));
				// doc.addField(SolrFields.BRAND_ID_FACET, new SingleMap("set", piVO.getBrandId()));
				doc.addField(SolrFields.BRAND_NAME, new SingleMap("set", piVO.getBrandName()));
				doc.addField(SolrFields.IS_BRAND_ACTIVE, new SingleMap("set", piVO.getBrandStatus() == 1));
				doc.addField(SolrFields.CATEGORY_ID, new SingleMap("set", piVO.getCategoryId()));
				// doc.addField(SolrFields.CATEGORY_ID_FACET, new SingleMap("set", piVO.getCategoryId()));
				doc.addField(SolrFields.CATEGORY_PATH, new SingleMap("set", piVO.getCategoryPath()));
				doc.addField(SolrFields.IS_CATEGORY_ACTIVE, new SingleMap("set", piVO.getCategoryStatus() == 1));
				doc.addField(SolrFields.BARCODE, new SingleMap("set", piVO.getBarcode()));
				doc.addField(SolrFields.PRODUCT_ITEM_NAME, new SingleMap("set", piVO.getName()));
				doc.addField(SolrFields.CREATE_TIME, new SingleMap("set", piVO.getCreateTime()));
				doc.addField(SolrFields.PRODUCT_ITEM_STATUS, new SingleMap("set", piVO.getStatus()));
				// doc.addField(SolrFields.FRESH_FOOD_TYPE, new SingleMap("set", piVO.getFreshFoodType()));
				doc.addField(SolrFields.WEIGHT, new SingleMap("set", piVO.getWeight()));
				doc.addField(SolrFields.PRODUCT_ITEM_TYPE, new SingleMap("set", piVO.getType()));
				doc.addField(SolrFields.IMAGE, new SingleMap("set", piVO.getImage()));

				if (piVO.getAtts() != null && !piVO.getAtts().isEmpty()) {
					// FIXME add updateTime for attribute
					for (AttributeValueVO att : piVO.getAtts()) {
						doc.addField(String.format(SolrFields.ATT_S, att.getAttributeId()),
								new SingleMap("set", att.getAttributeValue()));
						doc.addField(String.format(SolrFields.ATT_I, att.getAttributeId()),
								new SingleMap("set", att.getAttributeValueId()));
						doc.addField(String.format(SolrFields.ATT_I_FACET, att.getAttributeId()),
								new SingleMap("set", att.getAttributeValueId()));
						doc.addField(String.format(SolrFields.ATT_NAME_S, att.getAttributeId()), 
								new SingleMap("set", att.getAttributeName()));
						try {
							doc.addField(String.format(SolrFields.ATT_D, att.getAttributeId()), new SingleMap("set",
									Double.parseDouble(att.getAttributeValue())));
						} catch (Exception e) {

						}
					}
				}
			}

			MerchantBean merchantBean = cachedMerchantBeans.get(wh.getMerchantId());
			if (merchantBean != null && merchantBean.getUpdateTime() >= piVO.getUpdateTime()) {
				wh.setMerchantStatus(merchantBean.getStatus());
				wh.setMerchantName(merchantBean.getName());
			}
			// WarehouseBean warehouseBean = cachedWarehouseBean.get(wh.getWarehouseId());
			// if (warehouseBean != null && warehouseBean.getUpdateTime() >= piVO.getUpdateTime()) {
			// wh.setWarehouseStatus(warehouseBean.getStatus());
			// }

			// Check UpdateTime of whpim
			Long lastWarehouseProductItemMappingTime = lastUpdateTimeOfWHPIMappings.get(wh
					.getWarehouseProductItemMappingId());
			if (lastWarehouseProductItemMappingTime != null
					&& lastWarehouseProductItemMappingTime >= piVO.getUpdateTime()) {
				// do nothing
			} else {
				doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_SKU, new SingleMap("set", wh.getMerchantSKU()));
				doc.addField(SolrFields.SELL_PRICE, new SingleMap("set", wh.getSellPrice()));

				if (wh.getOriginalPrice() > wh.getSellPrice()) {
					doc.addField(SolrFields.PRICE_FLAG, new SingleMap("set", true));
				} else {
					doc.addField(SolrFields.PRICE_FLAG, new SingleMap("set", false));
				}

				doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_STATUS,
						new SingleMap("set", wh.getMerchantProductItemStatus()));
				doc.addField(SolrFields.QUANTITY, new SingleMap("set", wh.getQuantity()));

				// doc.addField(SolrFields.SAFETY_STOCK, new SingleMap("set", wh.getSafetyStock()));
				// doc.addField(SolrFields.IS_SAFETY_STOCK, new SingleMap("set", wh.getQuantity() >
				// wh.getSafetyStock()));

				doc.addField(SolrFields.VISIBLE, new SingleMap("set", StatusesTool.getListBits(wh.getIsVisible())));
				doc.addField(SolrFields.ON_SITE, new SingleMap("set", wh.getIsVisible()));
			}
			// End: Check UpdateTime of whpim

			doc.addField(SolrFields.WAREHOUSE_ID, new SingleMap("set", wh.getWarehouseId()));
			doc.addField(SolrFields.MERCHANT_ID, new SingleMap("set", wh.getMerchantId()));
			doc.addField(SolrFields.MERCHANT_NAME, new SingleMap("set", wh.getMerchantName()));
			doc.addField(SolrFields.PRODUCT_ITEM_ID_MERCHANT_ID,
					new SingleMap("set", piVO.getId() + "_" + wh.getMerchantId()));

			doc.addField(SolrFields.IS_MERCHANT_ACTIVE, new SingleMap("set", wh.getMerchantStatus() == 1));
			doc.addField(SolrFields.WAREHOUSE_STATUS, new SingleMap("set", wh.getWarehouseStatus()));

			if (piVO.getType() == 2 || piVO.getType() == 4 || piVO.getPolicy() != 1) {
				doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(wh.getProvinceId(), 0)));
			} else {
				doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(4, 8, 0)));
			}
			// doc.addField(SolrFields.CITY_ID_FACET, new SingleMap("set", wh.getProvinceId()));

			doc.addField(SolrFields.PRICE_STATUS, new SingleMap("set", wh.getPriceStatus()));
			doc.addField(SolrFields.VAT_STATUS, new SingleMap("set", wh.getVatStatus()));

			doc.addField(SolrFields.PRODUCT_ITEM_POLICY, new SingleMap("set", piVO.getPolicy()));
			doc.addField(SolrFields.PRODUCT_ITEM_POLICY_BITS,
					new SingleMap("set", StatusesTool.getListBits(piVO.getPolicy())));
			
			//add 21_09_2015
			doc.addField(SolrFields.IS_NOT_APPLY_COMMISION, new SingleMap("set", wh.getIsNotApplyCommision() == 1));
			doc.addField(SolrFields.COMMISION_FEE, new SingleMap("set", wh.getCommisionFee()));

			docs.add(doc);
			updatedWarehouseProductItemMappingIds.add(wh.getWarehouseProductItemMappingId());
			updatedWarehouseProductItemMappingIdTimes.add(piVO.getUpdateTime());
		}
		updatedProductItemIds.add(piVO.getId());
		updatedProductItemIdTimes.add(piVO.getUpdateTime());
		return docs;
	}

	private SolrInputDocument makeSolrDocumentForUpdate(int warehouseProductItemMappingId, int provinceId,
			ProductItemVO pi) throws Exception {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);

		doc.addField(SolrFields.PRODUCT_ID, new SingleMap("set", pi.getProductId()));
		doc.addField(SolrFields.PRODUCT_ITEM_GROUP, new SingleMap("set", pi.getProductId()));

		doc.addField(SolrFields.PRODUCT_ITEM_ID, new SingleMap("set", pi.getId()));

		doc.addField(SolrFields.BRAND_ID, new SingleMap("set", pi.getBrandId()));
		// doc.addField(SolrFields.BRAND_ID_FACET, new SingleMap("set", pi.getBrandId()));

		doc.addField(SolrFields.BRAND_NAME, new SingleMap("set", pi.getBrandName()));

		doc.addField(SolrFields.CATEGORY_ID, new SingleMap("set", pi.getCategoryId()));
		// doc.addField(SolrFields.CATEGORY_ID_FACET, new SingleMap("set", pi.getCategoryId()));

		doc.addField(SolrFields.CATEGORY_PATH, new SingleMap("set", pi.getCategoryPath()));
		doc.addField(SolrFields.BARCODE, new SingleMap("set", pi.getBarcode()));
		doc.addField(SolrFields.PRODUCT_ITEM_NAME, new SingleMap("set", pi.getName()));
		doc.addField(SolrFields.CREATE_TIME, new SingleMap("set", pi.getCreateTime()));
		doc.addField(SolrFields.IS_CATEGORY_ACTIVE, new SingleMap("set", pi.getCategoryStatus() == 1));
		doc.addField(SolrFields.IS_BRAND_ACTIVE, new SingleMap("set", pi.getBrandStatus() == 1));
		doc.addField(SolrFields.PRODUCT_ITEM_STATUS, new SingleMap("set", pi.getStatus()));
		// doc.addField(SolrFields.FRESH_FOOD_TYPE, new SingleMap("set", pi.getFreshFoodType()));
		doc.addField(SolrFields.WEIGHT, new SingleMap("set", pi.getWeight()));
		doc.addField(SolrFields.PRODUCT_ITEM_TYPE, new SingleMap("set", pi.getType()));
		doc.addField(SolrFields.IMAGE, new SingleMap("set", pi.getImage()));

		if (pi.getAtts() != null && !pi.getAtts().isEmpty()) {
			for (AttributeValueVO att : pi.getAtts()) {
				doc.addField(String.format(SolrFields.ATT_S, att.getAttributeId()),
						new SingleMap("set", att.getAttributeValue()));
				doc.addField(String.format(SolrFields.ATT_I, att.getAttributeId()),
						new SingleMap("set", att.getAttributeValueId()));
				doc.addField(String.format(SolrFields.ATT_I_FACET, att.getAttributeId()),
						new SingleMap("set", att.getAttributeValueId()));
				doc.addField(String.format(SolrFields.ATT_NAME_S, att.getAttributeId()), 
						new SingleMap("set", att.getAttributeName()));
				try {
					doc.addField(String.format(SolrFields.ATT_D, att.getAttributeId()),
							new SingleMap("set", Double.parseDouble(att.getAttributeValue())));
				} catch (Exception e) {

				}
			}
		}

		if (pi.getType() == 2 || pi.getType() == 4 || pi.getPolicy() != 1) {
			doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(provinceId, 0)));
		} else {
			doc.addField(SolrFields.RECEIVED_CITY_ID, new SingleMap("set", Arrays.asList(4, 8, 0)));
		}

		doc.addField(SolrFields.PRODUCT_ITEM_POLICY, new SingleMap("set", pi.getPolicy()));
		doc.addField(SolrFields.PRODUCT_ITEM_POLICY_BITS,
				new SingleMap("set", StatusesTool.getListBits(pi.getPolicy())));

		return doc;
	}

	public void addAndCommit(List<SolrInputDocument> docs) throws SolrServerException, IOException {
		getSolrClient().add(docs);
		getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
	}

	public void add(List<SolrInputDocument> docs) throws SolrServerException, IOException {
		getSolrClient().add(docs);
	}

	public void commit() throws SolrServerException, IOException {
		getSolrClient().commit(isWaitFlush(), isWaitSearcher(), isSoftCommit());
	}

	public void updateToCacheForCreate(List<Integer> updatedProductItemIds, List<Long> updatedProductItemIdTimes,
			List<Integer> updatedWarehouseProductItemMappingIds, List<Long> updatedWarehouseProductItemMappingIdTimes) {
		Map<Integer, Long> piMap = new HashMap<Integer, Long>();
		for (int i = 0; i < updatedProductItemIds.size(); i++) {
			piMap.put(updatedProductItemIds.get(i), updatedProductItemIdTimes.get(i));
		}
		try {
			getCacheWrapper().getCacheMap(CacheFields.PRODUCT_ITEM_CREATE).putAll(piMap);
		} catch (Exception e) {
			getLogger().error("", e);
		}

		Map<Integer, Long> whPiMappingMap = new HashMap<Integer, Long>();
		for (int i = 0; i < updatedWarehouseProductItemMappingIds.size(); i++) {
			whPiMappingMap.put(updatedWarehouseProductItemMappingIds.get(i),
					updatedWarehouseProductItemMappingIdTimes.get(i));
		}
		try {
			getCacheWrapper().getCacheMap(CacheFields.WAREHOUSE_PRODUCT_ITEM_MAPPING).putAll(whPiMappingMap);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimeOfProductItemCreates(Set<Integer> setPiIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.PRODUCT_ITEM_CREATE)).getAll(setPiIds);
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<Integer, Long>();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Integer, Long> getLastUpdateTimeOfProductItemUpdates(Set<Integer> setPiIds) {
		try {
			return ((IMap) getCacheWrapper().getCacheMap(CacheFields.PRODUCT_ITEM_UPDATE)).getAll(setPiIds);
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<Integer, Long>();
		}
	}

}
