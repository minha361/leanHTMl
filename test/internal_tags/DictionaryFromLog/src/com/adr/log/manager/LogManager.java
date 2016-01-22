package com.adr.log.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adr.extractlog.ProductItemGetter;
import com.adr.extractlog.KeywordProduct;
import com.adr.extractlog.bean.LogBean;
import com.adr.extractlog.bean.ProductItemBean;

public class LogManager {
	private static final String ADR_SEARCH = "adrSearch%sadrPIID%d";
	
	private Set<Long> procuctItemIds = new HashSet<Long>();
	
	private Map<Long, ProductItemBean> productItems = new HashMap<Long, ProductItemBean>(); // productItemId => productItem
	
	private Map<String, KeywordProduct> keyWordProducts = new HashMap<String, KeywordProduct>();
	
	private static LogManager logManager;
	private LogManager(){
		
	}
	
	public static LogManager getInstance(){
		if(logManager == null){
			logManager = new LogManager();
		}
		return logManager;
	}
	
	
	public void addLogs(List<LogBean> logBeans){
		for(LogBean logBean : logBeans){
			procuctItemIds.add(logBean.getProductItemId());
			KeywordProduct keyWordProduct = getKeyWordProduct(logBean);
			if(keyWordProduct != null){
				keyWordProduct.addOne(logBean);
			} else {
				keyWordProduct = new KeywordProduct(logBean.getKeyWord(), logBean.getProductItemId(),  logBean.getProductItemName());
				keyWordProduct.addOne(logBean);
				addKeyWordProduct(keyWordProduct);
			}
		}
	}
	
	public KeywordProduct getKeyWordProduct(LogBean logBean){
		return keyWordProducts.get(getKeyMapOfKeyWordProduct(logBean.getKeyWord(), logBean.getProductItemId()));
	}
	
	private void addKeyWordProduct(KeywordProduct keyWordProduct){
		keyWordProducts.put(getKeyMapOfKeyWordProduct(keyWordProduct.getKeyWord(), keyWordProduct.getProductItemId()), keyWordProduct);
	}
	
	
	private String getKeyMapOfKeyWordProduct(String keyWord, long productItemId){
		String hashString = String.format(ADR_SEARCH, keyWord, productItemId);
		return hashString;
	}
	
	/**
	 * get TagJson of products from sqlServer
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public void loadProductItemInfor() throws IOException, SQLException{
		ProductItemGetter getProductItem = new ProductItemGetter();
		List<Long> productItemIdList = (new ArrayList<Long>());
		productItemIdList.addAll(procuctItemIds);
		getProductItem.setLogBeans(productItemIdList);
		ConfigManager configManager = ConfigManager.getIntence();
		int totalPage = productItemIdList.size()/configManager.getProductItemPerPage();
		for(int i = 0; i <= totalPage; i++){
			List<ProductItemBean> productItemBeans = getProductItem.getProducts(i);
			for(ProductItemBean productItemBean : productItemBeans){
				 addProductItem(productItemBean);
			}
		}
		System.out.println("total Product: " + productItems.size());
	}

	private void addProductItem(ProductItemBean productItemBean){
		productItems.put(productItemBean.getProductItemId(), productItemBean);
	}
	
	public ProductItemBean getProductBeanOfKeyWordProduct(KeywordProduct keyWordProduct){
		return productItems.get(keyWordProduct.getProductItemId());
	}
	
	public void exportAllLog() throws SQLException, IOException, ClassNotFoundException{
		KeywordProductManager keywordProductManager = KeywordProductManager.getInstance();
//		for(KeywordProduct keywordProduct : keyWordProducts.values()){
//			keywordProductManager.insertNew(keywordProduct);
//		}
		keywordProductManager.prepareTable();
		keywordProductManager.insertAll(keyWordProducts.values());
	}
}
