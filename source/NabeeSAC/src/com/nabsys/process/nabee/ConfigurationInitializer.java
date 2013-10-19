package com.nabsys.process.nabee;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.resource.ServerConfiguration;

public class ConfigurationInitializer {

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	protected DBPoolManager initConfigRepository(String port) throws NoSuchAlgorithmException, SQLException
	{
		logger.info(0x009C);
		Connection connection = null;
		DBPoolManager pool = new DBPoolManager();
		pool.setPoolName("CONFIG");
		pool.setDBDriver("org.hsqldb.jdbc.JDBCDriver");
		pool.setDBUrl("jdbc:hsqldb:hsql://localhost:"+port+"/NABEE");	
		pool.setDriverUrl("jdbc:apache:commons:dbcp:");
		pool.setUser("nabeeconfigdatabase");
		pool.setPassword("#!nabeeConfigDatabase#!");
		pool.setMaxPoolActive(ServerConfiguration.getMaxClientNum() / 2<=0?2:ServerConfiguration.getMaxClientNum() / 2);
		pool.setPoolIdle(ServerConfiguration.getMaxClientNum() / 4<=0?1:ServerConfiguration.getMaxClientNum() / 4);
		pool.setPoolMaxWait(2000);
		pool.setIsAutoCommit(false);
		pool.setIsReadOnly(false);
		
		pool.initConnectionPool();
		connection = pool.getConnection();
		logger.info(0x009D);
		
		initializeConfiguration(connection);
		
		return pool;
	}
	
	private void initializeConfiguration(Connection connection) throws NoSuchAlgorithmException, SQLException
	{
		try{
			initInstance(connection);
			initUsers(connection);
			initTelegram(connection);
			initComponent(connection);
			initService(connection);
			initSQL(connection);
			initInstanceExt(connection);
		}finally{
			connection.close();
		}
	}
	
