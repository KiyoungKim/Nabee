package com.nabsys.process.instance.management.back;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.fileio.ObjectFileIO;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.database.SimpleQuery;
import com.nabsys.database.TagDocument;
import com.nabsys.database.TagParser;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.ResourceFactory;

public class SqlManagement {
	
	private final NLogger logger = NLogger.getLogger(this.getClass());

	private final int DOCUMENT = 0;
	private ResourceFactory resourceFactory = null;
	
	public SqlManagement(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	@SuppressWarnings("unchecked")
	public NBFields execute(NBFields fromClient)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fromClient.get(IPC.NB_MGR_SQNC));
		
		if(fromClient.get("CMD_CODE").equals("N")) //�űԵ��
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_SQL_UPDATE);
			try {
				genNewObject(fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
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
		else if(fromClient.get("CMD_CODE").equals("S")) //SQL ����
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_SQL_UPDATE);
			try {
				saveSQL(fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (IOException e) {
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
		else if(fromClient.get("CMD_CODE").equals("D")) //����
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_SQL_UPDATE);
			try {
				deleteObject(fromClient);
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
		else if(fromClient.get("CMD_CODE").equals("R")) //�̸�����
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_SQL_UPDATE);
			try {
				renameObject(fromClient);
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
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SQL);
			SimpleQuery query = null;
			try {
				ArrayList<NBFields> paramList = (ArrayList<NBFields>)fromClient.get("PARAM");
				NBFields param = new NBFields();
				if(paramList.size() > 0)
					param = ((ArrayList<NBFields>)fromClient.get("PARAM")).get(0);

				query = new SimpleQuery(resourceFactory);
				query.setConnection((String)fromClient.get("PLUGIN"));
				
				toClient.putAll(query.execute((String)fromClient.get("URL"), param));
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Null Exception");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} finally {
				try {
					query.close();
				} catch (SQLException e) {
					logger.error(e, e.getMessage());
				} catch (NullPointerException e) {
				}
			}
		}

		return toClient;
	}
	
	private void renameObject(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
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
				rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.SQL_STORAGE WHERE ID = '"+newID+"'");
				rs.next();
				if(rs.getInt(1) >= 1) throw new KeyDuplicateException(0x0024);
				
				String sql = "INSERT INTO PUBLIC.SQL_STORAGE SELECT INSTANCE, REPLACE(ID, '"+id+"', '"+newID+"') ID, LEVEL, TYPE, NEED_PARAM, CONTENTS, TAG_DOCUMENT FROM SQL_STORAGE WHERE ID LIKE '"+id+"%'";
				rowUpdated = stmt.executeUpdate(sql);
				
				sql = "DELETE FROM PUBLIC.SQL_STORAGE WHERE ID LIKE '"+id+"%'";
				stmt.executeUpdate(sql);
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0079) + " [" + fromClient.get("PATH") + "=>" + fromClient.get("ID") + "]");
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
	
	private void deleteObject(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
		Connection 			connection 		= null;
		Statement			stmt 			= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String id = ((String)fromClient.get("PATH")).trim();
			int rowUpdated = 0;
			if(!id.equals(""))
			{
				String sql = "DELETE FROM PUBLIC.SQL_STORAGE WHERE ID LIKE '"+id+"%'";
				rowUpdated = stmt.executeUpdate(sql);
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0078) + " [" + fromClient.get("PATH") + "]");
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
	
	private void saveSQL(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
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
				
				String sql = "UPDATE PUBLIC.SQL_STORAGE SET CONTENTS = '"+contents+"', NEED_PARAM = '"+isNeedParam+"', TAG_DOCUMENT = ? WHERE ID = '"+id+"'";
				pstmt = connection.prepareStatement(sql);
				pstmt.setObject(1, tagInfoByte);
				rowUpdated = pstmt.executeUpdate();
			}
			connection.commit();
			if(rowUpdated <= 0) throw new NotFoundException();
			logger.info(NLabel.get(0x0077) + " [" + id + "]");
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
	
	private void genNewObject(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
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
			
			rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.SQL_STORAGE WHERE ID = '"+id+"'");
			rs.next();
			if(rs.getInt(1) >= 1) throw new KeyDuplicateException(0x0024);
			
			if(!id.equals(""))
			{
				stmt.executeUpdate("INSERT INTO SQL_STORAGE VALUES('"+id+"',"+(id.split("\\.").length)+",'"+(((Integer)fromClient.get("TYPE")) == DOCUMENT?"DOCUMENT":"FOLDER")+"','false','',null)");
			}
			connection.commit();
			
			if((Integer)fromClient.get("TYPE") == DOCUMENT) logger.info(NLabel.get(0x0075) + " [" + (String)fromClient.get("ID") + "]");
			else logger.info(NLabel.get(0x0076) + " [" + (String)fromClient.get("ID") + "]");
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
}
