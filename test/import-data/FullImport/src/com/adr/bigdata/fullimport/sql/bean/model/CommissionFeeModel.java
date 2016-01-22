package com.adr.bigdata.fullimport.sql.bean.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.adr.bigdata.fullimport.sql.bean.CategoryBean;
import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;
import com.adr.bigdata.fullimport.sql.dao.CategoryDAO;
import com.adr.bigdata.fullimport.sql.dao.CommissionFeeDAO;
import com.adr.bigdata.fullimport.sql.dao.NotApplyCommisionFeeDAO;

public class CommissionFeeModel extends AbstractSQLModel {
	private Map<String, CommisionFeeBean> productItem2Commision = new HashMap<>(); // mcId_productItemId
	private Map<String, CommisionFeeBean> category2Commision = new HashMap<>(); // mcId_categoryId
	private Map<String, CommisionFeeBean> brand2Commision = new HashMap<>(); // mcId_brandId
	private Map<Integer, CategoryBean> catMap;
	private Map<String, NotApplyCommisionFeeBean> category2NotCommission = new HashMap<>(); // mcId_category_id

	public void loadData() throws InstantiationException, IllegalAccessException, SQLException {
		CommissionFeeDAO dao = new CommissionFeeDAO();
		dao.setStmt(stmt);
		for (CommisionFeeBean bean : dao.allCommissionFee()) {
			String key = bean.getMerchantId() + "_" + bean.getEntityId();
			if (bean.getTypeId() == 1) {
				productItem2Commision.put(key, bean);
			} else if (bean.getTypeId() == 2) {
				brand2Commision.put(key, bean);
			} else if (bean.getTypeId() == 3) {
				category2Commision.put(key, bean);
			}
		}
		CategoryDAO catDAO = new CategoryDAO();
		catDAO.setStmt(stmt);
		catMap = catDAO.allCategory();
		NotApplyCommisionFeeDAO notApplyCommisionFeeDAO = new NotApplyCommisionFeeDAO();
		notApplyCommisionFeeDAO.setStmt(stmt);
		for (NotApplyCommisionFeeBean bean : notApplyCommisionFeeDAO.allNotApplyCommissionFee()) {
			category2NotCommission.put(bean.getMerchantId() + "_" + bean.getCategoryId(), bean);
		}
	}

	public Object[] getCommisionFee(int merchantId, int productItemId, int categoryId, int brandId) {
		CommisionFeeBean c = null;

		if (productItem2Commision.containsKey(merchantId + "_" + productItemId)) {
			c = productItem2Commision.get(merchantId + "_" + productItemId);
		} else if (brand2Commision.containsKey(merchantId + "_" + brandId)) {
			c = brand2Commision.get(merchantId + "_" + brandId);
		} else {
			int catId = 0;
			if (!catMap.containsKey(categoryId)) {
				return new Object[]{null, null};
			}
			int[] path = catMap.get(categoryId).getPath();
			for (int i = 0; i < path.length; i++) {
				catId = path[i];
				if (category2Commision.containsKey(merchantId + "_" + catId)) {
					break;
				}
			}
			if (catId != 0) {
				c = category2Commision.get(catId);
			}
		}

		NotApplyCommisionFeeBean n = null;
		if (category2NotCommission.containsKey(merchantId + "_" + categoryId)) {
			n = category2NotCommission.get(merchantId + "_" + categoryId);
		}

		return new Object[] { c, n };
	}

	public Map<Integer, CategoryBean> getCatMap() {
		return catMap;
	}

}
