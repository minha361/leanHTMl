package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.text.*;

import com.nhb.common.data.*;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.daos.*;
import com.adr.bigdata.indexing.db.sql.models.CachedModel;

import org.apache.solr.common.SolrInputDocument;

import com.adr.bigdata.indexing.db.sql.vos.*;
import com.adr.bigdata.indexing.db.sql.beans.*;
import com.adr.bigdata.indexing.utils.*;

import java.util.*;

public class ProductItemDocGenDummy extends CachedModel {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	public static String PI_ID = "productItemId";
	public static String PI_PRODUCT_ID = "productId";
	public static String PI_BRAND_ID = "brandId";
	public static String PI_BRAND_NAME = "brandName";
	public static String PI_CAT_ID = "categoryId";
	public static String PI_BARCODE = "barcode";
	public static String PI_PRODUCT_ITEM_NAME = "productItemName";
	public static String PI_PRODUCT_ITEM_STATUS = "productItemStatus";
	public static String PI_FRESH_FOOD_TYPE = "freshFoodType";
	public static String PI_WEIGHT = "weight";
	public static String PI_CREATE_TIME = "createTime";
	public static String PI_SOLR_FE_PRODUCT_ATTRIBUTE = "solrFeProductAttribute";
	public static String PI_ATT_ID = "attributeId";
	public static String PI_ATT_NAME = "attributeName";
	public static String PI_ATT_VALUE_ID = "attributeValueId";
	public static String PI_ATT_VALUE = "attributeValue";
	public static String PI_ATT_STATUS = "attributeStatus";
	public static String PI_WH_PI_MAPPING = "warehouseProductItemMapping";
	public static String PI_WH_PI_MAPPING_ID = "warehouseProductItemMappingId";
	public static String PI_MC_ID = "merchantId";
	public static String PI_WH_ID = "warehouseId";
	public static String PI_MC_NAME = "merchantName";
	public static String PI_MC_SKU = "merchantSKU";
	public static String PI_ORI_PRICE = "originalPrice";
	public static String PI_SELL_PRICE = "sellPrice";
	public static String PI_QUANTITY = "quantity";
	public static String PI_SAFETY_STOCK = "safetyStock";
	public static String PI_MC_PI_STATUS = "merchantProductItemStatus";
	public static String PI_BRAND_STATUS = "brandStatus";
	public static String PI_CATEGORY_NAME = "categoryName";
	public static String PI_CATEGORY_STATUS = "categoryStatus";
	public static String PI_PRODUCT_ITEM_TYPE = "productItemType";
	public static String PI_CATEGORY_PATH = "categoryPath";
	public static String PI_IMAGE = "image";
	public static String PI_UPDATE_TIME = "updateTime";
	public static String PI_MERCHANT_STATUS = "merchantStatus";
	public static String PI_WAREHOUSE_STATUS = "warehouseStatus";
	public static String PI_PROVINCE_ID = "provinceId";
	public static String PI_IS_VISIBLE = "isVisible";
	private DateFormat df;

