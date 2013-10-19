package com.nabsys.process.instance;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.database.DBPoolManager;

public class ConfigurationInitializer {
	
	private String logConfigFilePath = null;
	
	public DBPoolManager initConfigRepository(String port, String instanceName) throws NoSuchAlgorithmException, ClassNotFoundException, SQLException
	{
		Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		DBPoolManager pool = null;
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:"+port+"/NABEE", "nabeeconfigdatabase", "#!nabeeConfigDatabase#!");
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM INSTANCE WHERE ID = '"+instanceName+"'");
			if(!rs.next()) throw new NotFoundException();
			
			logConfigFilePath = rs.getString("LOG_CONFIG_PATH");
			
			pool = new DBPoolManager();
			pool.setPoolName("CONFIG");
			pool.setDBDriver("org.hsqldb.jdbc.JDBCDriver");
			pool.setDBUrl("jdbc:hsqldb:hsql://localhost:"+port+"/NABEE");	
			pool.setDriverUrl("jdbc:apache:commons:dbcp:");
			pool.setUser("nabeeconfigdatabase");
			pool.setPassword("#!nabeeConfigDatabase#!");
			pool.setMaxPoolActive(rs.getInt("MAX_CLIENTS"));
			pool.setPoolIdle(rs.getInt("MAX_CLIENTS")/ 4<=0?1:rs.getInt("MAX_CLIENTS") / 4);
			pool.setPoolMaxWait(2000);
			pool.setIsAutoCommit(false);
			pool.setIsReadOnly(true);
			
			pool.initConnectionPool();
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return pool;
	}
	
	public String getLogConfigurationFile()
	{
		return logConfigFilePath;
	}
}
