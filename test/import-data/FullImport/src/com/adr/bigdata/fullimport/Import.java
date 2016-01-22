package com.adr.bigdata.fullimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.fullimport.redis.RdAppConfig;
import com.adr.bigdata.fullimport.redis.RedisAppContextAdapter;
import com.adr.bigdata.fullimport.redis.model.FullUpdateRedisModel;
import com.adr.bigdata.fullimport.sql.bean.AttributeMappingBean;
import com.adr.bigdata.fullimport.sql.bean.CategoryBean;
import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.LandingPageBean;
import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.WarehouseProductItemMappingBean;
import com.adr.bigdata.fullimport.sql.bean.model.CommissionFeeModel;
import com.adr.bigdata.fullimport.sql.bean.model.ProductModel;
import com.adr.bigdata.fullimport.sql.mapper.SQLMapper;
import com.adr.bigdata.fullimport.sql.mapper.WarehouseProductItemMapper;

public class Import {
	private static String URL = "jdbc:sqlserver://%s:%s;databaseName=%s";

	public static void main(String[] args) throws Exception {
		Initializer.bootstrap(Import.class);
		
		String dbHost = System.getProperty("dbHost", "10.220.75.25");
		String dbPort = System.getProperty("dbPort", "1433");
		String dbName = System.getProperty("dbName", "Adayroi_CategoryManagement_ezibuy");
		String dbUser = System.getProperty("dbUser", "adruserfortest");
		String dbPass = System.getProperty("dbPass", "adruserfortest@qaz");
		int redisPort = Integer.parseInt(System.getProperty("redisPort", "8983"));
		String redisHost = System.getProperty("redisHost", "10.220.75.78");
		int redisDB = Integer.parseInt(System.getProperty("redisDB", "2"));

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		long start = System.currentTimeMillis();
		try (Connection conn = DriverManager.getConnection(String.format(URL, dbHost, dbPort, dbName), dbUser, dbPass);
				Statement stmt = conn.createStatement()) {

			CommissionFeeModel commissionFeeModel = new CommissionFeeModel();
			commissionFeeModel.setStmt(stmt);
			commissionFeeModel.loadData();
			Map<Integer, CategoryBean> allCats = commissionFeeModel.getCatMap();

			ProductModel productModel = new ProductModel();
			productModel.setStmt(stmt);
			Map<Integer, List<AttributeMappingBean>> allAtts = productModel.allAtts();
			Map<Integer, Integer> allProductViews = productModel.allProductViews();
			Map<Integer, List<LandingPageBean>> allLandingPages = productModel.allLandingPages();

			RedisAppContextAdapter adapter = RedisAppContextAdapter.getInstance(new RdAppConfig(redisPort, redisHost));
			FullUpdateRedisModel updateModel = adapter.getRdCacheModel(FullUpdateRedisModel.class, redisDB);

			System.out.println("start read warehouse_mapping and import: " + (System.currentTimeMillis() - start));
			try (ResultSet rs = stmt.executeQuery("[Product_Solr_Linda]")) {
				SQLMapper<WarehouseProductItemMappingBean> mapper = new WarehouseProductItemMapper();

				int count = 0;
				while (rs.next()) {
					WarehouseProductItemMappingBean bean = mapper.map(0, rs);
					Object[] os = commissionFeeModel.getCommisionFee(bean.getMerchantId(), bean.getProductItemId(),
							bean.getCategoryId(), bean.getBrandId());
					CommisionFeeBean commisionFeeBean = (CommisionFeeBean) os[0];
					NotApplyCommisionFeeBean notApplyCommisionFeeBean = (NotApplyCommisionFeeBean) os[1];
					updateModel.add(bean, allAtts.get(bean.getProductItemId()), allCats.get(bean.getCategoryId()),
							commisionFeeBean, allProductViews.get(bean.getProductItemId()), notApplyCommisionFeeBean,
							allLandingPages.get(bean.getProductItemWarehouseId()));
					count++;
					if (count % 50000 == 0) {
						updateModel.sync();
						System.out.println("time: " + (System.currentTimeMillis() - start));
						System.out.println("count: " + count);
					}
				}

				System.out.println("time: " + (System.currentTimeMillis() - start));
				System.out.println("count: " + count);
			}
			updateModel.sync();
			updateModel.close();
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - start));
	}

}
