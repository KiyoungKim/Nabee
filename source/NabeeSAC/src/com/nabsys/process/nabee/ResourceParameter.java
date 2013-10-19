package com.nabsys.process.nabee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.resource.InstanceContext;
import com.nabsys.resource.UserContext;

public class ResourceParameter {
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	final private AccessMap 		accessMap				= new AccessMap();
	private DBPoolManager 			configDBPool			= null;
	private String					databasePort			= null;
	public ResourceParameter(DBPoolManager configDBPool, String databasePort){
		this.configDBPool = configDBPool;
		this.databasePort = databasePort;
	}
	
	public DBPoolManager getConfigDBPool()
	{
		return this.configDBPool;
	}
	
	public String getConfigDBPort()
	{
		return this.databasePort;
	}
	
	public ThreadControler getThreadControler(long key) {
		return this.accessMap.get(key);
	}
	
	public boolean containsThreadControler(long key)
	{
		if(this.accessMap.containsKey(key)) return true;
		else return false;
	}

	public void setThreadControler(long key, ThreadControler threadControler) {
		this.accessMap.put(key, threadControler);
	}
	
	public void addInstanceThread() {
		this.accessMap.addInstanceThread();
	}
	
	public void removeThreadControler(long key, boolean isInstance)
	{
		this.accessMap.remove(key, isInstance);
	}
	
	public int getAccessNumber()
	{
		return this.accessMap.size() - this.accessMap.getInstanceCnt();
	}
	
	public void removeUser(UserContext user)
	{
		if(!user.getAuthorization().equals("Instance")) return;
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM PUBLIC.USER WHERE ID = '"+user.getID()+"' AND AUTH = 'Instance'");
			connection.commit();
		} catch (SQLException e) {
			logger.warn(0x010A);
			try {
				if(connection != null) connection.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public void addUser(UserContext user)
	{
		if(!user.getAuthorization().equals("Instance")) return;
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			stmt.executeUpdate("INSERT INTO PUBLIC.USER VALUES('"+user.getID()+"','"+user.getUserName()+"','"+user.getPassword()+"','"+user.getAuthorization()+"','"+user.getPhone()+"','"+user.isActivate()+"')");
			connection.commit();
		} catch (SQLException e) {
			logger.warn(0x0109);
			try {
				if(connection != null) connection.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
	}

	public UserContext getUser(String id) {
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		UserContext userContext	= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM USER WHERE ID = '"+id+"' AND TRIM(ACTIVATION) = 'true'");
			if(rs.next())
			{
				userContext = new UserContext(rs.getString("ID"),
									rs.getString("NAME"),
									rs.getString("PW"),
									rs.getString("AUTH"),
									rs.getString("PHONE"),
									rs.getString("ACTIVATION").trim().equals("true"));
			}
		} catch (SQLException e) {
			logger.warn(0x0106);
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		
		return userContext;
	}

	public InstanceContext getInstanceConfiguration(String id) {
		InstanceContext instanceContext = null;
		Connection 		connection 		= null;
		Statement 		stmt 			= null;
		ResultSet 		rs 				= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM INSTANCE WHERE ID = '"+id+"' ORDER BY ID ASC");
			if(rs.next())
			{
				instanceContext = new InstanceContext(rs.getString("ID"),
						rs.getString("SYSTEM_HEADER_ID"),
						rs.getString("USE_TELEGRAM_CACHE").trim().equals("true"),
						rs.getString("USE_COMPONENT_CACHE").trim().equals("true"),
						rs.getString("USE_QUERY_CACHE").trim().equals("true"),
						rs.getString("USE_SERVICE_CACHE").trim().equals("true"),
						rs.getInt("SERVICE_PORT"),
						rs.getInt("BUFFER_SIZE"),
						rs.getInt("READ_TIMEOUT"),
						rs.getInt("MAX_CLIENTS"),
						rs.getString("SERVER_ENCODING"),
						rs.getString("TIME_LOCALE"),
						rs.getString("CLASS_PATH"),
						rs.getString("JAVA_HOME"),
						rs.getString("SYSTEM_PATH"),
						rs.getString("SYSTEM_ENCODING"),
						rs.getString("FILE_ENCODING"),
						rs.getString("LOG_CONFIG_PATH"),
						rs.getString("LOAD_ON_STARTUP").trim().equals("true"),
						rs.getString("EXTRA_LOAD_PARAMS"),
						rs.getString("LENGTH_FIELD_ID"),
						rs.getString("ID_FIELD_ID"));
			}
		} catch (SQLException e) {
			logger.warn(0x0107);
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		
		return instanceContext;
	}
	
	public ArrayList<String> getInstanceList() {
		ArrayList<String> instanceList 	= new ArrayList<String>();
		Connection 		connection 		= null;
		Statement 		stmt 			= null;
		ResultSet 		rs 				= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT ID FROM INSTANCE");
			while(rs.next())
			{
				instanceList.add(rs.getString("ID"));
			}
		} catch (SQLException e) {
			logger.warn(0x0107);
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		
		return instanceList;
	}

	public void closeAllClients()
	{
		Set<Long> keySet = accessMap.keySet();
		Iterator<Long> itr = keySet.iterator();
		while(itr.hasNext())
		{
			long key = itr.next();
			accessMap.get(key).exit();
		}
		
		int tryCnt = 0;
		while(accessMap.size() > 0)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(e, 0x0016);
			}
			
			if(tryCnt > 600)
			{
				keySet = accessMap.keySet();
				itr = keySet.iterator();
				while(itr.hasNext())
				{
					long key = itr.next();
					
					if(accessMap.get(key).isInstance())
					{
						logger.fatal(NLabel.get(0x0043) + " [" + accessMap.get(key).getInstanceName() + "]");
					}
				}
				
				logger.fatal(0x0069);
				break;
			}
			
			tryCnt++;
		}
	}

}
