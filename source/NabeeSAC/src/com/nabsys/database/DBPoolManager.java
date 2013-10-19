package com.nabsys.database;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.process.ResourceFactory;

public class DBPoolManager{

	private String poolName = null;
	private String dbDriver = null;
	private String dbUrl = null;
	private String user = null;
	private String driverUrl = null;
	private String password = null;
	private int maxPoolActive = 0;
	private int poolIdle = 0;
	private int poolMaxWait = 0;
	private boolean isAutoCommit = false;
	private boolean isReadOnly = false;
	private ResourceFactory resourceFactory = null;
	
	public DBPoolManager()
	{
	}
	
	public DBPoolManager(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public void setResourceFactory(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public void setPoolName(String poolName)
	{
		this.poolName = poolName;
	}
	
	public void setDBDriver(String dbDriver)
	{
		this.dbDriver = dbDriver;
	}
	
	public void setDBUrl(String dbUrl)
	{
		this.dbUrl = dbUrl;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setDriverUrl(String driverUrl)
	{
		this.driverUrl = driverUrl;
	}
	
	public void setMaxPoolActive(int maxPoolActive)
	{
		this.maxPoolActive = maxPoolActive;
	}
	
	public void setPoolIdle(int poolIdle)
	{
		this.poolIdle = poolIdle;
	}
	
	public void setPoolMaxWait(int poolMaxWait)
	{
		this.poolMaxWait = poolMaxWait;
	}
	
	public void setIsAutoCommit(boolean isAutoCommit)
	{
		this.isAutoCommit = isAutoCommit;
	}
	
	public void setIsReadOnly(boolean isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}
	
	public void initConnectionPool() throws SQLException
	{
		if(poolName == null) throw new SQLException(NLabel.get(0x0065));
		if(dbDriver == null) throw new SQLException(NLabel.get(0x0066));
		if(dbUrl == null) throw new SQLException(NLabel.get(0x0067));
		if(user == null) throw new SQLException(NLabel.get(0x0068));
		if(driverUrl == null) throw new SQLException(NLabel.get(0x0069));
		if(password == null) throw new SQLException(NLabel.get(0x006A));
		if(maxPoolActive == 0) throw new SQLException(NLabel.get(0x006B));
		if(poolIdle == 0) throw new SQLException(NLabel.get(0x006C));
		if(poolMaxWait == 0) throw new SQLException(NLabel.get(0x006D));
			
		BasicDataSource  bds = new BasicDataSource();
		
		bds.setDriverClassName(dbDriver);
		bds.setUrl(dbUrl);
		bds.setUsername(user);
		bds.setPassword(password);
		bds.setMaxActive(maxPoolActive);
		bds.setMaxIdle(poolIdle);
		bds.setMaxWait(poolMaxWait);
		bds.setDefaultAutoCommit(isAutoCommit);
		bds.setDefaultReadOnly(isReadOnly);
		
		ObjectPool connectionPool = new GenericObjectPool(null);
		ConnectionFactory  connectionFactory = new DataSourceConnectionFactory(bds);
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, isAutoCommit);
		PoolingDriver driver = new PoolingDriver();
		driver.registerPool(poolName, connectionPool);
		
		Connection connection = getConnection();
		connection.close();
	}
	
	private final NLogger logger = NLogger.getLogger(this.getClass());
		
	public synchronized Connection getConnection() throws SQLException
	{
		
		try {
			Connection connection = new Connection(DriverManager.getConnection(driverUrl + poolName), resourceFactory);

			return connection;
		} catch (SQLException e) {
			logger.error(e, e.getMessage());
			logger.info("Retrying database connection to " + poolName + ".");
			BasicDataSource  bds = new BasicDataSource();
			
			bds.setDriverClassName(dbDriver);
			bds.setUrl(dbUrl);
			bds.setUsername(user);
			bds.setPassword(password);
			bds.setMaxActive(maxPoolActive);
			bds.setMaxIdle(poolIdle);
			bds.setMaxWait(poolMaxWait);
			bds.setDefaultAutoCommit(isAutoCommit);
			bds.setDefaultReadOnly(isReadOnly);
			
			ObjectPool connectionPool = new GenericObjectPool(null);
			ConnectionFactory  connectionFactory = new DataSourceConnectionFactory(bds);
			new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, isAutoCommit);
			PoolingDriver driver = new PoolingDriver();
			driver.registerPool(poolName, connectionPool);

			return new Connection(DriverManager.getConnection(driverUrl + poolName));
		}
	}

	public void closePool() throws SQLException {
		PoolingDriver driver = null;
		try {
			driver = (PoolingDriver)DriverManager.getDriver(driverUrl);
			driver.closePool(poolName);
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		
	}
	

}
