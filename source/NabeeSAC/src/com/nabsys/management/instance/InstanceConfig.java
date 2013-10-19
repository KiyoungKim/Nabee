package com.nabsys.management.instance;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;
import com.nabsys.resource.DOMConfigurator;
import com.nabsys.resource.ServerConfiguration;

public class InstanceConfig implements IManagementClass{
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();

		if(fromClient.get("CMD_CODE").equals("L"))
		{
			boolean isAll = false;
			if(fromClient.containsKey("ISALL") && ((String)fromClient.get("ISALL")).equals("true")) isAll = true;
			try {
				toClient = getInstancelist(context, isAll);
			} catch(Exception e){
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			}
		}
		else if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getInstanceConfig(context, fromClient);
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			} catch (SAXException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			} catch (TransformerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			} catch (ClassNotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting instance information.");
			} 
		}
		else if(fromClient.get("CMD_CODE").equals("F"))
		{
			try {
				ArrayList<NBFields> fieldList = getFieldList(context, fromClient);
				toClient.put("FLD_LST", fieldList);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
			
		}
		else if(fromClient.get("CMD_CODE").equals("I"))
		{
			try {
				addInstance(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("D"))
		{
			try {
				removeInstance(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
			
		}
		else if(fromClient.get("CMD_CODE").equals("S"))
		{
			try {
				setInstanceConfig(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("P"))
		{
			try {
				toClient = getPluginList(context);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting instance information.");
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting instance information.");
			} catch (SAXException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting instance information.");
			} catch (TransformerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting instance information.");
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		
		return toClient;
	}
	
	private NBFields getPluginList(ManagementContext context) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT PLUGIN_NAME PUBLIC.PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"' AND PLUGIN_NAME LIKE '%"+(!context.getFields().containsKey("SCH")?"":context.getFields().get("SCH"))+"%'");
			
			DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
			ArrayList<NBFields> plgList = new ArrayList<NBFields>();
			ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
			
			while(rs.next())
			{
				NBFields tmpFields = new NBFields();
				
				for(int j=0; j<getPluginList.size(); j++)
				{
					if(getPluginList.get(j).get("id").equals(rs.getString("PLUGIN_NAME")))
					{
						tmpFields.put("ID", getPluginList.get(j).get("id"));
						tmpFields.put("NAME", getPluginList.get(j).get("name"));
						tmpFields.put("TYPE", getPluginList.get(j).get("type"));
					}
				}
				plgList.add(tmpFields);
			}
			
			rtnFields.put("PLG_LST", plgList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}
	
	private void addInstance(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			String sql = "";
			sql+="INSERT INTO INSTANCE VALUES(";
			sql+="'"+context.getInstanceID()+"',";
			sql+="'',";
			sql+="'false',";
			sql+="'false',";
			sql+="'false',";
			sql+="'false',";
			sql+="0,";
			sql+="4096,";
			sql+="0,";
			sql+="10,";
			sql+="'UTF-8',";
			sql+="'',";
			sql+="'',";
			sql+="'',";
			sql+="'',";
			sql+="'UTF-8',";
			sql+="'UTF-8',";
			sql+="'',";
			sql+="'false',";
			sql+="'',";
			sql+="'',";
			sql+="''";
			sql+=")";
			stmt.executeUpdate(sql);
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		logger.info(NLabel.get(0x0089) + " [" + context.getInstanceID() + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	private void removeInstance(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			stmt.executeUpdate("DELETE FROM USER_ACCESS_LIST WHERE INSTANCE = '"+context.getInstanceID()+"'");
			stmt.executeUpdate("DELETE FROM PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"'");
			stmt.executeUpdate("DELETE FROM INSTANCE WHERE ID = '"+context.getInstanceID()+"'");
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		logger.info(NLabel.get(0x008B) + " [" + context.getInstanceID() + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	@SuppressWarnings("unchecked")
	private void setInstanceConfig(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "UPDATE PUBLIC.INSTANCE SET \n";
			boolean isExist = false;
			if(fields.containsKey("SYS_HDR"))
			{
				sql += "SYSTEM_HEADER_ID = '" + fields.get("SYS_HDR") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("LEN_FLD"))
			{
				sql += (isExist == true?",":"") + "LENGTH_FIELD_ID = '" + (fields.get("LEN_FLD").equals("-Undefined-")?"":fields.get("LEN_FLD")) + "'\n";
				isExist = true;
			}
			if(fields.containsKey("ID_FLD"))
			{
				sql += (isExist == true?",":"") + "ID_FIELD_ID = '" + (fields.get("ID_FLD").equals("-Undefined-")?"":fields.get("ID_FLD")) + "'\n";
				isExist = true;
			}
			if(fields.containsKey("TLGM_CACHE"))
			{
				sql += (isExist == true?",":"") + "USE_TELEGRAM_CACHE = '" + fields.get("TLGM_CACHE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("COMP_CACHE"))
			{
				sql += (isExist == true?",":"") + "USE_COMPONENT_CACHE = '" + fields.get("SQL_CACHE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("SQL_CACHE"))
			{
				sql += (isExist == true?",":"") + "USE_QUERY_CACHE = '" + fields.get("SQL_CACHE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("SVC_CACHE"))
			{
				sql += (isExist == true?",":"") + "USE_SERVICE_CACHE = '" + fields.get("SVC_CACHE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("LOAD_STUP"))
			{
				sql += (isExist == true?",":"") + "LOAD_ON_STARTUP = '" + fields.get("LOAD_STUP") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("PORT"))
			{
				sql += (isExist == true?",":"") + "SERVICE_PORT = " + (Integer)fields.get("PORT") + "\n";
				isExist = true;
			}
			if(fields.containsKey("BUFF_SIZE"))
			{
				sql += (isExist == true?",":"") + "BUFFER_SIZE = " + (Integer)fields.get("BUFF_SIZE") + "\n";
				isExist = true;
			}
			if(fields.containsKey("TIME_OUT"))
			{
				sql += (isExist == true?",":"") + "READ_TIMEOUT = " + (Integer)fields.get("TIME_OUT") + "\n";
				isExist = true;
			}
			if(fields.containsKey("MAX_CLT_NUM"))
			{
				sql += (isExist == true?",":"") + "MAX_CLIENTS = " + (Integer)fields.get("MAX_CLT_NUM") + "\n";
				isExist = true;
			}
			if(fields.containsKey("REMOTE_ENCODING"))
			{
				sql += (isExist == true?",":"") + "SERVER_ENCODING = '" + fields.get("REMOTE_ENCODING") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("LOCALE"))
			{
				sql += (isExist == true?",":"") + "TIME_LOCALE = '" + fields.get("LOCALE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("COMP_PATH"))
			{
				sql += (isExist == true?",":"") + "CLASS_PATH = '" + fields.get("COMP_PATH") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("JAVA_HOME"))
			{
				sql += (isExist == true?",":"") + "JAVA_HOME = '" + fields.get("JAVA_HOME") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("PATH"))
			{
				sql += (isExist == true?",":"") + "SYSTEM_PATH = '" + fields.get("PATH") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("SYS_ENCODING"))
			{
				sql += (isExist == true?",":"") + "SYSTEM_ENCODING = '" + fields.get("SYS_ENCODING") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("FILE_ENCODING"))
			{
				sql += (isExist == true?",":"") + "FILE_ENCODING = '" + fields.get("FILE_ENCODING") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("LOG_CFG_FILE"))
			{
				sql += (isExist == true?",":"") + "LOG_CONFIG_PATH = '" + fields.get("LOG_CFG_FILE") + "'\n";
				isExist = true;
			}
			if(fields.containsKey("ARGS"))
			{
				sql += (isExist == true?",":"") + "EXTRA_LOAD_PARAMS = '" + fields.get("ARGS") + "'\n";
				isExist = true;
			}
			
			sql += "WHERE ID = '"+context.getInstanceID()+"'";
			if(isExist) stmt.executeUpdate(sql);
			
			if(fields.containsKey("ACT_LST"))
			{
				for(int i=0; i<((ArrayList<NBFields>)fields.get("ACT_LST")).size(); i++)
				{
					NBFields act = ((ArrayList<NBFields>)fields.get("ACT_LST")).get(i);
					if(((String)act.get("CTG")).equals("USR"))
					{
						if(((String)act.get("ACT")).equals("I"))
						{
							rs = stmt.executeQuery("SELECT USER_ID FROM USER_ACCESS_LIST WHERE INSTANCE = '"+context.getInstanceID()+"' AND USER_ID = '"+act.get("ID")+"'");
							if(rs.next()) throw new KeyDuplicateException(0x0024);
							stmt.executeUpdate("INSERT INTO USER_ACCESS_LIST VALUES ('"+context.getInstanceID()+"', '"+act.get("ID")+"')");
						}
						else if(((String)act.get("ACT")).equals("D"))
						{
							stmt.executeUpdate("DELETE FROM USER_ACCESS_LIST WHERE INSTANCE = '"+context.getInstanceID()+"' AND USER_ID = '"+act.get("ID")+"'");
						}
					}
					else if(((String)act.get("CTG")).equals("PLG"))
					{
						if(((String)act.get("ACT")).equals("I"))
						{
							rs = stmt.executeQuery("SELECT PLUGIN_NAME FROM PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"' AND PLUGIN_NAME = '"+act.get("ID")+"'");
							if(rs.next()) throw new KeyDuplicateException(0x0024);
							stmt.executeUpdate("INSERT INTO PLUGIN_LIST VALUES ('"+context.getInstanceID()+"', '"+act.get("ID")+"')");
						}
						else if(((String)act.get("ACT")).equals("D"))
						{
							stmt.executeUpdate("DELETE FROM PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"' AND PLUGIN_NAME = '"+act.get("ID")+"'");
						}
					}
				}
			}
			connection.commit();
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		logger.info(NLabel.get(0x008A) + " [" + context.getInstanceID() + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	private ArrayList<NBFields> getFieldList(ManagementContext context, NBFields fields) throws SQLException
	{
		ArrayList<NBFields> hdrFldList = new ArrayList<NBFields>();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT TELEGRAM_FIELDS.ID ID FROM TELEGRAM, TELEGRAM_FIELDS WHERE TELEGRAM.ID = TELEGRAM_FIELDS.TELEGRAM_ID AND TELEGRAM.INSTANCE = '"+context.getInstanceID()+"' AND TELEGRAM_FIELDS.INSTANCE = '"+context.getInstanceID()+"' AND TELEGRAM.ID = '"+fields.get("SYS_HDR")+"' ORDER BY INDEX ASC");
			while(rs.next())
			{
				NBFields tmpMap = new NBFields();
				tmpMap.put("HDR_FLD", rs.getString("ID"));
				hdrFldList.add(tmpMap);
			}
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return hdrFldList;
	}
	
	private ArrayList<NBFields> getInstanceHeaderFields(ManagementContext context, NBFields fields) throws SQLException
	{
		ArrayList<NBFields> hdrFldList = new ArrayList<NBFields>();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT TELEGRAM_FIELDS.ID ID FROM INSTANCE, TELEGRAM, TELEGRAM_FIELDS WHERE INSTANCE.SYSTEM_HEADER_ID = TELEGRAM.ID AND INSTANCE.ID = TELEGRAM.INSTANCE AND INSTANCE.ID = TELEGRAM_FIELDS.INSTANCE AND TELEGRAM.ID = TELEGRAM_FIELDS.TELEGRAM_ID AND INSTANCE = '"+context.getInstanceID()+"'ORDER BY INDEX ASC");
			while(rs.next())
			{
				NBFields tmpMap = new NBFields();
				tmpMap.put("HDR_FLD", rs.getString("ID"));
				hdrFldList.add(tmpMap);
			}
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return hdrFldList;
	}
	
	private NBFields getInstanceConfig(ManagementContext context, NBFields fromClient) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException, ClassNotFoundException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM PUBLIC.INSTANCE WHERE ID = '"+context.getInstanceID()+"'");
			if(rs.next())
			{
				rtnFields.put("SYS_HDR"			, rs.getString("SYSTEM_HEADER_ID") == null?"":rs.getString("SYSTEM_HEADER_ID"));
				rtnFields.put("LEN_FLD"			, (rs.getString("LENGTH_FIELD_ID") == null || rs.getString("LENGTH_FIELD_ID").equals(""))?"-Undefined-":rs.getString("LENGTH_FIELD_ID"));
				rtnFields.put("ID_FLD"			, (rs.getString("ID_FIELD_ID") == null || rs.getString("ID_FIELD_ID").equals(""))?"-Undefined-":rs.getString("ID_FIELD_ID"));
				rtnFields.put("LOCALE"			, rs.getString("TIME_LOCALE"));
				rtnFields.put("LOG_CFG_FILE"	, rs.getString("LOG_CONFIG_PATH"));
				rtnFields.put("TLGM_CACHE"		, rs.getString("USE_TELEGRAM_CACHE").trim());
				rtnFields.put("SQL_CACHE"		, rs.getString("USE_QUERY_CACHE").trim());
				rtnFields.put("SVC_CACHE"		, rs.getString("USE_SERVICE_CACHE").trim());
				rtnFields.put("LOAD_STUP"		, rs.getString("LOAD_ON_STARTUP").trim());
				rtnFields.put("JAVA_HOME"		, rs.getString("JAVA_HOME"));
				rtnFields.put("PATH"			, rs.getString("SYSTEM_PATH"));
				rtnFields.put("COMP_PATH"		, rs.getString("CLASS_PATH"));
				rtnFields.put("SYS_ENCODING"	, rs.getString("SYSTEM_ENCODING"));
				rtnFields.put("FILE_ENCODING"	, rs.getString("FILE_ENCODING"));
				rtnFields.put("ARGS"			, rs.getString("EXTRA_LOAD_PARAMS"));
				rtnFields.put("PORT"			, rs.getInt("SERVICE_PORT"));
				rtnFields.put("BUFF_SIZE"		, rs.getInt("BUFFER_SIZE"));
				rtnFields.put("TIME_OUT"		, rs.getInt("READ_TIMEOUT"));
				rtnFields.put("MAX_CLT_NUM"		, rs.getInt("MAX_CLIENTS"));
				rtnFields.put("REMOTE_ENCODING"	, rs.getString("SERVER_ENCODING"));
			}
			
			ArrayList<NBFields> accList = new ArrayList<NBFields>();
			rs = stmt.executeQuery("SELECT USER_ID, NAME, AUTH, PHONE FROM USER_ACCESS_LIST, USER WHERE USER_ID = ID AND USER_ACCESS_LIST.INSTANCE = '"+context.getInstanceID()+"' ");
			while(rs.next())
			{
				NBFields tmpFields = new NBFields();
				
				tmpFields.put("ID", rs.getString("USER_ID"));
				tmpFields.put("NAME", rs.getString("NAME"));
				tmpFields.put("ROLE", rs.getString("AUTH"));
				tmpFields.put("PHONE", rs.getString("PHONE"));
				
				accList.add(tmpFields);
			}
			rtnFields.put("ACC_LST", accList);
			
			ArrayList<NBFields> plgList = new ArrayList<NBFields>();
			DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
			ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
			rs = stmt.executeQuery("SELECT PLUGIN_NAME FROM PUBLIC.PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"'");
			boolean isFind = false;
			while(rs.next())
			{
				NBFields tmpFields = new NBFields();
				
				for(int j=0; j<getPluginList.size(); j++)
				{
					if(getPluginList.get(j).get("id").equals(rs.getString("PLUGIN_NAME")))
					{
						tmpFields.put("ID", getPluginList.get(j).get("id"));
						tmpFields.put("NAME", getPluginList.get(j).get("name"));
						tmpFields.put("TYPE", getPluginList.get(j).get("type"));
						isFind = true;
					}
				}
				
				if(!isFind)
				{
					tmpFields.put("ID", 	rs.getString("PLUGIN_NAME"));
					tmpFields.put("NAME", 	"Deleted");
					tmpFields.put("TYPE", 	"OTHER");
				}
				
				plgList.add(tmpFields);
				
				isFind = false;
			}
			rtnFields.put("PLG_LST", plgList);
			
			///////////////////////HEADER FIELD LIST/////////////
			fromClient.put("SYS_HDR", (String)rtnFields.get("SYS_HDR"));
			rtnFields.put("FLD_LST", getInstanceHeaderFields(context, fromClient));
			///////////////////////HEADER FIELD LIST/////////////
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}
	
	
	private NBFields getInstancelist(ManagementContext context, boolean isAll) throws SQLException
	{
		NBFields rtnFields = new NBFields();
		String user = context.getUser();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			if(context.getUserAuthority().equals("Admin") || isAll)
			{
				rs = stmt.executeQuery("SELECT ID FROM PUBLIC.INSTANCE");
			}
			else
			{
				rs = stmt.executeQuery("SELECT INSTANCE.ID ID FROM INSTANCE, USER_ACCESS_LIST WHERE INSTANCE.ID = USER_ACCESS_LIST.INSTANCE AND USER_ID = '"+user+"'");
			}
			
			ArrayList<NBFields> instanceList = new ArrayList<NBFields>();
			while(rs.next())
			{
				NBFields instanceMap = new NBFields();
				instanceMap.put("INSTANCE_NAME", rs.getString("ID"));
				instanceMap.put("RUNNING", context.getInstances() == null?"false":String.valueOf(context.getInstances().NBgetStatus(rs.getString("ID"))));
				instanceList.add(instanceMap);
			}
			
			rtnFields.put("INSTANCE_LIST", instanceList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}

}
