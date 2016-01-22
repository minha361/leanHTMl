package com.adr.bigdata.dataimport.logic.impl.docgen;

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
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.adr.bigdata.indexing.APIFields;
import com.adr.bigdata.indexing.CacheFields;
import com.adr.bigdata.indexing.SolrFields;
import com.adr.bigdata.indexing.db.sql.beans.BrandBean;
import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;
import com.adr.bigdata.indexing.db.sql.beans.MerchantBean;
import com.adr.bigdata.indexing.db.sql.beans.ProductItemInfoUpdateBean;
import com.adr.bigdata.indexing.db.sql.beans.WarehouseBean;
import com.adr.bigdata.indexing.db.sql.daos.WarehouseProductItemMappingIdDAO;
import com.adr.bigdata.indexing.db.sql.models.BrandModel;
import com.adr.bigdata.indexing.db.sql.models.CategoryModel;
import com.adr.bigdata.indexing.db.sql.models.MerchantModel;
import com.adr.bigdata.indexing.db.sql.models.ProductItemModel;
import com.adr.bigdata.indexing.db.sql.models.WarehouseModel;
import com.adr.bigdata.indexing.db.sql.models.WarehouseProductItemMappingModel;
import com.adr.bigdata.indexing.db.sql.vos.AttributeAttributeValueVO;
import com.adr.bigdata.indexing.db.sql.vos.ProductItemWarehouseProductItemMappingVO;
import com.adr.bigdata.indexing.utils.StatusesTool;
import com.mario.consumer.cache.CacheWrapper;
import com.nhb.common.data.PuObject;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.PuObjectToProductItemWarehouseProductItemMappingVOConverter;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.ProductItemSolrDocCreator;
import com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc.PuObjectToAttributeValueVOConverter;
import com.adr.bigdata.dataimport.system.ExternalSources;

//import com.adr.bigdata.indexing.vos.ProductItemVO;


public class ProductItemDocGenerationJob extends DocGenerationBase {

	private static final long serialVersionUID = -7190155509434650120L;
	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext jsc)
			throws Exception {
		
		byteArrayToPuObject();
		Collection<PuObject> list = getPuObject().getPuObjectArray(
				APIFields.LIST);
		PuObject firstItem = (PuObject) list.iterator().next();
	
		if (firstItem.getPuObjectArray(ProductItemModel.PI_WH_PI_MAPPING) != null)
			return jsc.parallelize(generateNewDocs());
		else
			return jsc.parallelize(updateDocs());
	}

	public List<SolrInputDocument> generateNewDocs() throws Exception {
		ProductItemDocGenDummy dummy = new ProductItemDocGenDummy();
		dummy.setCacheWrapper(this.getCacheWrapper());
		dummy.setDbAdapter(this.getDbAdapter());
		Collection<PuObject> productItems = getPuObject().getPuObjectArray(
				APIFields.LIST);
		return dummy.newProductItem(productItems);
	}
	
	public List<SolrInputDocument> updateDocs() throws Exception {
		ProductItemDocGenDummy dummy = new ProductItemDocGenDummy();
		dummy.setCacheWrapper(this.getCacheWrapper());
		dummy.setDbAdapter(this.getDbAdapter());
		Collection<PuObject> productItems = getPuObject().getPuObjectArray(
				APIFields.LIST);
		return dummy.updateProductItem(productItems);
	}
	
}
