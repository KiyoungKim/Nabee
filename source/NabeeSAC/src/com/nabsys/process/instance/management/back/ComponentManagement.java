package com.nabsys.process.instance.management.back;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.ResourceFactory;

public class ComponentManagement {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;
	
	public ComponentManagement(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public NBFields execute(NBFields fields)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fields.get(IPC.NB_MGR_SQNC));
		toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_COMP_UPDATE);
		
		if(fields.get("CMD_CODE").equals("D"))
		{
			try {
				deleteComponent(fields);
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
		else if(fields.get("CMD_CODE").equals("I"))
		{
			try {
				newComponent(fields);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ClassNotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
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
		else if(fields.get("CMD_CODE").equals("U"))
		{
			try {
				modifyComponent(fields);
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
		
		return toClient;
	}
	
	private void modifyComponent(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FILE_PATH, SHARE FROM PUBLIC.COMPONENT WHERE ID = '"+fromClient.get("ID")+"'";
			rs = stmt.executeQuery(sql);
			
			if(!rs.next()) throw new NotFoundException();
			
			sql = "UPDATE PUBLIC.COMPONENT SET\n";
			sql += "ID = '"+fromClient.get("ID")+"'\n";
			boolean chkExist = false;
			if(fromClient.containsKey("SAVE_PATH"))
			{
				sql += ",FILE_PATH = '"+fromClient.get("SAVE_PATH")+"'\n";
				chkExist = true;
			}
			if(fromClient.containsKey("CLASS"))
			{
				sql += ",CLASS_NAME = '"+fromClient.get("CLASS")+"'\n";
				chkExist = true;
			}
			if(fromClient.containsKey("NAME"))
			{
				sql +=  ",NAME = '"+fromClient.get("NAME")+"'\n";
				chkExist = true;
			}
			sql += "WHERE ID = '"+fromClient.get("ID")+"'";
			if(chkExist)
			{
				stmt.executeUpdate(sql);
				connection.commit();
				logger.info(NLabel.get(0x0087) + " [" + fromClient.get("ID") + "]");
			}
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
	
	private void newComponent(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
		Connection			sourceConnection = null;
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		Statement 			sourceStmt		= null;
		ResultSet 			rs 				= null;
		ResultSet 			sourceRs		= null;
		
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			if(fromClient.containsKey("SHARE") && ((String)fromClient.get("SHARE")).equals("true"))
			{
				Class.forName("org.hsqldb.jdbc.JDBCDriver" );
				sourceConnection = DriverManager.getConnection("jdbc:hsqldb:file:F:/NabeePathway/NabeeSAC/data/NABEE;readonly=true", "nabeeconfigdatabase", "#!nabeeConfigDatabase#!");
				sourceStmt = sourceConnection.createStatement();
				sourceRs = sourceStmt.executeQuery("SELECT * FROM PUBLIC.COMPONENT WHERE INSTANCE = '"+fromClient.get("SRC_INS_NAME")+"' AND ID = '"+fromClient.get("ID")+"'");
				if(!sourceRs.next()) throw new NotFoundException();
				
				String sql = "INSERT INTO PUBLIC.COMPONENT VALUES (\n";
				sql += "'"+fromClient.get("ID")+"',";
				sql += "'"+fromClient.get("NAME")+"',";
				sql += "'"+sourceRs.getString("CLASS_NAME")+"',";
				sql += "'"+sourceRs.getString("FILE_PATH")+"',";
				sql += "'"+sourceRs.getString("SHARE").trim()+"'";
				sql += ")";
				stmt.executeUpdate(sql);
			}
			else
			{
				String sql = "INSERT INTO PUBLIC.COMPONENT VALUES (\n";
				sql += "'"+fromClient.get("ID")+"',";
				sql += "'"+fromClient.get("NAME")+"',";
				sql += "'"+fromClient.get("CLASS")+"',";
				sql += "'"+fromClient.get("SAVE_PATH")+"',";
				sql += "'false'";
				sql += ")";
				stmt.executeUpdate(sql);
			}
			connection.commit();
			logger.info(NLabel.get(0x0086) + " [" + fromClient.get("ID") + "]");
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(sourceRs != null) sourceRs.close();
			if(sourceStmt != null) sourceStmt.close();
			if(sourceConnection != null) sourceConnection.close();
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteComponent(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			ArrayList<NBFields> delList = (ArrayList<NBFields>)fromClient.get("DEL_LST");
			
			for(int i=0; i<delList.size(); i++)
			{
				String sql = "DELETE FROM PUBLIC.COMPONENT WHERE ID = '"+delList.get(i).get("ID")+"'";
				stmt.executeUpdate(sql);
				logger.info("[Component : " + delList.get(i).get("ID") + "] " + NLabel.get(0x0098));
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
	}
}
