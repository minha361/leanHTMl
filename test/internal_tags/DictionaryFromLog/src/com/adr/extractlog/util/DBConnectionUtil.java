package com.adr.extractlog.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.adr.log.manager.ConfigManager;

public class DBConnectionUtil {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static Connection mySQLConn;
	private static Connection sqlServerConn;
	public static Connection getSQLServerConnection() throws SQLException, IOException {
		ConfigManager configManager = ConfigManager.getIntence();
		if (sqlServerConn == null) {
			String dbURL = configManager.getProductItemDbUrl();
			String user = configManager.getProductItemDbUser();
			String pass = configManager.getProductItemDbPass();
			System.out.println("Connecting to SQLServer database...");
			sqlServerConn = DriverManager.getConnection(dbURL, user, pass);
		}
		return sqlServerConn;
	}
	
	public static Connection getMySQLConnection() throws IOException, ClassNotFoundException, SQLException{
		if(mySQLConn == null){
			ConfigManager configManager = ConfigManager.getIntence();
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// STEP 3: Open a connection
			System.out.println("Connecting to MySQL database...");
			mySQLConn = DriverManager.getConnection(configManager.getDbUrl(), configManager.getDbUser(), configManager.getDbPass());
		}
		return mySQLConn;
	}

}
