package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.Serializable;

import org.apache.spark.SparkConf;

public final class DocGenerationJobFactory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static DocGenerationJob getDocGenerationJob(int type) {
		switch (type) {
		case 1:
			return new BrandDocGenerationJob();

		case 2:
			return new CategoryDocGenerationJob();

		case 3:
			//return new AttributeDocGenerationJob();
			throw new IllegalArgumentException("Type AttributeDocGenerationJob no longer supported " + type);
			
		case 4:
			return new AttributeValueDocGenerationJob();

		case 5:
			return new MerchantDocGenerationJob();

		case 6:
			return new WarehouseDocGenerationJob();

		case 7:
			return new PromotionDocGenerationJob();
			
		case 8:
			return new PromotionProductItemMappingDocGenerationJob();

		case 9:
			return new WarehouseProductItemMappingDocGenerationJob();

		case 10:
			return new ProductItemDocGenerationJob();

		default:
			throw new IllegalArgumentException("Type not supported " + type);

		}
	}

}
