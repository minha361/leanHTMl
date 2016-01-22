package com.adr.extractlog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.adr.extractlog.util.DBConnectionUtil;
import com.adr.extractlog.util.VietnameseUtil;
import com.adr.log.manager.ConfigManager;

public class BrandGetter {
	// private static final String GET_PRODUCT_PATTERN =
		// "SELECT * FROM ProductItem WHERE Id in (2816, 2817, 2818, 2819)";
		private static final String GET_BRAND_PATTERN = "SELECT BrandName FROM Brand ORDER BY id OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";

		private boolean hasMore = true;
		public void getAllBrands() throws IOException, SQLException{
			
		}
		
		public Set<String> getBrands(int pageNumber) throws IOException, SQLException {
			Set<String> brands = new HashSet<String>();
			Connection conn = DBConnectionUtil.getSQLServerConnection();
			Statement statement = null;
			try {
				ConfigManager configManager = ConfigManager.getIntence();
				int maxItemPerPage = configManager.getProductItemPerPage();
				statement = conn.createStatement();
				String query = String.format(GET_BRAND_PATTERN, maxItemPerPage * pageNumber, maxItemPerPage);
				long now = System.currentTimeMillis();
				ResultSet resultSet = statement.executeQuery(query);
				System.out.println("get 1 brand's page: " + (System.currentTimeMillis() - now));
				int docNumber = 0;
				while (resultSet.next()) {
					docNumber++;
					String brandName = resultSet.getString("BrandName");
					
					brandName = brandName.replaceAll("\\+", " ");
					brandName = brandName.replaceAll("-", " ");
					brandName = brandName.replaceAll("/", " ");
					brandName = brandName.toLowerCase();
					brandName = brandName.trim();
					while(brandName.contains("  ")){
						brandName = brandName.replace("  ", " ");
					}
					
					brandName = VietnameseUtil.unAccent(brandName);
					brands.add(brandName);
				}
				
				if(docNumber < maxItemPerPage){
					hasMore = false;
				} else {
					hasMore = true;
				}

				try {
					resultSet.close();
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			return brands;
		}

		public boolean isHasMore() {
			return hasMore;
		}
}
