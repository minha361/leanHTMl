package com.adr.bigdata.dataimport.logic.impl.procjob;

import java.io.Serializable;

public final class ProcessingJobFactory implements Serializable {

	private static final long serialVersionUID = 3085978010150020275L;

	public static ProcessingJob getProcessingJob(int type) {
		switch (type) {
		case 1:
			return new BrandProcessingJob();
		/*case 2:
			return new CategoryProcessingJob();
		case 3:
			return new AttributeProcessingJob();
		case 4:
			return new AttributeValueProcessingJob();
		case 5:
			return new MerchantProcessingJob();
		case 6:
			return new WarehouseProcessingJob();
		case 7:
			return new PromotionProcessingJob();
		case 8:
			return new PromotionProductItemMappingProcessingJob();*/
		case 9:
			return new WarehouseProductItemMappingProcessingJob();
/*		case 10:
			return new ProductItemProcessingJob();
*/
		default:
			throw new IllegalArgumentException("Type not supported " + type);

		}
	}
}
