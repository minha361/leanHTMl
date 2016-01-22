package com.adr.log.manager;

import java.io.IOException;
import java.sql.*;
import java.util.Collection;

import com.adr.extractlog.KeywordProduct;
import com.adr.extractlog.bean.ProductItemBean;
import com.adr.extractlog.util.DBConnectionUtil;

public class KeywordProductManager {
	// private static final String INSERT_KEYWORD_PRODUCT_SQL =
	// "INSERT INTO `user_search`.`keyword_product` (`keyword`, `product_item_id`, `product_name`, `tags`, `brand_name`, `category_name`, `category_id`, `category_path`, `count`) VALUES ('%s', '%d', '%s', '%s', '%s', '%s', '%s', '%s', '%d');";
	private static final String INSERT_KEYWORD_PRODUCT_SQL = "INSERT INTO keyword_product (keyword, product_item_id, product_name, tags, brand_name, category_name, category_id, category_path, count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String TRUNCATE_SQL = "TRUNCATE `keyword_product`";
	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS keyword_product ( `id` INT(8) NULL DEFAULT NULL AUTO_INCREMENT, `keyword` VARCHAR(1000) NULL DEFAULT NULL, `product_item_id` INT(8) NULL DEFAULT NULL, `product_name` VARCHAR(1000) NULL DEFAULT NULL, `tags` VARCHAR(5000) NULL DEFAULT NULL, `brand_name` VARCHAR(1000) NULL DEFAULT NULL, `category_name` VARCHAR(1000) NULL DEFAULT NULL, `category_id` VARCHAR(5000) NULL DEFAULT NULL, `category_path` VARCHAR(5000) NULL DEFAULT NULL, `count` INT(11) NULL DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
	
	private static final long MAX_ITEM_INSERT = 100000;
	private KeywordProductManager() {

	}

	private static KeywordProductManager keywordProductManager;

	public static KeywordProductManager getInstance() {
		if (keywordProductManager == null) {
			keywordProductManager = new KeywordProductManager();
		}
		return keywordProductManager;
	}

	public void insertNew(KeywordProduct keyWordProduct) throws SQLException, IOException, ClassNotFoundException {
		ProductItemBean productItemBean = LogManager.getInstance().getProductBeanOfKeyWordProduct(keyWordProduct);
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		try {
			conn = DBConnectionUtil.getMySQLConnection();
			preparedStmt = conn.prepareStatement(INSERT_KEYWORD_PRODUCT_SQL);
			preparedStmt.setString(1, keyWordProduct.getKeyWord());
			preparedStmt.setLong(2, keyWordProduct.getProductItemId());
			preparedStmt.setString(3, keyWordProduct.getProductName());

			if (productItemBean != null) {
				preparedStmt.setString(4, productItemBean.getTagJson());
			} else {
				preparedStmt.setString(4, null);
			}

			preparedStmt.setString(5, keyWordProduct.getBrandsJson());
			preparedStmt.setString(6, keyWordProduct.getCategoryNamesJson());
			preparedStmt.setString(7, keyWordProduct.getCategoryIdsJson());
			preparedStmt.setString(8, keyWordProduct.getCategoryPathsJson());
			preparedStmt.setInt(9, keyWordProduct.getSearchAndClickCount());
			// execute the preparedstatement
			preparedStmt.executeUpdate();
		} finally {
			// finally block used to close resources
			try {
				if (preparedStmt != null)
					preparedStmt.close();
			} catch (SQLException se) {
			}
		}
	}

	public void insertAll(Collection<KeywordProduct> keywordProducts) throws SQLException, IOException, ClassNotFoundException {
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		long i = 0;
		try {
			conn = DBConnectionUtil.getMySQLConnection();
			preparedStmt = conn.prepareStatement(INSERT_KEYWORD_PRODUCT_SQL);
			for (KeywordProduct keyWordProduct : keywordProducts) {
				ProductItemBean productItemBean = LogManager.getInstance().getProductBeanOfKeyWordProduct(keyWordProduct);

				preparedStmt.setString(1, keyWordProduct.getKeyWord());
				preparedStmt.setLong(2, keyWordProduct.getProductItemId());
				preparedStmt.setString(3, keyWordProduct.getProductName());

				if (productItemBean != null) {
					preparedStmt.setString(4, productItemBean.getTagJson());
				} else {
					preparedStmt.setString(4, null);
				}

				preparedStmt.setString(5, keyWordProduct.getBrandsJson());
				preparedStmt.setString(6, keyWordProduct.getCategoryNamesJson());
				preparedStmt.setString(7, keyWordProduct.getCategoryIdsJson());
				preparedStmt.setString(8, keyWordProduct.getCategoryPathsJson());
				preparedStmt.setInt(9, keyWordProduct.getSearchAndClickCount());
				// execute the preparedstatement
				preparedStmt.addBatch();
				i++;

				if (i % MAX_ITEM_INSERT == 0 || i == keywordProducts.size()) {
					preparedStmt.executeBatch(); // Execute every 1000 items.
//					try {
//						if (preparedStmt != null)
//							preparedStmt.close();
//					} catch (SQLException se) {
//					}
//					preparedStmt = conn.prepareStatement(INSERT_KEYWORD_PRODUCT_SQL);
				}
				// preparedStmt.executeUpdate();
			}

		} finally {
			// finally block used to close resources
			try {
				if (preparedStmt != null)
					preparedStmt.close();
			} catch (SQLException se) {
			}
		}

	}
	public void prepareTable() throws ClassNotFoundException, IOException, SQLException{
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		PreparedStatement truncateStmt = null;
		try {
			conn = DBConnectionUtil.getMySQLConnection();
			preparedStmt = conn.prepareStatement(CREATE_TABLE_SQL);
			preparedStmt.execute();
			
			truncateStmt = conn.prepareStatement(TRUNCATE_SQL);
			truncateStmt.execute();
		} finally {
			// finally block used to close resources
			try {
				if (preparedStmt != null)
					preparedStmt.close();
			} catch (SQLException se) {
			}
			try {
				if (truncateStmt != null)
					truncateStmt.close();
			} catch (SQLException se) {
			}
		}
	}
}
