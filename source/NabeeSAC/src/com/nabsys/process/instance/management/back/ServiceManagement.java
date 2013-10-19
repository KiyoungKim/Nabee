package com.nabsys.process.instance.management.back;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.ResourceFactory;

public class ServiceManagement {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;
	
	public ServiceManagement(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public NBFields execute(NBFields fields)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fields.get(IPC.NB_MGR_SQNC));
		toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_COMP_UPDATE);
		
		if(fields.get("CMD_CODE").equals("S"))
		{
			try {
				setServiceConfig(fields);
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
		else if(fields.get("CMD_CODE").equals("D"))
		{
			try {
				removeService(fields);
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
		
		return toClient;
	}
	
	private void setServiceConfig(NBFields fields) throws SQLException
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
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
			sql += "AND ID = '"+(String)fields.get("ID")+"'";
			
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
				sql += "WHERE ID = '" + fields.get("ID") +"'";
			}
			else
			{
				sql = "INSERT INTO PUBLIC.SERVICE\n";
				sql += "VALUES(\n";
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
	}
	
	private void removeService(NBFields fields) throws SQLException
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "";
			sql += "DELETE FROM PUBLIC.SERVICE\n";
			sql += "WHERE 1=1\n";
			sql += "AND ID = '"+(String)fields.get("ID")+"'";
			
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
	}
}
