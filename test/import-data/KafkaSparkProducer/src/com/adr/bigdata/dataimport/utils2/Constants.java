package com.adr.bigdata.dataimport.utils2;

public class Constants {

	//public static final String SPARK_MASTER = "spark://10.220.75.134:7077";
	public static final String SOLR_DOC_TOPIC = "dataimport";
	public static final String SEP = "___";
	public static final String SPARK_MASTER = "spark://10.220.75.79:7077";
	public static final String COLLECTION = "commit_policy_product";
	public static final String BATCH_SIZE = "100";
	//public static final String SOLR_CLOUD_ZKHOST = "10.220.75.80:7574,10.220.75.80:8985,10.220.75.80:8987";
	public static final String SOLR_CLOUD_ZKHOST = "10.220.75.80:9093,10.220.75.81:9093,10.220.75.82:9093";

	public static final String BRAND_TOPIC = "brand";
	public static final String MERCHANT_TOPIC = "merchant";
	public static final String CATEGORY_TOPIC = "category";
	public static final String ATT_TOPIC = "att";
	public static final String ATT_VALUE_TOPIC = "attValue";
	public static final String WAREHOUSE_TOPIC = "warehouse";
	public static final String PROMOTION_TOPIC = "promotion";
	public static final String PROMOTION_PRODUCTITEM_TOPIC = "promotion_product_item";
	public static final String WAREHOUSE_PRODUCTITEM_TOPIC = "warehouse_product_item";
	public static final String PRODUCT_ITEM_TOPIC = "product_item";

/*	public enum TOPIC_TYPE {
		BRAND_TOPIC(0), MERCHANT_TOPIC(1), CATEGORY_TOPIC(2), ATT_TOPIC(3), ATT_VALUE_TOPIC(
				4), WAREHOUSE_TOPIC(5), PROMOTION_TOPIC(6), PROMOTION_PRODUCTITEM_TOPIC(
				7), WAREHOUSE_PRODUCTITEM_TOPIC(8), PRODUCT_ITEM_TOPIC(9);

		private int code;

		private TOPIC_TYPE(int i) {
			code = i;
		}

		public int getCode() {
			return code;
		}
	}*/

	public enum TOPIC_TYPE {
		brand(0), merchant(1), category(2), att(3), attValue(
				4), warehouse(5), promotion(6), promotion_product_item(
				7), warehouse_product_item(8),product_item(9);

		private int code;

		private TOPIC_TYPE(int i) {
			code = i;
		}

		public int getCode() {
			return code;
		}
	}
	
	public static void main(String[] args) {
		String topic = "merchant";
		System.out.println(topic + " : "
				+ Constants.TOPIC_TYPE.valueOf(topic).getCode());

	}
}
