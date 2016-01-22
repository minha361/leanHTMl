package com.adr.extractlog;

import com.adr.extractlog.bean.LogBean;
import com.adr.extractlog.bean.ProductItemBean;
import com.adr.log.manager.LogManager;

public class GetProductItemTest {
	public static void main(String[] args) {
		ProductItemGetter getProductItem = new ProductItemGetter();
		String formatString = getProductItem.formatTagJson("[{\"Id\":28,\"Name\":\"áo\"},{\"Id\":29,\"Name\":\"quần\"},{\"Id\":30,\"Name\":\"Giày\"},{\"Id\":31,\"Name\":\"dép\"},{\"Id\":32,\"Name\":\"nồi\"}]");
		System.out.println(formatString);
		
		
		GetProductItemTest getProductItemTest = new GetProductItemTest();
		getProductItemTest.formatTest();
	}
	
	public void formatTest(){
		LogBean logBean = new LogBean();
		KeywordProduct keyWordProduct = new KeywordProduct(logBean.getKeyWord(), logBean.getProductItemId(),  logBean.getProductItemName());//LogManager.getInstance().getKeyWordProduct(logBean);
		ProductItemBean productItemBean = new ProductItemBean();
		String formatString = "chao%d";
		System.out.println(String.format(formatString, 123l));
		
		String INSERT_KEYWORD_PRODUCT_SQL = "INSERT INTO `user_search`.`keyword_product` (`keyword`, `product_item_id`, `product_name`, `tags`, `brand_name`, `category_name`, `category_id`, `category_path`, `count`) VALUES ('%s', '%d', '%s', '%s', '%s', '%s', '%s', '%s', '%d');";
		String outPut = String.format(INSERT_KEYWORD_PRODUCT_SQL, keyWordProduct.getKeyWord(), keyWordProduct.getProductItemId(), keyWordProduct.getProductName(), productItemBean.getTagJson(), keyWordProduct.getBrandsJson(), keyWordProduct.getCategoryNamesJson(), keyWordProduct.getCategoryIdsJson(), keyWordProduct.getCategoryPathsJson(), keyWordProduct.getSearchAndClickCount());
		System.out.println(outPut);
	}
	
}
