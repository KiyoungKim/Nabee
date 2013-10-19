package com.nabsys.management.document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;
import com.nabsys.resource.DOMConfigurator;
import com.nabsys.resource.ServerConfiguration;

public class ServiceConfig implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();
		
		if(fromClient.get("CMD_CODE").equals("L"))
		{
			try {
				toClient = getServiceList(context, fromClient);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
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
		else if(fromClient.get("CMD_CODE").equals("S"))
		{
			try {
				setServiceConfig(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
			}catch (NullPointerException e) {
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
		else if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getServiceConfig(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
			}catch (NullPointerException e) {
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
				toClient = removeService(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
			}catch (NullPointerException e) {
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
		else if(fromClient.get("CMD_CODE").equals("PL"))
		{
			try {
				toClient = getProtocolList(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("DL"))
		{
			try {
				toClient = getDatabaseList(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("SL"))
		{
			try {
				toClient = getSQLList(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("CPL"))
		{
			try {
				toClient = getConnectionPoolList(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("LC"))
		{
			try {
				toClient = getCallableServiceList(context, fromClient);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
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
	
	private NBFields getSQLList(ManagementContext context, NBFields fromClient) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			toClient 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			String id = ((String)fromClient.get("PATH")).trim();

			rs = stmt.executeQuery("SELECT ID FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '%"+id+"%'");
			ArrayList<NBFields> list = new ArrayList<NBFields>();
			while(rs.next())
			{
				NBFields tmp = new NBFields();
				tmp.put("ID", rs.getString("ID"));
				list.add(tmp);
			}
			
			toClient.put("LIST", list);
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return toClient;
	}
	
	private NBFields getConnectionPoolList(ManagementContext context, NBFields fields) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet			rs				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			rs = stmt.executeQuery("SELECT PLUGIN_NAME FROM PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"'");
			ArrayList<NBFields> pluginList = new ArrayList<NBFields>();
			DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
			ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
			while(rs.next())
			{
				for(int j=0; j<getPluginList.size(); j++)
				{
					if(rs.getString("PLUGIN_NAME").equals(getPluginList.get(j).get("id")) && getPluginList.get(j).get("type").equals("CONNECTION"))
					{
						NBFields tmp = new NBFields();
						tmp.put("NAME", rs.getString("PLUGIN_NAME"));
						pluginList.add(tmp);
						break;
					}
				}
			}
			rtnFields.put("LIST", pluginList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getDatabaseList(ManagementContext context, NBFields fields) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet			rs				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			rs = stmt.executeQuery("SELECT PLUGIN_NAME FROM PLUGIN_LIST WHERE INSTANCE = '"+context.getInstanceID()+"'");
			ArrayList<NBFields> pluginList = new ArrayList<NBFields>();
			DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
			ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
			while(rs.next())
			{
				for(int j=0; j<getPluginList.size(); j++)
				{
					if(rs.getString("PLUGIN_NAME").equals(getPluginList.get(j).get("id")) && getPluginList.get(j).get("type").equals("DATABASE"))
					{
						NBFields tmp = new NBFields();
						tmp.put("NAME", rs.getString("PLUGIN_NAME"));
						pluginList.add(tmp);
						break;
					}
				}
			}
			rtnFields.put("LIST", pluginList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getProtocolList(ManagementContext context, NBFields fields) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			rtnFields 		= new NBFields();
		DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
		ArrayList<HashMap<String, String>> getProtocolList = dom.getSubNodeListParamMap("server/protocol-list");
		ArrayList<NBFields> listInfo = new ArrayList<NBFields>();
		for(int i=0; i<getProtocolList.size(); i++)
		{
			NBFields protocolFields = new NBFields();
			protocolFields.put("ID", getProtocolList.get(i).get("id"));
			protocolFields.put("NAME", getProtocolList.get(i).get("name"));
			listInfo.add(protocolFields);
		}
		rtnFields.put("LIST", listInfo);
		
		return rtnFields;
	}
	
	private NBFields removeService(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "";
			sql += "DELETE FROM PUBLIC.SERVICE\n";
			sql += "WHERE 1=1\n";
			sql += "AND ID = '"+(String)fields.get("ID")+"'\n";
			sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			
			int rtn = stmt.executeUpdate(sql);
			connection.commit();
			if(rtn <= 0) throw new NotFoundException();
			
		} catch(SQLException e){
			if(connection != null) connection.rollback();
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields setServiceConfig(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		PreparedStatement	pstmt			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "";
			sql += "SELECT COUNT(*) FROM PUBLIC.SERVICE\n";
			sql += "WHERE 1=1\n";
			sql += "AND ID = '"+(String)fields.get("ID")+"'\n";
			sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			
			rs = stmt.executeQuery(sql);
			rs.next();
			String serviceRemark = fields.containsKey("REMARK")?(String)fields.get("REMARK"):"";
			serviceRemark = serviceRemark.replaceAll("'", "''");
			
			if(rs.getInt(1) > 0)
			{
				if(((String)fields.get("ACTION")).equals("I")) throw new KeyDuplicateException(0x0024);
				sql = "UPDATE PUBLIC.SERVICE\n";
				sql += "SET\n";
				sql += "NAME = '" + (fields.containsKey("NAME")?fields.get("NAME"):"") +"',\n";
				sql += "TYPE = '" + (fields.containsKey("TYPE")?fields.get("TYPE"):"") +"',\n";
				sql += "REMARK = '" + serviceRemark +"',\n";
				sql += "SERVICE_DESIGN = ?,\n";
				sql += "ACTIVATE = '" + (fields.containsKey("ACTIVATE")?fields.get("ACTIVATE"):"false") +"'\n";
				sql += "WHERE ID = '" + fields.get("ID") +"'\n";
				sql += "AND INSTANCE = '" + context.getInstanceID() +"'\n";
			}
			else
			{
				sql = "INSERT INTO PUBLIC.SERVICE\n";
				sql += "VALUES(\n";
				sql += "'" + context.getInstanceID() + "',\n";
				sql += "'" + (fields.containsKey("ID")?fields.get("ID"):"") +"',\n";
				sql += "'" + (fields.containsKey("NAME")?fields.get("NAME"):"") +"',\n";
				sql += "'" + (fields.containsKey("TYPE")?fields.get("TYPE"):"") +"',\n";
				sql += "'" + serviceRemark +"',\n";
				sql += "?,\n";
				sql += "'" + (fields.containsKey("ACTIVATE")?fields.get("ACTIVATE"):"false") +"'\n";
				sql += ")\n";
			}
	
			pstmt = connection.prepareStatement(sql);
			pstmt.setObject(1, fields.get("SERVICE_DESIGN"));
			pstmt.executeUpdate();
			
			sql = "INSERT INTO PUBLIC.SERVICE_H\n";
			sql += "SELECT INSTANCE, ID, CURRENT_TIMESTAMP, NAME, TYPE, REMARK, SERVICE_DESIGN, ACTIVATE FROM PUBLIC.SERVICE \n";
			sql += "WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '" + fields.get("ID") +"'\n";
			stmt.executeUpdate(sql);
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null) connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(pstmt != null) pstmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getServiceConfig(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "";
			sql += "SELECT ID, NAME, TYPE, ACTIVATE, REMARK, SERVICE_DESIGN FROM PUBLIC.SERVICE\n";
			sql += "WHERE 1=1\n";
			sql += "AND ID = '"+(String)fields.get("ID")+"'\n";
			sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			
			rs = stmt.executeQuery(sql);
			
			if(!rs.next()) throw new NotFoundException();
				
			rtnFields.put("ID"					, rs.getString("ID"));
			rtnFields.put("NAME"				, rs.getString("NAME"));
			rtnFields.put("TYPE"				, rs.getString("TYPE"));
			rtnFields.put("REMARK"				, rs.getString("REMARK"));
			rtnFields.put("ACTIVATE"			, rs.getString("ACTIVATE").trim());
			rtnFields.put("SERVICE_DESIGN"		, rs.getBytes("SERVICE_DESIGN")==null?"null":rs.getBytes("SERVICE_DESIGN"));
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getCallableServiceList(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String key = (String)fields.get("SCH");
			String caller = (String)fields.get("CALLER");
			String sql = "";
			sql += "SELECT ID, TYPE FROM PUBLIC.SERVICE\n";
			sql += "WHERE INSTANCE = '"+context.getInstanceID()+"'\n";
			sql += "AND (ID LIKE '%"+key+"%'\n";
			sql += "OR NAME LIKE '%"+key+"%')\n";
			sql += "AND (TYPE = 'MessageQueue'\n";
			sql += "OR TYPE = 'General')\n";
			sql += "AND ID != '"+caller+"'\n";
			sql += " ORDER BY ID ASC LIMIT 0, 100";

			rs = stmt.executeQuery(sql);
			ArrayList<NBFields> list = new ArrayList<NBFields>();
			while(rs.next())
			{
				NBFields service = new NBFields();
				
				service.put("ID"					, rs.getString("ID"));
				service.put("TYPE"					, rs.getString("TYPE"));
				list.add(service);
			}
			rtnFields.put("LIST", list);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getServiceList(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String key = (String)fields.get("SCH");
			String sql = "";
			sql += "SELECT ID, NAME, TYPE, REMARK FROM PUBLIC.SERVICE\n";
			sql += "WHERE 1=1\n";
			sql += "AND (ID LIKE '%"+key+"%'\n";
			sql += "OR NAME LIKE '%"+key+"%'\n";
			sql += "OR TYPE LIKE '%"+key+"%')\n";

			if(!context.getInstanceID().equals(""))
			{
				sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			}
			sql += " ORDER BY ID ASC LIMIT 0, 100";
			
			rs = stmt.executeQuery(sql);
			ArrayList<NBFields> list = new ArrayList<NBFields>();
			while(rs.next())
			{
				NBFields service = new NBFields();
				
				service.put("ID"					, rs.getString("ID"));
				service.put("NAME"					, rs.getString("NAME"));
				service.put("TYPE"					, rs.getString("TYPE"));
				service.put("REMARK"				, rs.getString("REMARK"));
				list.add(service);
			}
			
			rtnFields.put("LIST", list);
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
