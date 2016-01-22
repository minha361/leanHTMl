package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.ProductItemModel;
import com.adr.bigdata.indexing.db.sql.vos.AttributeAttributeValueVO;
import com.adr.bigdata.indexing.db.sql.vos.ProductItemWarehouseProductItemMappingVO;
import com.nhb.common.data.PuObject;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.PuObjectToProductItemWarehouseProductItemMappingVOConverter;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.ProductItemSolrDocCreator;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.PuObjectToAttributeValueVOConverter;

public class ProductItemDocGenerationJobV2 extends DocGenerationBase {

	private static final long serialVersionUID = -7190155509434650120L;

	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws Exception {
		byteArrayToPuObject();
		Collection<PuObject> list = getPuObject().getPuObjectArray(
				APIFields.LIST);
		PuObject firstItem = (PuObject) list.iterator().next();
		if (firstItem.getPuObjectArray(ProductItemModel.PI_WH_PI_MAPPING) != null)
			return jsc.parallelize(generateNewDocs(jsc));
		else
			return jsc.parallelize(updateDocs(jsc));
	}

	public List<SolrInputDocument> generateNewDocs(JavaSparkContext jsc) {
		Collection<PuObject> col = getPuObject().getPuObjectArray(
				APIFields.LIST);
		JavaRDD<List<SolrInputDocument>> docs = jsc.parallelize(
				new ArrayList<PuObject>(col)).map(
				new Function<PuObject, List<SolrInputDocument>>() {

					private static final long serialVersionUID = 1L;

					@Override
					public List<SolrInputDocument> call(PuObject productItem)
							throws Exception {
						int productItemId = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_ID);
						int productId = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_PRODUCT_ID);
						int brandId = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_BRAND_ID);
						String brandName = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_BRAND_NAME);
						if (brandName == null)
							throw new NullPointerException();