	private void initInstanceExt(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.USER_ACCESS_LIST(INSTANCE VARCHAR(50) NOT NULL,USER_ID VARCHAR(50) NOT NULL,FOREIGN KEY(INSTANCE) REFERENCES INSTANCE(ID),FOREIGN KEY(USER_ID) REFERENCES USER(ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.PLUGIN_LIST(INSTANCE VARCHAR(50) NOT NULL,PLUGIN_NAME VARCHAR(100) NOT NULL,FOREIGN KEY(INSTANCE) REFERENCES INSTANCE(ID))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	
	private void initInstance(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.INSTANCE(ID VARCHAR(50) NOT NULL,SYSTEM_HEADER_ID VARCHAR(50),USE_TELEGRAM_CACHE CHAR(5),USE_COMPONENT_CACHE CHAR(5),USE_QUERY_CACHE CHAR(5),USE_SERVICE_CACHE CHAR(5),SERVICE_PORT INTEGER,BUFFER_SIZE INTEGER,READ_TIMEOUT INTEGER,MAX_CLIENTS INTEGER,SERVER_ENCODING VARCHAR(30),TIME_LOCALE VARCHAR(30),CLASS_PATH VARCHAR(2000),JAVA_HOME VARCHAR(300),SYSTEM_PATH VARCHAR(300),SYSTEM_ENCODING VARCHAR(30),FILE_ENCODING VARCHAR(30),LOG_CONFIG_PATH VARCHAR(300),LOAD_ON_STARTUP CHAR(5),EXTRA_LOAD_PARAMS VARCHAR(300),LENGTH_FIELD_ID VARCHAR(50),ID_FIELD_ID VARCHAR(50),PRIMARY KEY(ID))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	
	private void initTelegram(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.TELEGRAM(INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL,NAME VARCHAR(50) NOT NULL,HEADER_ID VARCHAR(50),LOG_LEVEL CHAR(1) NOT NULL,REMARK VARCHAR(200),PRIMARY KEY(INSTANCE, ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.TELEGRAM_FIELDS(INSTANCE VARCHAR(50) NOT NULL,TELEGRAM_ID VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL,NAME VARCHAR(50) NOT NULL,INDEX INTEGER NOT NULL, LENGTH INTEGER NOT NULL,MANDATORY CHAR(5) NOT NULL,PADDING CHAR(1) NOT NULL,ALIGN CHAR(1) NOT NULL,TYPE CHAR(1) NOT NULL,REMARK VARCHAR(200),PRIMARY KEY(INSTANCE, TELEGRAM_ID, ID),FOREIGN KEY(INSTANCE, TELEGRAM_ID) REFERENCES TELEGRAM(INSTANCE, ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.TELEGRAM_H(INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL,MODIFY_TIME TIMESTAMP NOT NULL,NAME VARCHAR(50) NOT NULL,HEADER_ID VARCHAR(50),LOG_LEVEL CHAR(1) NOT NULL,REMARK VARCHAR(200),PRIMARY KEY(INSTANCE, ID, MODIFY_TIME))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.TELEGRAM_FIELDS_H(INSTANCE VARCHAR(50) NOT NULL,TELEGRAM_ID VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL, MODIFY_TIME TIMESTAMP NOT NULL,NAME VARCHAR(50) NOT NULL,INDEX INTEGER NOT NULL, LENGTH INTEGER NOT NULL,MANDATORY CHAR(5) NOT NULL,PADDING CHAR(1) NOT NULL,ALIGN CHAR(1) NOT NULL,TYPE CHAR(1) NOT NULL,REMARK VARCHAR(200),PRIMARY KEY(INSTANCE, TELEGRAM_ID, MODIFY_TIME, ID),FOREIGN KEY(INSTANCE, TELEGRAM_ID, MODIFY_TIME) REFERENCES TELEGRAM_H(INSTANCE, ID, MODIFY_TIME))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	
	private void initSQL(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS  PUBLIC.SQL_STORAGE (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(300) NOT NULL,LEVEL INTEGER NOT NULL, TYPE VARCHAR(10) NOT NULL,NEED_PARAM CHAR(5) NOT NULL, CONTENTS VARCHAR(10000),TAG_DOCUMENT BLOB ,PRIMARY KEY(INSTANCE, ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS  PUBLIC.SQL_STORAGE_H (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(300) NOT NULL,MODIFY_TIME TIMESTAMP NOT NULL,LEVEL INTEGER NOT NULL, TYPE VARCHAR(10) NOT NULL,NEED_PARAM CHAR(5) NOT NULL,CONTENTS VARCHAR(10000),PRIMARY KEY(INSTANCE, ID, MODIFY_TIME))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	
	private void initComponent(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.COMPONENT (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL,NAME VARCHAR(100) NOT NULL,CLASS_NAME VARCHAR(200) NOT NULL,FILE_PATH VARCHAR(300) NOT NULL,SHARE CHAR(5) NOT NULL,PRIMARY KEY(INSTANCE,ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.COMPONENT_H (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(50) NOT NULL,MODIFY_TIME TIMESTAMP NOT NULL,NAME VARCHAR(100) NOT NULL,CLASS_NAME VARCHAR(200) NOT NULL,FILE_PATH VARCHAR(300) NOT NULL,SHARE CHAR(5) NOT NULL,PRIMARY KEY(INSTANCE,ID,MODIFY_TIME))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	private void initService(Connection connection) throws SQLException
	{
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.SERVICE (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(100) NOT NULL,NAME VARCHAR(100) NOT NULL,TYPE VARCHAR(20) NOT NULL,REMARK VARCHAR(200) NOT NULL,SERVICE_DESIGN BLOB,ACTIVATE CHAR(5) NOT NULL,PRIMARY KEY(INSTANCE, ID))");
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS  PUBLIC.SERVICE_H (INSTANCE VARCHAR(50) NOT NULL,ID VARCHAR(100) NOT NULL,MODIFY_TIME TIMESTAMP NOT NULL,NAME VARCHAR(100) NOT NULL,TYPE VARCHAR(20) NOT NULL,REMARK VARCHAR(200) NOT NULL,SERVICE_DESIGN BLOB,ACTIVATE CHAR(5) NOT NULL,PRIMARY KEY(INSTANCE, ID, MODIFY_TIME))");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
	
	private void initUsers(Connection connection) throws NoSuchAlgorithmException, SQLException
	{
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("CREATE CACHED TABLE IF NOT EXISTS PUBLIC.USER (ID VARCHAR(50) NOT NULL PRIMARY KEY, NAME VARCHAR(50) NOT NULL, PW VARCHAR(100) NOT NULL,AUTH VARCHAR(20),PHONE VARCHAR(20),ACTIVATION CHAR(5))");
			
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USER WHERE ID = 'Admin'");
			rs.next();
			if(rs.getInt(1) <= 0)
			{
				stmt.executeUpdate("INSERT INTO PUBLIC.USER VALUES('Admin', '"+NLabel.get(0x0027)+"', '"+Hash.getMD5Hash(Hash.getMD5Hash("Admin"))+"', 'Admin', '', 'true')");
			}
			
			stmt.executeUpdate("DELETE FROM PUBLIC.USER WHERE AUTH= 'Instance'");
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			stmt.close();
		}
	}
}
