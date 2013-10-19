package com.nabsys.process;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.fileio.ObjectFileIO;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.CustomLoader;
import com.nabsys.database.DBPoolManager;
import com.nabsys.database.SqlContext;
import com.nabsys.database.TagDocument;
import com.nabsys.process.exception.PluginInitializationException;
import com.nabsys.process.instance.messagequeue.QueueFactory;
import com.nabsys.resource.BatchServiceContext;
import com.nabsys.resource.DOMConfigurator;
import com.nabsys.resource.GeneralServiceContext;
import com.nabsys.resource.InstanceContext;
import com.nabsys.resource.MessageQueueServiceContext;
import com.nabsys.resource.OnlineServiceContext;
import com.nabsys.resource.PluginList;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.ServiceContext;
import com.nabsys.resource.ServiceContextList;
import com.nabsys.resource.TelegramContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class ResourceFactory {
	final 	NLogger 						logger = NLogger.getLogger(this.getClass());
	private DBPoolManager					configDBPool	= null;
	private PluginList 						pluginList 		= null;
	private PluginList						protocolList	= null;
	private HashMap<String, IPlugin>		pluginTofinalize = null;
	private QueueFactory 					queueFactory	= null; 	
	private HashMap<Long, Context>			testContextMap	= null;
	private InstanceContext					instanceContext = null;
	private HashMap<String, ServiceContext>	serviceMap		= null;
	private HashMap<String, TelegramContext>telegramMap		= null;
	private HashMap<String, SqlContext>		sqlMap			= null;
	private String							instanceID		= null;
	
	public ResourceFactory(DBPoolManager pool, String instanceID) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		this.configDBPool = pool;
		this.instanceID = instanceID;
		try {
			loadPlugin(pool);
			loadProtocol();
			
			Connection 		connection 		= null;
			Statement 		stmt 			= null;
			ResultSet 		rs 				= null;
			try {
				connection = configDBPool.getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery("SELECT * FROM INSTANCE WHERE ID = '" + instanceID + "'");
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
		} catch (SQLException e) {
			logger.warn(e, 0x0023);
		} 
	}
	
	public DBPoolManager getConfigDBPool()
	{
		return this.configDBPool;
	}
	
	public void setQueueFactory(QueueFactory queueFactory)
	{
		this.queueFactory = queueFactory;
	}
	
	public QueueFactory getQueueFactory()
	{
		return queueFactory;
	}
	
	public void setServiceContext(long clientID, Context ctx)
	{
		if(testContextMap == null) testContextMap = new HashMap<Long, Context>();
		testContextMap.put(clientID, ctx);
	}
	
	public Context getServiceContext(long clientID)
	{
		if(testContextMap.containsKey(clientID)) return testContextMap.get(clientID);
		else return null;
	}
	
	public void removeServiceContext(long clientID)
	{
		if(testContextMap != null && testContextMap.containsKey(clientID)) testContextMap.remove(clientID);
	}
	
	public InstanceContext getInstanceConfiguration()
	{
		return instanceContext;
	}
	
	public SqlContext getSql(String id) throws SQLException, IOException, ClassNotFoundException
	{
		if(instanceContext.isUseTelegramCache())
		{
			if(sqlMap == null)
			{
				synchronized(this){
					if(sqlMap == null)
					{
						sqlMap = new HashMap<String, SqlContext>();
					}
				}
			}
			if(sqlMap.containsKey(id)) return sqlMap.get(id);
		}
		SqlContext context = null;
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM SQL_STORAGE WHERE ID = '"+id+"' AND INSTANCE = '" + instanceID + "'");
			if(rs.next())
			{
				TagDocument tagGDocument = null;
				byte[] tagDocumentData = rs.getBytes("TAG_DOCUMENT");
				if(tagDocumentData != null)
				{
					ObjectFileIO ofio = new ObjectFileIO();
					tagGDocument = (TagDocument)ofio.recoverObject(tagDocumentData);
				}
				context = new SqlContext(rs.getString("ID"), rs.getInt("LEVEL"), rs.getString("TYPE"), rs.getString("NEED_PARAM").trim().equals("true"), rs.getString("CONTENTS"), tagGDocument);
				
				if(instanceContext.isUseQueryCache() && sqlMap != null && !sqlMap.containsKey(id))
				{
					sqlMap.put(id, context);
				}
			}
			else
			{
				if(instanceContext.isUseQueryCache() && sqlMap != null && sqlMap.containsKey(id))
				{
					return sqlMap.get(id);
				}
				else
				{
					throw new NotFoundException(NLabel.get(0x010F) + " [" +id+ "]");
				}
			}
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		return context;
	}
	
	public TelegramContext getTelegram(String id) throws SQLException
	{
		if(instanceContext.isUseTelegramCache())
		{
			if(telegramMap == null)
			{
				synchronized(this){
					if(telegramMap == null)
					{
						telegramMap = new HashMap<String, TelegramContext>();
					}
				}
			}
			
			if(telegramMap.containsKey(id)) return telegramMap.get(id);
		}
		TelegramContext context = null;
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String 	sql = "SELECT TG.ID TGID, TG.NAME TGNM, TG.HEADER_ID TGHDNM, TG.LOG_LEVEL TGLLV,\n";
					sql += "FD.ID FDID, FD.NAME FDNM, FD.INDEX FDIDX, FD.LENGTH FDLTH,\n";
					sql += "FD.MANDATORY FDMDT, FD.PADDING FDPD, FD.ALIGN FDALG, FD.TYPE FDTP\n";
					sql += "FROM TELEGRAM TG LEFT OUTER JOIN TELEGRAM_FIELDS FD\n";
					sql += "ON TG.ID = FD.TELEGRAM_ID AND TG.INSTANCE = FD.INSTANCE\n";
					sql += "WHERE TG.ID = '"+id+"' AND TG.INSTANCE = '" + instanceID + "'\n";
					sql += "ORDER BY FD.INDEX";
			rs = stmt.executeQuery(sql);
			TelegramFieldContextList list = new TelegramFieldContextList();
			boolean chkTG = false;
			boolean exists = false;
			while(rs.next())
			{
				if(!exists) exists = true;
				if(!chkTG)
				{
					context = new TelegramContext(rs.getString("TGID"), rs.getString("TGNM"), rs.getString("TGHDNM"), rs.getString("TGLLV").toCharArray()[0], "", list);
					chkTG = true;
				}
				
				if(rs.getString("FDID") != null && !rs.getString("FDID").equals(""))
				{
					list.put(rs.getString("FDID"), new TelegramFieldContext(rs.getString("FDID"), rs.getString("FDNM"), rs.getInt("FDIDX"), rs.getInt("FDLTH"), rs.getString("FDMDT").trim().equals("true"), rs.getString("FDPD").toCharArray()[0], rs.getString("FDALG").toCharArray()[0], rs.getString("FDTP").toCharArray()[0], ""));
				}
			}
			
			if(exists)
			{
				if(instanceContext.isUseTelegramCache() && telegramMap != null && !telegramMap.containsKey(id))
				{
					telegramMap.put(id, context);
				}
			}
			else
			{
				if(instanceContext.isUseTelegramCache() && telegramMap != null && telegramMap.containsKey(id))
				{
					return telegramMap.get(id);
				}
			}
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		return context;
	}
	
	private ServiceContext getService(String id, String type) throws SQLException, IOException, ClassNotFoundException
	{
		if(instanceContext.isUseServiceCache())
		{
			if(serviceMap == null)
			{
				synchronized(this){
					if(serviceMap == null)
					{
						serviceMap = new HashMap<String, ServiceContext>();
					}
				}
			}
			if(serviceMap.containsKey(id)) return serviceMap.get(id);
		}
		
		ServiceContext context = null;
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "";
			if(type == null)
			{
				sql = "SELECT * FROM SERVICE WHERE ID = '"+id+"' AND TRIM(ACTIVATE) = 'true' AND INSTANCE = '" + instanceID + "'";
			}
			else
			{
				sql = "SELECT * FROM SERVICE WHERE ID = '"+id+"' AND TYPE = '"+type+"' AND TRIM(ACTIVATE) = 'true' AND INSTANCE = '" + instanceID + "'";
			}
			
			rs = stmt.executeQuery(sql);

			if(rs.next())
			{
				if(type == null)
				{
					type = rs.getString("TYPE");
				}
				
				byte[] serviceData = rs.getBytes("SERVICE_DESIGN");
				
				if(type.equals("General"))
				{
					context = new GeneralServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
				}
				else if(type.equals("Online"))
				{
					context = new OnlineServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
				}
				else if(type.equals("Batch"))
				{
					context = new BatchServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
				}
				else if(type.equals("MessageQueue"))
				{
					context = new MessageQueueServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
				}
				
				if(serviceMap != null)
				{
					serviceMap.put(id, context);
				}
				
				if(instanceContext.isUseServiceCache() && serviceMap != null && !serviceMap.containsKey(id))
				{
					serviceMap.put(id, context);
				}
			}
			else
			{
				if(instanceContext.isUseServiceCache() && serviceMap != null && serviceMap.containsKey(id))
				{
					return serviceMap.get(id);
				}
			}
			
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		return context;
	}

	/*
	 * This method just called when instance is initializing.
	 * Doesn't need to use cash.
	 */
	private ServiceContextList getServiceList(String type) throws SQLException, IOException, ClassNotFoundException
	{
		ServiceContextList contextList = new ServiceContextList();
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM SERVICE WHERE TYPE = '"+type+"' AND TRIM(ACTIVATE) = 'true' AND INSTANCE = '" + instanceID + "'");
			
			while(rs.next())
			{
				byte[] serviceData = rs.getBytes("SERVICE_DESIGN");
				if(type.equals("General"))
				{
					ServiceContext context = new GeneralServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
					contextList.put(rs.getString("ID"), context);
				}
				else if(type.equals("Online"))
				{
					ServiceContext context = new OnlineServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
					contextList.put(rs.getString("ID"), context);
				}
				else if(type.equals("Batch"))
				{
					ServiceContext context = new BatchServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
					contextList.put(rs.getString("ID"), context);
				}
				else if(type.equals("MessageQueue"))
				{
					ServiceContext context = new MessageQueueServiceContext(
							rs.getString("ID"),
							rs.getString("NAME"),
							rs.getString("TYPE"),
							rs.getString("REMARK"),
							serviceData,
							rs.getString("ACTIVATE").trim().equals("true"));
					contextList.put(rs.getString("ID"), context);
				}
			}
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
		return contextList;
	}
	
	public OnlineServiceContext getOnlineService(String id) throws SQLException, IOException, ClassNotFoundException
	{
		logger.debug("Request service : " + id);
		OnlineServiceContext service = (OnlineServiceContext)getService(id, "Online");
		if(service == null) throw new RuntimeException(NLabel.get(0x0108));
		service.setNetworkInformation(this);
		return service;
	}
	
	public BatchServiceContext getBatchService(String id) throws SQLException, IOException, ClassNotFoundException
	{
		return (BatchServiceContext)getService(id, "Batch");
	}
	
	public ServiceContext getService(String id) throws SQLException, IOException, ClassNotFoundException
	{
		return getService(id, null);
	}
	
	public ServiceContextList getBatchServiceList() throws SQLException, IOException, ClassNotFoundException
	{
		return getServiceList("Batch");
	}

	public MessageQueueServiceContext getMessageQueueService(String id) throws SQLException, IOException, ClassNotFoundException
	{
		return (MessageQueueServiceContext)getService(id, "MessageQueue");
	}
	
	public GeneralServiceContext getGeneralService(String id) throws SQLException, IOException, ClassNotFoundException
	{
		return (GeneralServiceContext)getService(id, "General");
	}
	
	public ServiceContextList getMessageQueueServiceList() throws SQLException, IOException, ClassNotFoundException
	{
		return getServiceList("MessageQueue");
	}
	
	public Object getPlugin(String id)
	{
		return pluginList.get(id);
	}
	
	public PluginList getPluginList()
	{
		return pluginList;
	}
	
	public Class<?> getProtocol(String id)
	{
		return (Class<?>)protocolList.get(id);
	}
	
	private void loadProtocol() throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		protocolList = new PluginList();
		CustomLoader cl = new CustomLoader();
		DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
		ArrayList<HashMap<String, String>> protocolInfoList = dom.getSubNodeListParamMap("server/protocol-list");
		for(int i=0; i<protocolInfoList.size(); i++)
		{
			String protocolID = protocolInfoList.get(i).get("id");
			String classURL = protocolInfoList.get(i).get("class");
			String path = classURL.replace(".", "/") + ".class";
			File classFile = new File(path);
			if(classFile.exists())
			{
				Class<?> protocol = cl.getCustomClass(classURL, path);
				protocolList.put(protocolID, protocol);
			}
			else
			{
				Class<?> protocol = cl.getCustomClass(classURL);
				protocolList.put(protocolID, protocol);
			}
		}
	}
	
	private void loadPlugin(DBPoolManager pool) throws SQLException
	{
		pluginList = new PluginList();
		pluginTofinalize = new HashMap<String, IPlugin>();
		Connection 	connection 	= null;
		Statement 	stmt 		= null;
		ResultSet 	rs 			= null;
		CustomLoader cl = null;
		try {
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM PLUGIN_LIST WHERE INSTANCE = '" + instanceID + "'");
			while(rs.next())
			{
				HashMap<String, String> params = ServerConfiguration.getPluginParams(rs.getString("PLUGIN_NAME"));
				cl = new CustomLoader();
				Class<?> tmpClass;
				Object pluginObject = null;
				IPlugin plugin 		= null;
				try {
					tmpClass = cl.getCustomClass(params.get("CLASS"), 
												params.get("CLASS").replace(".", "/") + ".class");
					Object tmpObj = tmpClass.newInstance();
					
					plugin = (IPlugin)tmpObj;
					pluginObject = plugin.initializer(params);
				} catch (IOException e) {
					logger.warn(e, e.getMessage());
				} catch (InstantiationException e) {
					logger.warn(e, e.getMessage());
				} catch (IllegalAccessException e) {
					logger.warn(e, e.getMessage());
				} catch (PluginInitializationException e) {
					logger.warn(e.getMessage());
					logger.warn(e, NLabel.get(0x0023) + " [" + rs.getString("PLUGIN_NAME") + "]");
					pluginObject = e.getReturnObject();
				} catch(Exception e){
					logger.warn(e, e.getMessage());
				}
				
				if(pluginObject instanceof DBPoolManager) ((DBPoolManager)pluginObject).setResourceFactory(this);
				
				pluginTofinalize.put(rs.getString("PLUGIN_NAME"), plugin);
				pluginList.put(rs.getString("PLUGIN_NAME"), pluginObject);
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
	
	public void closeResources()
	{
		closePlugin();
		try {
			configDBPool.closePool();
		} catch (SQLException e) {
		}
	}
	
	private void closePlugin()
	{
		Set<String> keySet = pluginTofinalize.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			IPlugin tmpPluginContext = pluginTofinalize.get(key);
			if(tmpPluginContext != null)
				tmpPluginContext.finalizer();
		}
	}
	
}
