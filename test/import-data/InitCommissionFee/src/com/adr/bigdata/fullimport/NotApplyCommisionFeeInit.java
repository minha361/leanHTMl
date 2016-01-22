package com.adr.bigdata.fullimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;

import com.adr.bigdata.fullimport.solr.model.UpdateSolrCommissionModel;
import com.adr.bigdata.fullimport.sql.bean.CommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.CommissionFeeForInitBean;
import com.adr.bigdata.fullimport.sql.bean.NotApplyCommisionFeeBean;
import com.adr.bigdata.fullimport.sql.bean.model.CommissionFeeModel;
import com.adr.bigdata.fullimport.sql.dao.CommissionFeeDAO;
import com.adr.bigdata.fullimport.sql.mapper.CommissionFeeForInitMapper;
import com.adr.bigdata.fullimport.sql.mapper.SQLMapper;
import com.nhb.common.utils.Initializer;

public class NotApplyCommisionFeeInit {
	private static String URL = "jdbc:sqlserver://%s:%s;databaseName=%s";

	public static void main(String[] args) throws Exception {
		Initializer.bootstrap(NotApplyCommisionFeeInit.class);

		String dbHost = System.getProperty("dbHost", "10.220.75.25");
		String dbPort = System.getProperty("dbPort", "1433");
		String dbName = System.getProperty("dbName", "Adayroi_CategoryManagement_ezibuy");
		String dbUser = System.getProperty("dbUser", "adruserfortest");
		String dbPass = System.getProperty("dbPass", "adruserfortest@qaz");
		String solrLink = System.getProperty("solrLink", "http://10.220.75.133:8983/solr/product");

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		long start = System.currentTimeMillis();
		try (Connection conn = DriverManager.getConnection(String.format(URL, dbHost, dbPort, dbName), dbUser, dbPass);
				Statement stmt = conn.createStatement()) {
			CommissionFeeModel commissionFeeModel = new CommissionFeeModel();
			commissionFeeModel.setStmt(stmt);
			commissionFeeModel.loadData();
			System.out.println("\t\ttime get cache: " + (System.currentTimeMillis() - start));
			try (ConcurrentUpdateSolrClient solrClient = new ConcurrentUpdateSolrClient(solrLink, 20000, 8)) {
				UpdateSolrCommissionModel updateModel = new UpdateSolrCommissionModel();
				updateModel.setSolrClient(solrClient);
				try (ResultSet rs = stmt.executeQuery(CommissionFeeDAO.sqlCommissionForInit)) {
					SQLMapper<CommissionFeeForInitBean> mapper = new CommissionFeeForInitMapper();
					int count = 0;
					while (rs.next()) {
						CommissionFeeForInitBean wpm = mapper.map(0, rs);
						Object[] os = commissionFeeModel.getCommisionFee(wpm.getMerchantId(), wpm.getProductItemId(),
								wpm.getCategoryId(), wpm.getBrandId());
						CommisionFeeBean commisionFeeBean = (CommisionFeeBean) os[0];
						NotApplyCommisionFeeBean notApplyCommisionFeeBean = (NotApplyCommisionFeeBean) os[1];
						if (commisionFeeBean == null || notApplyCommisionFeeBean == null) {
							continue;
						}
						updateModel.add(wpm, commisionFeeBean, notApplyCommisionFeeBean);
						count++;
					}
					System.out.println("\ttotal warehouseMapping not apply commission fee: " + count);
				}
				solrClient.close();
			}
		}
		System.out.println("\ttotal time execute: " + (System.currentTimeMillis() - start));
	}
}
