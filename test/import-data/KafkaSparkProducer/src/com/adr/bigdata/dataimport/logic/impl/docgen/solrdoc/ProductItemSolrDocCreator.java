package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.adr.bigdata.indexing.db.sql.vos.AttributeAttributeValueVO;
import com.adr.bigdata.indexing.db.sql.vos.ProductItemWarehouseProductItemMappingVO;
import com.adr.bigdata.indexing.utils.StatusesTool;

public class ProductItemSolrDocCreator extends CachedModel {
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public SolrInputDocument createSolrDocument(
			int warehouseProductItemMappingId, int productItemId,
			Integer productId, Integer brandId, String brandName,
			Integer categoryId, String barcode, String productItemName,
			Integer productItemStatus, Integer freshFoodType, Double weight,
			String createTime, List<AttributeAttributeValueVO> atts,
			ProductItemWarehouseProductItemMappingVO wh, int brandStatus,
			String categoryName, int categoryStatus, List<Long> categoryPath,
			int productItemType, String image, Long updateTime)
			throws Exception {
		
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID,
				warehouseProductItemMappingId);
		/* Check so vs cac create Product Item khac */
		try {
			Long lastCreateTime = (Long) getCacheWrapper().getCacheMap(
					CacheFields.PRODUCT_ITEM_CREATE).get(productItemId);
			if (lastCreateTime != null && lastCreateTime >= updateTime) {
				getLogger().info(
						"productItemId " + productItemId
								+ " has a newer CreateProductItem");
				return null;
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
		/* End: Check so vs cac create Product Item khac */

		/* Check so vs cac update ProductItem khac */
		try {
			Long lastUpdateTime = (Long) getCacheWrapper().getCacheMap(
					CacheFields.PRODUCT_ITEM_UPDATE).get(productItemId);
			if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
				getLogger()
						.info("productItemId :"
								+ productItemId
								+ " has a newer UpdateProductItem, just update warehouseMapping");
			} else {
				/* Check some field from cache */
				BrandBean brandBean = (BrandBean) getCacheWrapper()
						.getCacheMap(CacheFields.BRAND).get(brandId);
				if (brandBean != null
						&& brandBean.getUpdateTime() >= updateTime) {
					brandName = brandBean.getName();
					brandStatus = brandBean.getStatus();
				}

				CategoryBean categoryBean = (CategoryBean) getCacheWrapper()
						.getCacheMap(CacheFields.CATEGORY).get(categoryId);
				if (categoryBean != null
						&& categoryBean.getUpdateTime() >= updateTime) {
					categoryStatus = categoryBean.getStatus();
					categoryPath = new ArrayList<Long>();
					for (int catId : categoryBean.getPath()) {
						categoryPath.add((long) catId);
					}
				}
				/* End: Check some field from cache */

				doc.addField(SolrFields.PRODUCT_ID, productId);
				doc.addField(SolrFields.PRODUCT_ITEM_GROUP, productId);
				doc.addField(SolrFields.PRODUCT_ITEM_ID, productItemId);
				doc.addField(SolrFields.BRAND_ID, brandId);
				doc.addField(SolrFields.BRAND_ID_FACET, brandId);
				doc.addField(SolrFields.BRAND_NAME, brandName);
				doc.addField(SolrFields.IS_BRAND_ACTIVE, brandStatus == 1);
				doc.addField(SolrFields.CATEGORY_ID, categoryId);
				doc.addField(SolrFields.CATEGORY_ID_FACET, categoryId);
				doc.addField(SolrFields.CATEGORY_PATH, categoryPath);
				doc.addField(SolrFields.IS_CATEGORY_ACTIVE, categoryStatus == 1);
				doc.addField(SolrFields.BARCODE, barcode);
				doc.addField(SolrFields.PRODUCT_ITEM_NAME, productItemName);
				Date d = df.parse(createTime);
				doc.addField(SolrFields.CREATE_TIME, d.getTime()); // FIXME
				doc.addField(SolrFields.IS_PRODUCT_ITEM_ACTIVE,
						productItemStatus == 1);
				doc.addField(SolrFields.FRESH_FOOD_TYPE, freshFoodType);
				doc.addField(SolrFields.WEIGHT, weight);

				doc.addField(SolrFields.PRODUCT_ITEM_TYPE, productItemType);
				doc.addField(SolrFields.IMAGE, image);

				if (atts != null && !atts.isEmpty()) {// FIXME add updateTime
														// for attribute
					Set<Integer> set = new HashSet<Integer>();
					for (AttributeAttributeValueVO att : atts) {
						if (set.contains(att.getAttributeId())) {
							continue;
						}
						Map<String, Object> fsValueId = new HashMap<String, Object>();
						fsValueId.put("set", att.getAttributeValueId());

						Map<String, Object> fsValue = new HashMap<String, Object>();
						fsValue.put("set", att.getAttributeValue());

						Map<String, Object> fsName = new HashMap<String, Object>();
						fsName.put("set", att.getAttributeName());

						Map<String, Object> fsStatus = new HashMap<String, Object>();
						fsStatus.put("set", att.getAttributeStatus());

						doc.addField(
								String.format(SolrFields.ATT_S,
										att.getAttributeId()), fsValue);
						doc.addField(
								String.format(SolrFields.ATT_NAME_S,
										att.getAttributeId()), fsName);
						doc.addField(
								String.format(SolrFields.ATT_I,
										att.getAttributeId()), fsValueId);
						doc.addField(
								String.format(SolrFields.ATT_B,
										att.getAttributeId()), fsStatus);

						try {
							Map<String, Object> fsValueD = new HashMap<String, Object>();
							fsValueD.put("set",
									Double.parseDouble(att.getAttributeValue()));

							doc.addField(
									String.format(SolrFields.ATT_D,
											att.getAttributeId()), fsValueD);
						} catch (NumberFormatException e) {
							// do nothing
						}
						set.add(att.getAttributeId());
					}
				}
			}
		} catch (Exception e) {
			/* Check some field from cache */
			BrandBean brandBean = (BrandBean) getCacheWrapper().getCacheMap(
					CacheFields.BRAND).get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				brandName = brandBean.getName();
				brandStatus = brandBean.getStatus();
			}

			CategoryBean categoryBean = (CategoryBean) getCacheWrapper()
					.getCacheMap(CacheFields.CATEGORY).get(categoryId);
			if (categoryBean != null
					&& categoryBean.getUpdateTime() >= updateTime) {
				categoryStatus = categoryBean.getStatus();
				categoryPath = new ArrayList<Long>();
				for (int catId : categoryBean.getPath()) {
					categoryPath.add((long) catId);
				}
			}
			/* End: Check some field from cache */

			doc.addField(SolrFields.PRODUCT_ID, productId);
			doc.addField(SolrFields.PRODUCT_ITEM_GROUP, productId);
			doc.addField(SolrFields.PRODUCT_ITEM_ID, productItemId);
			doc.addField(SolrFields.BRAND_ID, brandId);
			doc.addField(SolrFields.BRAND_ID_FACET, brandId);
			doc.addField(SolrFields.BRAND_NAME, brandName);
			doc.addField(SolrFields.IS_BRAND_ACTIVE, brandStatus == 1);
			doc.addField(SolrFields.CATEGORY_ID, categoryId);
			doc.addField(SolrFields.CATEGORY_ID_FACET, categoryId);
			doc.addField(SolrFields.CATEGORY_PATH, categoryPath);
			doc.addField(SolrFields.IS_CATEGORY_ACTIVE, categoryStatus == 1);
			doc.addField(SolrFields.BARCODE, barcode);
			doc.addField(SolrFields.PRODUCT_ITEM_NAME, productItemName);
			Date d = df.parse(createTime);
			doc.addField(SolrFields.CREATE_TIME, d.getTime()); // FIXME
			doc.addField(SolrFields.IS_PRODUCT_ITEM_ACTIVE,
					productItemStatus == 1);
			doc.addField(SolrFields.FRESH_FOOD_TYPE, freshFoodType);
			doc.addField(SolrFields.WEIGHT, weight);

			doc.addField(SolrFields.PRODUCT_ITEM_TYPE, productItemType);
			doc.addField(SolrFields.IMAGE, image);

			if (atts != null && !atts.isEmpty()) { // FIXME add updateTime for
													// attribute
				Set<Integer> set = new HashSet<Integer>();
				for (AttributeAttributeValueVO att : atts) {
					if (set.contains(att.getAttributeId())) {
						continue;
					}
					Map<String, Object> fsValueId = new HashMap<String, Object>();
					fsValueId.put("set", att.getAttributeValueId());

					Map<String, Object> fsValue = new HashMap<String, Object>();
					fsValue.put("set", att.getAttributeValue());

					Map<String, Object> fsName = new HashMap<String, Object>();
					fsName.put("set", att.getAttributeName());

					Map<String, Object> fsStatus = new HashMap<String, Object>();
					fsStatus.put("set", att.getAttributeStatus());

					doc.addField(
							String.format(SolrFields.ATT_S,
									att.getAttributeId()), fsValue);
					doc.addField(
							String.format(SolrFields.ATT_NAME_S,
									att.getAttributeId()), fsName);
					doc.addField(
							String.format(SolrFields.ATT_I,
									att.getAttributeId()), fsValueId);
					doc.addField(
							String.format(SolrFields.ATT_B,
									att.getAttributeId()), fsStatus);

					try {
						Map<String, Object> fsValueD = new HashMap<String, Object>();
						fsValueD.put("set",
								Double.parseDouble(att.getAttributeValue()));

						doc.addField(
								String.format(SolrFields.ATT_D,
										att.getAttributeId()), fsValueD);
					} catch (NumberFormatException ex) {
						// do nothing
					}
					set.add(att.getAttributeId());
				}
			}
			getLogger().error("", e);
		}
		/* End: Check so vs cac update ProductItem khac */