	public ProductItemDocGenDummy() {
		this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public List<SolrInputDocument> updateProductItem(
			Collection<PuObject> productItems) throws Exception {

		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		List<Integer> piIds = new ArrayList<Integer>();
		Map<Integer, PuObject> piId2ProductItem = new HashMap<Integer, PuObject>();

		for (PuObject productItem : productItems) {
			Integer piId = productItem.getInteger("productItemId");
			Long updateTime = productItem.getLong("updateTime");
			try {
				Long lastCreateTime = (Long) this.getCacheWrapper()
						.getCacheMap("productItemCreate").get(piId);
				Long lastUpdateTime = (Long) this.getCacheWrapper()
						.getCacheMap("productItemUpdate").get(piId);
				if ((lastCreateTime != null && lastCreateTime >= updateTime)
						|| (lastUpdateTime != null && lastUpdateTime >= updateTime)) {
					this.getLogger().info(
							"productItemId: " + piId
									+ " has been updated by a newer");
					continue;
				}
			} catch (Exception e) {
				this.getLogger().error("", (Throwable) e);
			}
			piIds.add(piId);
			piId2ProductItem.put(piId, productItem);
		}

		if (!piIds.isEmpty()) {
			List<ProductItemInfoUpdateBean> piInfos = null;
			try {
				@SuppressWarnings("unchecked")
				WarehouseProductItemMappingIdDAO dao = (WarehouseProductItemMappingIdDAO) this
						.getDbAdapter().getDBI()
						.open(WarehouseProductItemMappingIdDAO.class);
				try {
					piInfos = dao.getPIInfoWhenUpdate(piIds);
				} finally {
					if (dao != null) {
						dao.close();
					}
				}
			} finally {

			}

			if (piInfos == null) {
				this.getLogger().error("piInfos == null");
				return null;
			}

			this.getLogger().debug("piInfos: " + piInfos);

			try {

				for (ProductItemInfoUpdateBean piInfo : piInfos) {
					PuObject productItem2 = piId2ProductItem.get(piInfo
							.getProductItemId());
					Integer id = productItem2.getInteger("productItemId");
					Integer productId = productItem2.getInteger("productId");
					Integer brandId = productItem2.getInteger("brandId");
					String brandName = productItem2.getString("brandName");
					Integer categoryId = productItem2.getInteger("categoryId");
					String barcode = productItem2.getString("barcode");
					String name = productItem2.getString("productItemName");
					Integer status = productItem2
							.getInteger("productItemStatus");
					Integer brandStatus = productItem2
							.getInteger("brandStatus");
					Integer categoryStatus = productItem2
							.getInteger("categoryStatus");
					Collection<Long> categoryPath = (Collection<Long>) productItem2
							.getLongArray("categoryPath");
					Long updateTime2 = productItem2.getLong("updateTime");
					String sCreateDate = productItem2.getString("createTime");
					Integer feshFoodType = productItem2
							.getInteger("freshFoodType");
					Double weight = productItem2.getDouble("weight");
					String categoryName = productItem2
							.getString("productItemName");
					Integer productItemType = productItem2
							.getInteger("productItemType");
					String image = productItem2.getString("image");

					if (id == null || productId == null || brandId == null
							|| brandName == null || categoryId == null
							|| barcode == null || name == null
							|| status == null || brandStatus == null
							|| categoryStatus == null || categoryPath == null
							|| updateTime2 == null) {
						this.getLogger().error("missing some fields");
					} else {
						BrandBean brandBean = (BrandBean) this
								.getCacheWrapper().getCacheMap("brand")
								.get(brandId);
						if (brandBean != null
								&& brandBean.getUpdateTime() >= updateTime2) {
							brandName = brandBean.getName();
							brandStatus = brandBean.getStatus();
						}
						CategoryBean categoryBean = (CategoryBean) this
								.getCacheWrapper().getCacheMap("category")
								.get(categoryId);
						if (categoryBean != null
								&& categoryBean.getUpdateTime() >= updateTime2) {
							categoryStatus = categoryBean.getStatus();
							categoryPath = new ArrayList<Long>();
							for (int catId : categoryBean.getPath()) {
								categoryPath.add((long) catId);
							}
						}
						List<AttributeAttributeValueVO> atts = null;
						Collection<PuObject> puAtts = (Collection<PuObject>) productItem2
								.getPuObjectArray("solrFeProductAttribute");
						if (puAtts != null) {
							atts = new ArrayList<AttributeAttributeValueVO>();
							for (PuObject puAtt : puAtts) {
								AttributeAttributeValueVO vo = new AttributeAttributeValueVO();
								vo.setAttributeId(puAtt
										.getInteger("attributeId"));
								vo.setAttributeName(puAtt
										.getString("attributeName"));
								if (vo.getAttributeName() == null) {
									throw new NullPointerException();
								}
								vo.setAttributeStatus(puAtt
										.getInteger("attributeStatus"));
								if (vo.getAttributeStatus() == null) {
									throw new NullPointerException();
								}
								vo.setAttributeValue(puAtt
										.getString("attributeValue"));
								if (vo.getAttributeValue() == null) {
									throw new NullPointerException();
								}
								vo.setAttributeValueId(puAtt
										.getInteger("attributeValueId"));
								if (vo.getAttributeValueId() == null) {
									throw new NullPointerException();
								}
								atts.add(vo);
							}
						}
						SolrInputDocument doc = this.updateSolrDocument(
								piInfo.getWhpiId(), id, productId, brandId,
								brandName, categoryId, barcode, name, status,
								feshFoodType, weight, sCreateDate, atts,
								brandStatus, categoryName, categoryStatus,
								categoryPath, productItemType, image);
						if (doc == null) {
							continue;
						}

						docs.add(doc);

					}
				}
			} finally {

			}
			return docs;
		}
		return null;
	}

	public List<SolrInputDocument> newProductItem(
			Collection<PuObject> productItems) throws Exception {
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		List<Integer> updatedWarehouseProductItemMappingIds = new ArrayList<Integer>();
		List<Long> updatedTimes = new ArrayList<Long>();
		for (PuObject productItem : productItems) {
			int productItemId = productItem.getInteger("productItemId");
			int productId = productItem.getInteger("productId");
			int brandId = productItem.getInteger("brandId");
			String brandName = productItem.getString("brandName");

			if (brandName == null) {
				throw new NullPointerException();
			}

			int categoryId = productItem.getInteger("categoryId");
			String barcode = productItem.getString("barcode");
			if (barcode == null) {
				throw new NullPointerException();
			}
			String productItemName = productItem.getString("productItemName");
			if (productItemName == null) {
				throw new NullPointerException();
			}
			int productItemStatus = productItem.getInteger("productItemStatus");
			int freshFoodType = productItem.getInteger("freshFoodType");
			double weight = productItem.getDouble("weight");
			String sCreateTime = productItem.getString("createTime");
			if (sCreateTime == null) {
				throw new NullPointerException();
			}
			int brandStatus = productItem.getInteger("brandStatus");
			String categoryName = productItem.getString("categoryName");
			int categoryStatus = productItem.getInteger("categoryStatus");
			List<Long> categoryPath = (List<Long>) productItem
					.getLongArray("categoryPath");
			if (categoryPath == null) {
				throw new NullPointerException();
			}
			int productItemType = productItem.getInteger("productItemType");
			String image = productItem.getString("image");
			Long updateTime = productItem.getLong("updateTime");
			if (updateTime == null) {
				this.getLogger().error(
						"updateTime of ProductItem: " + productId
								+ " is missing");
			} else {
				Collection<PuObject> puAtts = (Collection<PuObject>) productItem
						.getPuObjectArray("solrFeProductAttribute");
				List<AttributeAttributeValueVO> atts = new ArrayList<AttributeAttributeValueVO>();
				if (puAtts == null) {
					this.getLogger().debug("atts is null");
				} else {
					// Take the first attribute ? -- com.adr.bigdata.indexing.logic.impl.ProductItemUpdatedLogicProccessor line 189.
					Set<Integer> attIds = new HashSet<Integer>();
					for (PuObject puAtt : puAtts) {
						
						if (attIds.contains(puAtt.getInteger("attributeId")))
							continue;
						
						AttributeAttributeValueVO vo = new AttributeAttributeValueVO();
						vo.setAttributeId(puAtt.getInteger("attributeId"));
						vo.setAttributeName(puAtt.getString("attributeName"));
						if (vo.getAttributeName() == null) {
							throw new NullPointerException();
						}
						vo.setAttributeStatus(puAtt
								.getInteger("attributeStatus"));
						if (vo.getAttributeStatus() == null) {
							throw new NullPointerException();
						}
						vo.setAttributeValue(puAtt.getString("attributeValue"));
						if (vo.getAttributeValue() == null) {
							throw new NullPointerException();
						}
						vo.setAttributeValueId(puAtt
								.getInteger("attributeValueId"));
						if (vo.getAttributeValueId() == null) {
							throw new NullPointerException();
						}
						atts.add(vo);
						attIds.add(puAtt.getInteger("attributeId"));
					}
				}
				Collection<PuObject> puWarehouses = (Collection<PuObject>) productItem
						.getPuObjectArray("warehouseProductItemMapping");
				if (puWarehouses == null) {
					this.getLogger().error(
							"warehouseProductItemMappings of " + productItemId
									+ " is null");
				} else {
					List<ProductItemWarehouseProductItemMappingVO> whs = new ArrayList<ProductItemWarehouseProductItemMappingVO>();
					for (PuObject puWh : puWarehouses) {
						ProductItemWarehouseProductItemMappingVO vo2 = new ProductItemWarehouseProductItemMappingVO();
						vo2.setWarehouseProductItemMappingId(puWh
								.getInteger("warehouseProductItemMappingId"));
						vo2.setMerchantId(puWh.getInteger("merchantId"));
						if (vo2.getMerchantId() == null) {
							throw new NullPointerException();
						}
						vo2.setWarehouseId(puWh.getInteger("warehouseId"));
						if (vo2.getWarehouseId() == null) {
							throw new NullPointerException();
						}
						vo2.setMerchantName(puWh.getString("merchantName"));
						if (vo2.getMerchantName() == null) {
							throw new NullPointerException();
						}
						vo2.setMerchantSKU(puWh.getString("merchantSKU"));
						if (vo2.getMerchantSKU() == null) {
							throw new NullPointerException();
						}
						vo2.setOriginalPrice(puWh.getDouble("originalPrice"));
						if (vo2.getOriginalPrice() == null) {
							throw new NullPointerException();
						}
						vo2.setSellPrice(puWh.getDouble("sellPrice"));
						if (vo2.getSellPrice() == null) {
							throw new NullPointerException();
						}
						vo2.setQuantity(puWh.getInteger("quantity"));
						if (vo2.getQuantity() == null) {
							throw new NullPointerException();
						}
						vo2.setSafetyStock(puWh.getInteger("safetyStock"));
						if (vo2.getSafetyStock() == null) {
							throw new NullPointerException();
						}
						vo2.setMerchantProductItemStatus(puWh
								.getInteger("merchantProductItemStatus"));
						if (vo2.getMerchantProductItemStatus() == null) {
							throw new NullPointerException();
						}
						vo2.setMerchantStatus(puWh.getInteger("merchantStatus"));
						if (vo2.getMerchantStatus() == null) {
							throw new NullPointerException();
						}
						vo2.setWarehouseStatus(puWh
								.getInteger("warehouseStatus"));
						if (vo2.getWarehouseStatus() == null) {
							throw new NullPointerException();
						}
						vo2.setProvinceId(puWh.getInteger("provinceId"));
						if (vo2.getProvinceId() == null) {
							throw new NullPointerException();
						}
						int isVisible = puWh.getInteger("isVisible");
						vo2.setIsVisible(isVisible);
						whs.add(vo2);
					}
					for (ProductItemWarehouseProductItemMappingVO wh : whs) {
						SolrInputDocument doc = this.createSolrDocument(
								wh.getWarehouseProductItemMappingId(),
								productItemId, productId, brandId, brandName,
								categoryId, barcode, productItemName,
								productItemStatus, freshFoodType, weight,
								sCreateTime, atts, wh, brandStatus,
								categoryName, categoryStatus, categoryPath,
								productItemType, image, updateTime);
						if (doc != null) {
							docs.add(doc);
							updatedWarehouseProductItemMappingIds.add(wh
									.getWarehouseProductItemMappingId());
							updatedTimes.add(updateTime);

						}
					}
				}
			}
		}
		return docs;
	}

	private SolrInputDocument createSolrDocument(
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
		doc.addField("product_item_id_warehouse_id",
				(Object) warehouseProductItemMappingId);
		try {
			Long lastCreateTime = (Long) this.getCacheWrapper()
					.getCacheMap("productItemCreate").get(productItemId);
			if (lastCreateTime != null && lastCreateTime >= updateTime) {
				this.getLogger().info(
						"productItemId " + productItemId
								+ " has a newer CreateProductItem");
				return null;
			}
		} catch (Exception e) {
			this.getLogger().error("", (Throwable) e);
		}

		try {
			Long lastUpdateTime = (Long) this.getCacheWrapper()
					.getCacheMap("productItemUpdate").get(productItemId);
			if (lastUpdateTime != null && lastUpdateTime >= updateTime) {
				this.getLogger()
						.info("productItemId :"
								+ productItemId
								+ " has a newer UpdateProductItem, just update warehouseMapping");
			} else {
				BrandBean brandBean = (BrandBean) this.getCacheWrapper()
						.getCacheMap("brand").get(brandId);
				if (brandBean != null
						&& brandBean.getUpdateTime() >= updateTime) {
					brandName = brandBean.getName();
					brandStatus = brandBean.getStatus();
				}
				CategoryBean categoryBean = (CategoryBean) this
						.getCacheWrapper().getCacheMap("category")
						.get(categoryId);
				if (categoryBean != null
						&& categoryBean.getUpdateTime() >= updateTime) {
					categoryStatus = categoryBean.getStatus();
					categoryPath = new ArrayList<Long>();
					for (int catId : categoryBean.getPath()) {
						categoryPath.add((long) catId);
					}
				}
				doc.addField("product_id", (Object) productId);
				doc.addField("product_item_group", (Object) productId);
				doc.addField("product_item_id", (Object) productItemId);
				doc.addField("brand_id", (Object) brandId);
				// doc.addField("brand_id_facet", (Object) brandId);
				doc.addField("brand_name", (Object) brandName);
				doc.addField("is_brand_active", (Object) (brandStatus == 1));
				doc.addField("category_id", (Object) categoryId);
				// doc.addField("category_id_facet", (Object) categoryId);
				doc.addField("category_path", (Object) categoryPath);
				doc.addField("is_category_active",
						(Object) (categoryStatus == 1));
				doc.addField("barcode", (Object) barcode);
				doc.addField("product_item_name", (Object) productItemName);
				Date d = this.df.parse(createTime);
				doc.addField("create_time", (Object) d.getTime());
				/*
				 * doc.addField("is_product_item_active", (Object)
				 * (productItemStatus == 1));
				 */
				/*
				 * doc.addField("product_item_status", (Object)
				 * (productItemStatus == 1));
				 */
				doc.addField("product_item_status", (Object) productItemStatus);
				// doc.addField("fresh_food_type", (Object) freshFoodType);
				doc.addField("weight", (Object) weight);
				doc.addField("product_item_type", (Object) productItemType);
				doc.addField("image", (Object) image);

				/*
				 * if (atts != null && !atts.isEmpty()) { Set<Integer> set = new
				 * HashSet<Integer>(); for (AttributeAttributeValueVO att :
				 * atts) { if (set.contains(att.getAttributeId())) { continue; }
				 * Map<String, Object> fsValueId = new HashMap<String,
				 * Object>(); fsValueId.put("set", att.getAttributeValueId());
				 * Map<String, Object> fsValue = new HashMap<String, Object>();
				 * fsValue.put("set", att.getAttributeValue()); Map<String,
				 * Object> fsName = new HashMap<String, Object>();
				 * fsName.put("set", att.getAttributeName()); Map<String,
				 * Object> fsStatus = new HashMap<String, Object>();
				 * fsStatus.put("set", att.getAttributeStatus()); doc.addField(
				 * String.format("attr_%d_txt", att.getAttributeId()), (Object)
				 * fsValue); doc.addField( String.format("attr_name_%d_txt",
				 * att.getAttributeId()), (Object) fsName);
				 * doc.addField(String.format("attr_%d_i",
				 * att.getAttributeId()), (Object) fsValueId); doc.addField(
				 * String.format("attr_active_%d_b", att.getAttributeId()),
				 * (Object) fsStatus); try { Map<String, Object> fsValueD = new
				 * HashMap<String, Object>(); fsValueD.put("set",
				 * Double.parseDouble(att.getAttributeValue())); doc.addField(
				 * String.format("attr_%d_d", att.getAttributeId()), (Object)
				 * fsValueD); } catch (NumberFormatException ex) { }
				 * set.add(att.getAttributeId()); }
				 */

				if (atts != null && !atts.isEmpty()) {
					Set<Integer> set = new HashSet<Integer>();
					for (AttributeAttributeValueVO att : atts) {

						
						if (set.contains(att.getAttributeId()))
							{ continue; }
						 

						Map<String, Object> tmp = new HashMap<String, Object>();
						tmp.put("set", att.getAttributeValue());

						doc.addField(
								String.format(SolrFields.ATT_S,
										att.getAttributeId()), tmp);
						try {

							Map<String, Object> attMap = new HashMap<String, Object>();
							attMap.put("set",
									Double.parseDouble(att.getAttributeValue()));

							doc.addField(
									String.format(SolrFields.ATT_D,
											att.getAttributeId()), attMap);

						} catch (NumberFormatException e) {
						}
					}
				}
			}
		} catch (Exception e) {
			BrandBean brandBean = (BrandBean) this.getCacheWrapper()
					.getCacheMap("brand").get(brandId);
			if (brandBean != null && brandBean.getUpdateTime() >= updateTime) {
				brandName = brandBean.getName();
				brandStatus = brandBean.getStatus();
			}
			CategoryBean categoryBean = (CategoryBean) this.getCacheWrapper()
					.getCacheMap("category").get(categoryId);
			if (categoryBean != null
					&& categoryBean.getUpdateTime() >= updateTime) {
				categoryStatus = categoryBean.getStatus();
				categoryPath = new ArrayList<Long>();
				for (int catId : categoryBean.getPath()) {
					categoryPath.add((long) catId);
				}
			}
		}

		try {
			MerchantBean merchantBean = (MerchantBean) this.getCacheWrapper()
					.getCacheMap("merchant").get(wh.getMerchantId());
			if (merchantBean != null
					&& merchantBean.getUpdateTime() >= updateTime) {
				wh.setMerchantStatus(merchantBean.getStatus());
			}
			WarehouseBean warehouseBean = (WarehouseBean) this
					.getCacheWrapper().getCacheMap("warehouse")
					.get(wh.getWarehouseId());
			if (warehouseBean != null
					&& warehouseBean.getUpdateTime() >= updateTime) {
				wh.setWarehouseStatus(warehouseBean.getStatus());
			}
		} catch (Exception e) {
			this.getLogger().error("", (Throwable) e);
		}

		try {
			Long lastWarehouseProductItemMappingTime = (Long) this
					.getCacheWrapper()
					.getCacheMap("warehouseProductItemMapping")
					.get(warehouseProductItemMappingId);
			if (lastWarehouseProductItemMappingTime == null
					|| lastWarehouseProductItemMappingTime < updateTime) {
				doc.addField("merchant_product_item_sku",
						(Object) wh.getMerchantSKU());
				// doc.addField("original_price", (Object)
				// wh.getOriginalPrice());
				doc.addField("sell_price", (Object) wh.getSellPrice());
				doc.addField("merchant_product_item_status",
						(Object) wh.getMerchantProductItemStatus());
				doc.addField("quantity", (Object) wh.getQuantity());
				// doc.addField("safety_stock", (Object) wh.getSafetyStock());
				// doc.addField("is_safety_stock" ,(Object) (wh.getQuantity() >
				// wh.getSafetyStock()));
				doc.addField("visible",
						(Object) StatusesTool.getListBits(wh.getIsVisible()));

				doc.addField("on_site", (Object) wh.getIsVisible());
			}
		} catch (Exception e) {
			doc.addField("merchant_product_item_sku",
					(Object) wh.getMerchantSKU());
			// doc.addField("original_price", (Object) wh.getOriginalPrice());
			doc.addField("sell_price", (Object) wh.getSellPrice());
			doc.addField("merchant_product_item_status",
					(Object) wh.getMerchantProductItemStatus());
			doc.addField("quantity", (Object) wh.getQuantity());
			// doc.addField("safety_stock", (Object) wh.getSafetyStock());
			// doc.addField("is_safety_stock", (Object) (wh.getQuantity() >
			// wh.getSafetyStock()));
			doc.addField("visible",
					(Object) StatusesTool.getListBits(wh.getIsVisible()));
			doc.addField("on_site", (Object) wh.getIsVisible());
			this.getLogger().error("", (Throwable) e);
		}

		doc.addField("warehouse_id", (Object) wh.getWarehouseId());
		doc.addField("merchant_id", (Object) wh.getMerchantId());
		doc.addField("merchant_name", (Object) wh.getMerchantName());
		doc.addField("product_item_id_merchant_id",
				(Object) (String.valueOf(productItemId) + "_" + wh
						.getMerchantId()));
		doc.addField("is_merchant_active",
				(Object) (wh.getMerchantStatus() == 1));
		doc.addField("is_warehouse_active",
				(Object) (wh.getWarehouseStatus() == 1));

		if (productItemType == 2 || productItemType == 4) {

			Map<String, Object> fsValueId = new HashMap<String, Object>();
			fsValueId.put("set", Arrays.asList(wh.getProvinceId(), 0));
			doc.addField("received_city_id", fsValueId);

		} else {

			Map<String, Object> fsValueId = new HashMap<String, Object>();
			fsValueId.put("set", Arrays.asList(4, 8, 0));
			doc.addField("received_city_id", fsValueId);

		}

		// doc.addField("city_id", (Object) wh.getProvinceId());
		// doc.addField("city_id_facet", (Object) wh.getProvinceId());

		return doc;
	}

	private SolrInputDocument updateSolrDocument(
			int warehouseProductItemMappingId, Integer productItemId,
			Integer productId, Integer brandId, String brandName,
			Integer categoryId, String barcode, String productItemName,
			Integer productItemStatus, Integer freshFoodType, Double weight,
			String sCreateTime, List<AttributeAttributeValueVO> atts,
			Integer brandStatus, String categoryName, Integer categoryStatus,
			Collection<Long> categoryPath, Integer productItemType, String image)
			throws Exception {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("product_item_id_warehouse_id",
				(Object) warehouseProductItemMappingId);
		int count = 0;
		if (productId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productId);
			doc.addField("product_id", (Object) fs);
			doc.addField("product_item_group", (Object) fs);
			++count;
		}
		if (productItemId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemId);
			doc.addField("product_item_id", (Object) fs);
			++count;
		}
		if (brandId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandId);
			doc.addField("brand_id", (Object) fs);
			// doc.addField("brand_id_facet", (Object) fs);
			++count;
		}
		if (brandName != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandName);
			doc.addField("brand_name", (Object) fs);
			++count;
		}
		if (categoryId != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryId);
			doc.addField("category_id", (Object) fs);
			// doc.addField("category_id_facet", (Object) fs);
			++count;
		}
		if (categoryPath != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryPath);
			doc.addField("category_path", (Object) fs);
			++count;
		}
		if (barcode != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", barcode);
			doc.addField("barcode", (Object) fs);
			++count;
		}
		if (productItemName != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemName);
			doc.addField("product_item_name", (Object) fs);
			++count;
		}
		if (sCreateTime != null) {
			Date d = this.df.parse(sCreateTime);
			Map<String, Object> fs2 = new HashMap<String, Object>();
			fs2.put("set", d.getTime());
			doc.addField("create_time", (Object) fs2);
			++count;
		}
		if (categoryStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", categoryStatus == 1);
			doc.addField("is_category_active", (Object) fs);
			++count;
		}
		if (brandStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", brandStatus == 1);
			doc.addField("is_brand_active", (Object) fs);
			++count;
		}
		/*
		 * if (productItemStatus != null) { Map<String, Object> fs = new
		 * HashMap<String, Object>(); fs.put("set", productItemStatus == 1);
		 * doc.addField("is_product_item_active", (Object) fs); ++count; }
		 */

		if (productItemStatus != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemStatus);
			doc.addField("product_item_status", (Object) fs);
			++count;
		}

		/*
		 * if (freshFoodType != null) { Map<String, Object> fs = new
		 * HashMap<String, Object>(); fs.put("set", freshFoodType);
		 * doc.addField("fresh_food_type", (Object) fs); ++count; }
		 */

		if (weight != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", weight);
			doc.addField("weight", (Object) weight);
			++count;
		}
		if (productItemType != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", productItemType);
			doc.addField("product_item_type", (Object) fs);
			++count;
		}
		if (image != null) {
			Map<String, Object> fs = new HashMap<String, Object>();
			fs.put("set", image);
			doc.addField("image", (Object) fs);
			++count;
		}

		/*
		 * if (atts != null && !atts.isEmpty()) { Set<Integer> set = new
		 * HashSet<Integer>(); for (AttributeAttributeValueVO att : atts) { if
		 * (set.contains(att.getAttributeId())) { continue; } Map<String,
		 * Object> fsValueId = new HashMap<String, Object>();
		 * fsValueId.put("set", att.getAttributeValueId()); Map<String, Object>
		 * fsValue = new HashMap<String, Object>(); fsValue.put("set",
		 * att.getAttributeValue()); Map<String, Object> fsName = new
		 * HashMap<String, Object>(); fsName.put("set", att.getAttributeName());
		 * Map<String, Object> fsStatus = new HashMap<String, Object>();
		 * fsStatus.put("set", att.getAttributeStatus()); doc.addField(
		 * String.format("attr_%d_txt", att.getAttributeId()), (Object)
		 * fsValue); doc.addField( String.format("attr_name_%d_txt",
		 * att.getAttributeId()), (Object) fsName);
		 * doc.addField(String.format("attr_%d_i", att.getAttributeId()),
		 * (Object) fsValueId); doc.addField( String.format("attr_active_%d_b",
		 * att.getAttributeId()), (Object) fsStatus); try { Map<String, Object>
		 * fsValueD = new HashMap<String, Object>(); fsValueD.put("set",
		 * Double.parseDouble(att.getAttributeValue())); doc.addField(
		 * String.format("attr_%d_d", att.getAttributeId()), (Object) fsValueD);
		 * } catch (NumberFormatException ex) { } set.add(att.getAttributeId());
		 * ++count; } }
		 */

		if (atts != null && !atts.isEmpty()) {
			Set<Integer> set = new HashSet<Integer>();
			for (AttributeAttributeValueVO att : atts) {

				/*
				 * if (set.contains(att.getAttributeId())) { continue; }
				 */

				Map<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("set", att.getAttributeValue());

				doc.addField(
						String.format(SolrFields.ATT_S, att.getAttributeId()),
						tmp);

				try {

					Map<String, Object> attMap = new HashMap<String, Object>();
					attMap.put("set",
							Double.parseDouble(att.getAttributeValue()));

					doc.addField(
							String.format(SolrFields.ATT_D,
									att.getAttributeId()), attMap);

				} catch (NumberFormatException e) {
				}
			}
		}

		if (count == 0) {
			return null;
		}
		return doc;
	}
}
