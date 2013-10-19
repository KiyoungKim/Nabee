package com.nabsys.management.sql;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.fileio.ObjectFileIO;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.database.TagDocument;
import com.nabsys.database.TagParser;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;
import com.nabsys.resource.DOMConfigurator;
import com.nabsys.resource.ServerConfiguration;

public class QueryConfig implements IManagementClass{

	private final NLogger logger = NLogger.getLogger(this.getClass().getName());
	private final int DOCUMENT = 0;
	private final int FOLDER = 1;
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();
				
		if(fromClient.get("CMD_CODE").equals("L"))
		{
			try {
				toClient = getList(fromClient, context);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting SQL information");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
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
		else if(fromClient.get("CMD_CODE").equals("N"))
		{
			try {
				genNewObject(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch(Exception e){
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting SQL information");
			}
		}
		else if(fromClient.get("CMD_CODE").equals("S")) 
		{
			try {
				saveSQL(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting SQL information");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting SQL information");
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
		else if(fromClient.get("CMD_CODE").equals("D"))
		{
			try {
				deleteObject(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch(NullPointerException e){
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch(Exception e){
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting SQL information");
			}
		}
		else if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				renameObject(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("E"))
		{
			fromClient.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SQL);
			fromClient.put(IPC.NB_MGR_SQNC, clientSequence);

			try {
				logger.info("Send SQL to instance [" + context.getInstanceID() + "]");

				context.getInstances().NBgetProtocol(context.getInstanceID())._writePacket(fromClient);

				return null;
			} catch (SocketClosedException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (TimeoutException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NetException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (UnsupportedEncodingException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ProtocolException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (DataTypeException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("H"))  //Search
		{
			try {
				toClient = searchReqeust(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			}catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("C"))  //Contents Search
		{
			try {
				toClient = searchContents(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			}catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}

		return toClient;
	}
	
	private void renameObject(NBFields fromClient, ManagementContext context) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement			stmt 			= null;
		ResultSet			rs				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String id = ((String)fromClient.get("PATH")).trim();
			int rowUpdated = 0;
			if(!id.equals(""))
			{
				String newID = id.substring(0, id.lastIndexOf(".") + 1) + fromClient.get("ID");
				rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+newID+"'");
				rs.next();
				if(rs.getInt(1) >= 1) throw new KeyDuplicateException(0x0024);
				
				String sql = "INSERT INTO PUBLIC.SQL_STORAGE SELECT INSTANCE, REPLACE(ID, '"+id+"', '"+newID+"') ID, LEVEL, TYPE, NEED_PARAM, CONTENTS, TAG_DOCUMENT FROM SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%'";
				rowUpdated = stmt.executeUpdate(sql);
				
				sql = "INSERT INTO PUBLIC.SQL_STORAGE_H SELECT INSTANCE, REPLACE(ID, '"+id+"', '"+newID+"') ID, CURRENT_TIMESTAMP, LEVEL, TYPE, NEED_PARAM, CONTENTS FROM SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%'";
				stmt.executeUpdate(sql);
				
				sql = "DELETE FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%'";
				stmt.executeUpdate(sql);
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0079) + " [" + id + "=>" + fromClient.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
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
	}
	
	private void deleteObject(NBFields fromClient, ManagementContext context) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement			stmt 			= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String id = ((String)fromClient.get("PATH")).trim();
			int rowUpdated = 0;
			if(!id.equals(""))
			{
				String sql = "DELETE FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%'";
				rowUpdated = stmt.executeUpdate(sql);
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0078) + " [" + id + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
	}
	
	private void saveSQL(NBFields fromClient, ManagementContext context) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		PreparedStatement	pstmt 			= null;
		Statement			stmt 			= null;
		try{
			connection = configDBPool.getConnection();
			String id = ((String)fromClient.get("PATH")).trim();
			int rowUpdated = 0;
			if(!id.equals(""))
			{
				String contents = ((String)fromClient.get("SQL")).replaceAll("'", "''");
				TagParser parser = new TagParser();
				TagDocument tagDoc = parser.parse((String)fromClient.get("SQL"));
				boolean isNeedParam = parser.isNeedParam();
				ObjectFileIO fio = new ObjectFileIO();
				byte[] tagInfoByte = fio.convertToByte(tagDoc);
				
				String sql = "UPDATE PUBLIC.SQL_STORAGE SET CONTENTS = '"+contents+"', NEED_PARAM = '"+isNeedParam+"', TAG_DOCUMENT = ? WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'";
				pstmt = connection.prepareStatement(sql);
				pstmt.setObject(1, tagInfoByte);
				rowUpdated = pstmt.executeUpdate();
				
				stmt = connection.createStatement();
				stmt.executeUpdate("INSERT INTO SQL_STORAGE_H SELECT INSTANCE, ID, CURRENT_TIMESTAMP MODIFY_TIME ,LEVEL, TYPE, NEED_PARAM, CONTENTS FROM SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'");
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0077) + " [" + id + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(pstmt != null) pstmt.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
	}
	
	private void genNewObject(NBFields fromClient, ManagementContext context) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String id = ((String)fromClient.get("PATH")).trim();
			if(id.equals(""))
				id = fromClient.get("ID") + "";
			else
				id = id + "." + fromClient.get("ID");

			rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'");
			rs.next();
			if(rs.getInt(1) >= 1) throw new KeyDuplicateException(0x0024);
			
			if(!id.equals(""))
			{
				stmt.executeUpdate("INSERT INTO SQL_STORAGE VALUES('"+context.getInstanceID()+"','"+id+"',"+(id.split("\\.").length)+",'"+(((Integer)fromClient.get("TYPE")) == DOCUMENT?"DOCUMENT":"FOLDER")+"','false','',null)");
				stmt.executeUpdate("INSERT INTO SQL_STORAGE_H SELECT INSTANCE, ID, CURRENT_TIMESTAMP MODIFY_TIME ,LEVEL, TYPE, NEED_PARAM, CONTENTS FROM SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'");
			}
			connection.commit();
			
			if((Integer)fromClient.get("TYPE") == DOCUMENT) logger.info(NLabel.get(0x0075) + " [" + id + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
			else logger.info(NLabel.get(0x0076) + " [" + id + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
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
	}
	
	private NBFields searchReqeust(NBFields fromClient, ManagementContext context) throws SQLException
	{
		NBFields toClient = new NBFields();
		
		if(fromClient.containsKey("STATE") && ((String)fromClient.get("STATE")).equals("END"))
		{
			toClient.put("STATE", "END");
			return toClient;
		}
		
		boolean caseSensitive = ((String)fromClient.get("CASE")).equals("true");
		boolean findContents = ((String)fromClient.get("CNTS")).equals("true");
		boolean findName = ((String)fromClient.get("FLDR")).equals("true");
		String id = ((String)fromClient.get("PATH")).trim();
		String key = (String)fromClient.get("SCH_KEY");
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
		
			String sql = "SELECT ID, TYPE, LEVEL FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%' \n";
			if(findContents || findName)
			{
				sql += " AND (";
				if(findName)
					sql += (caseSensitive?"ID":"UPPER(ID)")+" LIKE '%"+key.toUpperCase()+"%'";
				if(findContents)
				{
					if(findName) sql += " OR ";
					sql += (caseSensitive?"CONTENTS":"UPPER(CONTENTS)")+" LIKE '%"+key.toUpperCase()+"%'";
				}
				sql += ")";
			}
			sql += " ORDER BY ID";

			rs = stmt.executeQuery(sql);
			ArrayList<NBFields> schList = new ArrayList<NBFields>();
			int cnt = 0;
			while(rs.next())
			{
				NBFields tmp = new NBFields();
				tmp.put("TYPE", rs.getString("TYPE"));
				tmp.put("PATH" , rs.getString("ID"));
				schList.add(tmp);
				cnt++;
			}
			toClient.put("CNT", cnt);
			toClient.put("SCH_LIST", schList);

		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return toClient;
	}
	
	private NBFields searchContents(NBFields fromClient, ManagementContext context) throws SQLException
	{
		NBFields toClient = new NBFields();
		ArrayList<NBFields> list = new ArrayList<NBFields>();
		boolean caseSensitive = ((String)fromClient.get("CASE")).equals("true");
		String key = (String)fromClient.get("SCH_KEY");
		String id = ((String)fromClient.get("PATH")).trim();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT CONTENTS FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()) throw new NotFoundException();
			
			String contents = rs.getString("CONTENTS");
			Pattern pattern = Pattern.compile(".*"+(caseSensitive?key:key.toUpperCase())+"+.*");
			Matcher matcher = pattern.matcher(caseSensitive?contents:contents.toUpperCase());
			
			while(matcher.find())
			{
				NBFields tmp = new NBFields();
				tmp.put("FIND_STR", contents.substring(matcher.start(), matcher.end()));
				tmp.put("MATCH_START", matcher.start());
				list.add(tmp);
			}
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		toClient.put("STR_LIST", list);
		return toClient;
	}
	
	private NBFields getList(NBFields fromClient, ManagementContext context) throws SQLException, ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields 			toClient 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			ArrayList<NBFields> listFields = new ArrayList<NBFields>();
			String id = ((String)fromClient.get("PATH")).trim();

			if(!id.equals(""))
			{
				rs = stmt.executeQuery("SELECT * FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+id+"'");
			
				if(!rs.next()) throw new NotFoundException();
			
				if(rs.getString("TYPE").equals("DOCUMENT"))
				{
					toClient.put("SQL", rs.getString("CONTENTS"));
					
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
								tmp.put("PLG_NM", rs.getString("PLUGIN_NAME"));
								pluginList.add(tmp);
								break;
							}
						}
					}
					toClient.put("PLG_LST", pluginList);
				}
				else //FOLDER
				{
					rs = stmt.executeQuery("SELECT * FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID LIKE '"+id+"%' AND LEVEL = " + (rs.getInt("LEVEL") + 1) + " ORDER BY ID ASC");
					while(rs.next())
					{
						NBFields tmp = new NBFields();
						tmp.put("TYPE", rs.getString("TYPE").equals("DOCUMENT")?DOCUMENT:FOLDER);
						tmp.put("ID", rs.getString("ID").substring(rs.getString("ID").lastIndexOf(".") + 1, rs.getString("ID").length()));
						
						listFields.add(tmp);
					}
				}
			}
			else
			{
				rs = stmt.executeQuery("SELECT * FROM PUBLIC.SQL_STORAGE WHERE INSTANCE = '"+context.getInstanceID()+"' AND LEVEL = 1 ORDER BY ID ASC");
				while(rs.next())
				{
					NBFields tmp = new NBFields();
					tmp.put("TYPE", rs.getString("TYPE").equals("DOCUMENT")?DOCUMENT:FOLDER);
					tmp.put("ID", rs.getString("ID"));
					
					listFields.add(tmp);
				}
			}
			toClient.put("LIST", listFields);
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return toClient;
	}
}
