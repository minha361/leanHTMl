package com.adr.log.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

	private static ConfigManager configManager;
	private String dbUrl;
	private String dbUser;
	private String dbPass;
	private int docsPerPage;

	private String productItemDbUrl;
	private String productItemDbUser;
	private String productItemDbPass;
	private int productItemPerPage;

	private ConfigManager() throws IOException {
		Properties prop = new Properties();
		FileInputStream configInput = null;
		try {
			configInput = new FileInputStream("../conf/extract.conf");
			prop.load(configInput);
			// set the properties value
			dbUrl = prop.getProperty("dbUrl");
			dbUser = prop.getProperty("dbUser");
			dbPass = prop.getProperty("dbPass");
			docsPerPage = Integer.valueOf(prop.getProperty("docsPerPage"));
			
			productItemDbUrl = prop.getProperty("productItemDbUrl");
			productItemDbUser = prop.getProperty("productItemDbUser");
			productItemDbPass = prop.getProperty("productItemDbPass");
			productItemPerPage = Integer.valueOf(prop.getProperty("productItemPerPage"));
			
			
			// save properties to project root folder
		} finally {
			if (configInput != null) {
				try {
					configInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static final ConfigManager getIntence() throws IOException {
		if (configManager == null) {
			configManager = new ConfigManager();
		}
		return configManager;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public int getDocsPerPage() {
		return docsPerPage;
	}

	public void setDocsPerPage(int docsPerPage) {
		this.docsPerPage = docsPerPage;
	}

	public String getProductItemDbUrl() {
		return productItemDbUrl;
	}

	public void setProductItemDbUrl(String productItemDbUrl) {
		this.productItemDbUrl = productItemDbUrl;
	}

	public String getProductItemDbUser() {
		return productItemDbUser;
	}

	public void setProductItemDbUser(String productItemDbUser) {
		this.productItemDbUser = productItemDbUser;
	}

	public String getProductItemDbPass() {
		return productItemDbPass;
	}

	public void setProductItemDbPass(String productItemDbPass) {
		this.productItemDbPass = productItemDbPass;
	}

	public int getProductItemPerPage() {
		return productItemPerPage;
	}
	
	
}
