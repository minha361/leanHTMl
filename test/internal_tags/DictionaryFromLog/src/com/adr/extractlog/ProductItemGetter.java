package com.adr.extractlog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.adr.extractlog.bean.ProductItemBean;
import com.adr.extractlog.util.DBConnectionUtil;
import com.adr.log.manager.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProductItemGetter {
	// private static final String GET_PRODUCT_PATTERN =
	// "SELECT * FROM ProductItem WHERE Id in (2816, 2817, 2818, 2819)";
	private static final String GET_PRODUCT_PATTERN = "SELECT Id, TagJson FROM ProductItem WHERE Id in (%s)";
	private List<Long> productItemIds;

	public void setLogBeans(List<Long> productItemIds) {
		this.productItemIds = productItemIds;
	}

	public List<ProductItemBean> getProducts(int pageNumber) throws IOException, SQLException {
		List<ProductItemBean> productItemBeans = new ArrayList<ProductItemBean>();
		Connection conn = DBConnectionUtil.getSQLServerConnection();
		Statement statement = null;
		try {
			String joinProductItemIds = joinProductItemIds(productItemIds, ", ", pageNumber);
			statement = conn.createStatement();
			String query = String.format(GET_PRODUCT_PATTERN, joinProductItemIds);
			long now = System.currentTimeMillis();
			ResultSet resultSet = statement.executeQuery(query);
			System.out.println("get 1 product's page: " + (System.currentTimeMillis() - now));
			while (resultSet.next()) {
				ProductItemBean productItemBean = new ProductItemBean();
				productItemBean.setProductItemId(resultSet.getLong("Id"));
				productItemBean.setTagJson(formatTagJson(resultSet.getString("TagJson")));
				productItemBeans.add(productItemBean);
//				System.out.println(productItemBean.getProductItemId() + ", " + productItemBean.getTagJson());
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
		return productItemBeans;
	}

	private String joinProductItemIds(List<Long> productItemIds, String conjunction, int pageNumber) throws IOException {
		ConfigManager configManager = ConfigManager.getIntence();
		long startPoint = pageNumber * configManager.getProductItemPerPage();
		long endPoint = startPoint + configManager.getProductItemPerPage();
		long index = 0;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Long item : productItemIds) {
			if (startPoint <= index && index < endPoint) {
				if (first) {
					first = false;
				} else {
					sb.append(conjunction);
				}
				sb.append(item);
			}
			index++;
		}
		return sb.toString();
	}

	/**
	 * convert Tag's Json object in sqlserver to Json Array
	 * 
	 * @param originalString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String formatTagJson(String originalString) {
		// [{"Id":28,"Name":"áo"},{"Id":29,"Name":"quần"},{"Id":30,"Name":"Giày"},{"Id":31,"Name":"dép"},{"Id":32,"Name":"nồi"}]
		
		Gson g = new Gson();
		if (originalString == null || originalString.length() == 0) {
			return null;
		} else {
			Type listType = new TypeToken<List<TagProductItem>>(){}.getType();
			List<TagProductItem> posts = (List<TagProductItem>) g.fromJson(originalString, listType);
			Set<String> tagValues = new HashSet<String>();
		
			for(int i = 0; i < posts.size(); i++){
				TagProductItem aItem = posts.get(i);
				tagValues.add(aItem.Name);
			}
			return g.toJson(tagValues);
		}
	}
	
	private class TagProductItem{
		String Name;
	}

	
}