						int categoryId = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_CAT_ID);
						String barcode = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_BARCODE);
						if (barcode == null)
							throw new NullPointerException();

						String productItemName = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_PRODUCT_ITEM_NAME);
						if (productItemName == null)
							throw new NullPointerException();

						int productItemStatus = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_PRODUCT_ITEM_STATUS);
						int freshFoodType = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_FRESH_FOOD_TYPE);
						double weight = productItem
								.getDouble(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_WEIGHT);
						String sCreateTime = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_CREATE_TIME);
						if (sCreateTime == null)
							throw new NullPointerException();

						int brandStatus = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_BRAND_STATUS);
						String categoryName = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_CATEGORY_NAME);
						int categoryStatus = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_CATEGORY_STATUS);
						List<Long> categoryPath = (List<Long>) productItem
								.getLongArray(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_CATEGORY_PATH);
						if (categoryPath == null)
							throw new NullPointerException();
						int productItemType = productItem
								.getInteger(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_PRODUCT_ITEM_TYPE);
						String image = productItem
								.getString(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_IMAGE);

						Long updateTime = productItem
								.getLong(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_UPDATE_TIME);
						if (updateTime == null) {
							getLogger().error(
									"updateTime of ProductItem: " + productId
											+ " is missing");
							return null;
						}

						/* Get Attributes */
						Collection<PuObject> puAtts = productItem
								.getPuObjectArray(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_SOLR_FE_PRODUCT_ATTRIBUTE);
						List<AttributeAttributeValueVO> atts = new ArrayList<AttributeAttributeValueVO>();
						if (puAtts == null) {
							getLogger().debug("atts is null");
						} else {
							for (PuObject puAtt : puAtts) {
								atts.add(PuObjectToAttributeValueVOConverter
										.convert(puAtt));
							}
						}
						/* End: Get Attributes */

						/* Get warehouses */
						Collection<PuObject> puWarehouses = productItem
								.getPuObjectArray(com.adr.bigdata.indexing.db.sql.models.ProductItemModel.PI_WH_PI_MAPPING);
						if (puWarehouses == null) {
							getLogger().error(
									"warehouseProductItemMappings of "
											+ productItemId + " is null");
							return null;
						}

						List<ProductItemWarehouseProductItemMappingVO> whs = new ArrayList<ProductItemWarehouseProductItemMappingVO>();
						for (PuObject puWh : puWarehouses) {
							whs.add(PuObjectToProductItemWarehouseProductItemMappingVOConverter
									.convert(puWh));
						}
						/* End Get Warehouse */

						List<SolrInputDocument> op = new ArrayList<SolrInputDocument>();
						ProductItemSolrDocCreator creator = new ProductItemSolrDocCreator();
						for (ProductItemWarehouseProductItemMappingVO wh : whs) {
							op.add(creator.createSolrDocument(
									wh.getWarehouseProductItemMappingId(),
									productItemId, productId, brandId,
									brandName, categoryId, barcode,
									productItemName, productItemStatus,
									freshFoodType, weight, sCreateTime, atts,
									wh, brandStatus, categoryName,
									categoryStatus, categoryPath,
									productItemType, image, updateTime));
						}

						return op;
					}

				});
		List<List<SolrInputDocument>> ld = docs.collect();
		List<SolrInputDocument> rdocs = new ArrayList<SolrInputDocument>();
		for (List<SolrInputDocument> ls : ld)
			rdocs.addAll(ls);
		return rdocs;
	}

	public List<SolrInputDocument> updateDocs(JavaSparkContext jsc)
			throws Exception {
		byteArrayToPuObject();
		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		Collection<PuObject> productItems = getPuObject().getPuObjectArray(
				APIFields.LIST);
		List<Integer> piIds = new ArrayList<Integer>();
		Map<Integer, PuObject> piId2ProductItem = new HashMap<Integer, PuObject>();
		for (PuObject productItem : productItems) {
			Integer piId = productItem.getInteger(ProductItemModel.PI_ID);
			Long updateTime = productItem
					.getLong(ProductItemModel.PI_UPDATE_TIME);
			/* Check to cache */
			if (getCheckingUpdateTime()) {
				try {
					Long lastCreateTime = (Long) getCacheWrapper().getCacheMap(
							CacheFields.PRODUCT_ITEM_CREATE).get(piId);
					Long lastUpdateTime = (Long) getCacheWrapper().getCacheMap(
							CacheFields.PRODUCT_ITEM_UPDATE).get(piId);
					if ((lastCreateTime != null && lastCreateTime >= updateTime)
							|| (lastUpdateTime != null && lastUpdateTime >= updateTime)) {
						getLogger().info(
								"productItemId: " + piId
										+ " has been updated by a newer");
						continue;
					}
				} catch (Exception e) {
					getLogger().error("", e);
				}
			}
			/* End: Check to cache */
			piIds.add(piId);
			piId2ProductItem.put(piId, productItem);
		}

		if (piIds.isEmpty())
			return null;
		List<ProductItemInfoUpdateBean> piInfos = null;
		try (WarehouseProductItemMappingIdDAO dao = getDbAdapter().getDBI()
				.open(WarehouseProductItemMappingIdDAO.class)) {
			piInfos = dao.getPIInfoWhenUpdate(piIds);
		}
		if (piInfos == null) {
			getLogger().error("piInfos == null");
			return null;
		}
		getLogger().debug("piInfos: " + piInfos);

		for (ProductItemInfoUpdateBean piInfo : piInfos) {
			PuObject productItem = piId2ProductItem.get(piInfo
					.getProductItemId());

			Integer id = productItem.getInteger(ProductItemModel.PI_ID);
			Integer productId = productItem
					.getInteger(ProductItemModel.PI_PRODUCT_ID);
			Integer brandId = productItem
					.getInteger(ProductItemModel.PI_BRAND_ID);
			String brandName = productItem
					.getString(ProductItemModel.PI_BRAND_NAME);
			Integer categoryId = productItem
					.getInteger(ProductItemModel.PI_CAT_ID);
			String barcode = productItem.getString(ProductItemModel.PI_BARCODE);
			String name = productItem
					.getString(ProductItemModel.PI_PRODUCT_ITEM_NAME);
			Integer status = productItem
					.getInteger(ProductItemModel.PI_PRODUCT_ITEM_STATUS);
			Integer brandStatus = productItem
					.getInteger(ProductItemModel.PI_BRAND_STATUS);
			Integer categoryStatus = productItem
					.getInteger(ProductItemModel.PI_CATEGORY_STATUS);
			Collection<Long> categoryPath = productItem
					.getLongArray(ProductItemModel.PI_CATEGORY_PATH);
			Long updateTime = productItem
					.getLong(ProductItemModel.PI_UPDATE_TIME);
			String sCreateDate = productItem
					.getString(ProductItemModel.PI_CREATE_TIME);
			Integer feshFoodType = productItem
					.getInteger(ProductItemModel.PI_FRESH_FOOD_TYPE);
			Double weight = productItem.getDouble(ProductItemModel.PI_WEIGHT);
			String categoryName = productItem
					.getString(ProductItemModel.PI_PRODUCT_ITEM_NAME);
			Integer productItemType = productItem
					.getInteger(ProductItemModel.PI_PRODUCT_ITEM_TYPE);
			String image = productItem.getString(ProductItemModel.PI_IMAGE);

			if (id == null || productId == null || brandId == null
					|| brandName == null || categoryId == null
					|| barcode == null || name == null || status == null
					|| brandStatus == null || categoryStatus == null
					|| categoryPath == null || updateTime == null) {
				getLogger().error("missing some fields");
				continue;
			}

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

			/* Get Attributes */
			List<AttributeAttributeValueVO> atts = null;
			Collection<PuObject> puAtts = productItem
					.getPuObjectArray(ProductItemModel.PI_SOLR_FE_PRODUCT_ATTRIBUTE);
			ProductItemSolrDocCreator creator = new ProductItemSolrDocCreator();
			if (puAtts != null) {
				// FIXME update time for attribute
				atts = new ArrayList<AttributeAttributeValueVO>();
				for (PuObject puAtt : puAtts) {
					atts.add(PuObjectToAttributeValueVOConverter.convert(puAtt));
				}
				SolrInputDocument doc = creator.updateSolrDocument(
						piInfo.getWhpiId(), id, productId, brandId, brandName,
						categoryId, barcode, name, status, feshFoodType,
						weight, sCreateDate, atts, brandStatus, categoryName,
						categoryStatus, categoryPath, productItemType, image);
				if (doc != null)
					docs.add(doc);
				else
					continue;
			}
		}
		return docs;
	}
	// End: Get all ProductItemIds to get info

}