		/* Check some info of warehouseMapping */
		try {
			MerchantBean merchantBean = (MerchantBean) getCacheWrapper()
					.getCacheMap(CacheFields.MERCHANT).get(wh.getMerchantId());
			if (merchantBean != null
					&& merchantBean.getUpdateTime() >= updateTime) {
				wh.setMerchantStatus(merchantBean.getStatus());
			}

			WarehouseBean warehouseBean = (WarehouseBean) getCacheWrapper()
					.getCacheMap(CacheFields.WAREHOUSE)
					.get(wh.getWarehouseId());
			if (warehouseBean != null
					&& warehouseBean.getUpdateTime() >= updateTime) {
				wh.setWarehouseStatus(warehouseBean.getStatus());
			}
		} catch (Exception e) {
			getLogger().error("", e);
		}
		/* End: Check some info of warehouseMapping */

		/* Check updateTime of warehouseProductItemMapping */
		try {
			Long lastWarehouseProductItemMappingTime = (Long) getCacheWrapper()
					.getCacheMap(CacheFields.WAREHOUSE_PRODUCT_ITEM_MAPPING)
					.get(warehouseProductItemMappingId);
			if (lastWarehouseProductItemMappingTime != null
					&& lastWarehouseProductItemMappingTime >= updateTime) {

			} else {
				doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_SKU,
						wh.getMerchantSKU());
				//doc.addField(SolrFields.ORIGINAL_PRICE, wh.getOriginalPrice());
				doc.addField(SolrFields.SELL_PRICE, wh.getSellPrice());
				doc.addField(SolrFields.IS_MERCHANT_PRODUCT_ITEM_ACTIVE,
						wh.getMerchantProductItemStatus() == 1);
				doc.addField(SolrFields.QUANTITY, wh.getQuantity());

				doc.addField(SolrFields.SAFETY_STOCK, wh.getSafetyStock());
				doc.addField(SolrFields.IS_SAFETY_STOCK,
						wh.getQuantity() > wh.getSafetyStock());

				doc.addField(SolrFields.VISIBLE,
						StatusesTool.getListBits(wh.getIsVisible()));
			}
		} catch (Exception e) {
			doc.addField(SolrFields.MERCHANT_PRODUCT_ITEM_SKU,
					wh.getMerchantSKU());
			//doc.addField(SolrFields.ORIGINAL_PRICE, wh.getOriginalPrice());
			doc.addField(SolrFields.SELL_PRICE, wh.getSellPrice());
			doc.addField(SolrFields.IS_MERCHANT_PRODUCT_ITEM_ACTIVE,
					wh.getMerchantProductItemStatus() == 1);
			doc.addField(SolrFields.QUANTITY, wh.getQuantity());

			doc.addField(SolrFields.SAFETY_STOCK, wh.getSafetyStock());
			doc.addField(SolrFields.IS_SAFETY_STOCK,
					wh.getQuantity() > wh.getSafetyStock());

			doc.addField(SolrFields.VISIBLE,
					StatusesTool.getListBits(wh.getIsVisible()));
			getLogger().error("", e);
		}
		/* End: Check updateTime of warehouseProductItemMapping */

		doc.addField(SolrFields.WAREHOUSE_ID, wh.getWarehouseId());
		doc.addField(SolrFields.MERCHANT_ID, wh.getMerchantId());
		doc.addField(SolrFields.MERCHANT_NAME, wh.getMerchantName());
		doc.addField(SolrFields.PRODUCT_ITEM_ID_MERCHANT_ID, productItemId
				+ "_" + wh.getMerchantId());

		doc.addField(SolrFields.IS_MERCHANT_ACTIVE, wh.getMerchantStatus() == 1);
		doc.addField(SolrFields.IS_WAREHOUSE_ACTIVE,
				wh.getWarehouseStatus() == 1);

		//doc.addField(SolrFields.CITY_ID, wh.getProvinceId());
		doc.addField("received_city_id",wh.getProvinceId());
		//doc.addField(SolrFields.CITY_ID_FACET, wh.getProvinceId());

		return doc;
	}
	
	public SolrInputDocument updateSolrDocument(int warehouseProductItemMappingId, Integer productItemId,
			Integer productId, Integer brandId, String brandName, Integer categoryId, String barcode,
			String productItemName, Integer productItemStatus, Integer freshFoodType, Double weight,
			String sCreateTime, List<AttributeAttributeValueVO> atts, Integer brandStatus, String categoryName,
			Integer categoryStatus, Collection<Long> categoryPath, Integer productItemType, String image)
			throws Exception {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(SolrFields.PRODUCT_ITEM_ID_WAREHOUSE_ID, warehouseProductItemMappingId);

		int count = 0;
		if (productId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productId);
			doc.addField(SolrFields.PRODUCT_ID, fs);
			doc.addField(SolrFields.PRODUCT_ITEM_GROUP, fs);
			count++;
		}

		if (productItemId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemId);
			doc.addField(SolrFields.PRODUCT_ITEM_ID, fs);
			count++;
		}

		if (brandId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandId);
			doc.addField(SolrFields.BRAND_ID, fs);
			doc.addField(SolrFields.BRAND_ID_FACET, fs);
			count++;
		}

		if (brandName != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandName);
			doc.addField(SolrFields.BRAND_NAME, fs);
			count++;
		}

		if (categoryId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryId);
			doc.addField(SolrFields.CATEGORY_ID, fs);
			doc.addField(SolrFields.CATEGORY_ID_FACET, fs);
			count++;
		}

		if (categoryPath != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryPath);
			doc.addField(SolrFields.CATEGORY_PATH, fs);
			count++;
		}

		if (barcode != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", barcode);
			doc.addField(SolrFields.BARCODE, fs);
			count++;
		}

		if (productItemName != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemName);
			doc.addField(SolrFields.PRODUCT_ITEM_NAME, fs);
			count++;
		}

		if (sCreateTime != null) {
			Date d = df.parse(sCreateTime);
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", d.getTime());
			doc.addField(SolrFields.CREATE_TIME, fs);
			count++;
		}

		// category name

		if (categoryStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryStatus == 1);
			doc.addField(SolrFields.IS_CATEGORY_ACTIVE, fs);
			count++;
		}

		if (brandStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandStatus == 1);
			doc.addField(SolrFields.IS_BRAND_ACTIVE, fs);
			count++;
		}

		if (productItemStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemStatus == 1);
			doc.addField(SolrFields.IS_PRODUCT_ITEM_ACTIVE, fs);
			count++;
		}

		if (freshFoodType != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", freshFoodType);
			doc.addField(SolrFields.FRESH_FOOD_TYPE, fs);
			count++;
		}

		if (weight != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", weight);
			doc.addField(SolrFields.WEIGHT, weight);
			count++;
		}

		if (productItemType != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemType);
			doc.addField(SolrFields.PRODUCT_ITEM_TYPE, fs);
			count++;
		}

		if (image != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", image);
			doc.addField(SolrFields.IMAGE, fs);
			count++;
		}

		if (atts != null && !atts.isEmpty()) {
			Set<Integer> set = new HashSet<Integer>();
			for (AttributeAttributeValueVO att : atts) {
				if (set.contains(att.getAttributeId())) {
					continue;
				}
				Map<String, Object> fsValueId = new HashMap<String, Object>();
				fsValueId.put("set", att.getAttributeValueId());

				Map<String, Object> fsValue = new HashMap<String, Object>();
				fsValue.put("set", att.getAttributeValue());

				Map<String, Object> fsName = new HashMap<String, Object>();
				fsName.put("set", att.getAttributeName());

				Map<String, Object> fsStatus = new HashMap<String, Object>();
				fsStatus.put("set", att.getAttributeStatus());

				doc.addField(String.format(SolrFields.ATT_S, att.getAttributeId()), fsValue);
				doc.addField(String.format(SolrFields.ATT_NAME_S, att.getAttributeId()), fsName);
				doc.addField(String.format(SolrFields.ATT_I, att.getAttributeId()), fsValueId);
				doc.addField(String.format(SolrFields.ATT_B, att.getAttributeId()), fsStatus);

				try {
					Map<String, Object> fsValueD = new HashMap<String, Object>();
					fsValueD.put("set", Double.parseDouble(att.getAttributeValue()));

					doc.addField(String.format(SolrFields.ATT_D, att.getAttributeId()), fsValueD);
				} catch (NumberFormatException e) {
					// do nothing
				}
				set.add(att.getAttributeId());
				count++;
			}
		}
		
		if (count == 0)
			return null;
		return doc;
	}
}
