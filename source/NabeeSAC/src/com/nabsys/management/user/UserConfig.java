package com.nabsys.management.user;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;

public class UserConfig implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();

		if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getUserList(fromClient, context);
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
		else if(fromClient.get("CMD_CODE").equals("S"))
		{
			try {
				setUserList(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "User already exists");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Can't find user information");
			} catch (NoSuchAlgorithmException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting user information.");
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
		else
		{
			toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			toClient.put("RTN_MSG", "Unsuppored command code.");
		}

		return toClient;
	}
	
	private NBFields getUserList(NBFields fields, ManagementContext context) throws SQLException
	{
		NBFields 			rtnValue 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			ArrayList<NBFields> userArray = new ArrayList<NBFields>();
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			if(context.getUserAuthority().equals("Operator"))
			{
				String sql = "SELECT * FROM PUBLIC.USER WHERE (ID LIKE '%"+fields.get("SCH")+"%' OR NAME LIKE '%"+fields.get("SCH")+"%') AND AUTH != 'Admin' AND AUTH != 'Instance' AND AUTH != 'Operator' UNION ALL SELECT * FROM PUBLIC.USER WHERE (ID LIKE '%"+fields.get("SCH")+"%' OR NAME LIKE '%"+fields.get("SCH")+"%') AND AUTH = 'Operator' AND ID = '"+context.getUser()+"' ORDER BY ID ASC LIMIT 0, 100";
				rs = stmt.executeQuery(sql);
			}
			else if(context.getUserAuthority().equals("Admin"))
			{
				rs = stmt.executeQuery("SELECT * FROM PUBLIC.USER WHERE (ID LIKE '%"+fields.get("SCH")+"%' OR NAME LIKE '%"+fields.get("SCH")+"%') AND AUTH != 'Instance' ORDER BY ID ASC");
			}
			else
			{
				rtnValue.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				rtnValue.put("RTN_MSG", "Can't find user information.");	
				return rtnValue;
			}
			
			boolean isExists = false;
			while(rs.next())
			{
				NBFields tmp = new NBFields();
				tmp.put("ID", rs.getString("ID"));
				tmp.put("PW", rs.getString("PW"));
				tmp.put("NAME", rs.getString("NAME"));
				tmp.put("ROLE", rs.getString("AUTH"));
				tmp.put("PHONE", rs.getString("PHONE"));
				tmp.put("ATV", rs.getString("ACTIVATION").trim());
				
				userArray.add(tmp);
				isExists = true;
			}
			
			if(!isExists)
			{
				rtnValue.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				rtnValue.put("RTN_MSG", "Can't find user information.");
			}
			else
			{
				rtnValue.put("USR_LST", userArray);
			}
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnValue;
		
	}
	
	@SuppressWarnings("unchecked")
	private void setUserList(NBFields fields, ManagementContext context) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			if(fields.containsKey("ACT_LST"))
			{
				ArrayList<NBFields> actList = (ArrayList<NBFields>)fields.get("ACT_LST");
				for(int i=0; i<actList.size(); i++)
				{
					NBFields act = actList.get(i);
					if(act.get("ACT").equals("I"))
					{
						rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.USER WHERE ID = '"+act.get("ID")+"'");
						rs.next();
						if(rs.getInt(1) >= 1) throw new KeyDuplicateException(0x0024); 
						
						String sql = "INSERT INTO PUBLIC.USER VALUES (";
						sql += "'"+act.get("ID")+"',";
						sql += "'"+act.get("NAME")+"',";
						sql += "'"+Hash.getMD5Hash((String)act.get("PW"))+"',";
						sql += "'"+act.get("ROLE")+"',";
						sql += "'"+act.get("PHONE")+"',";
						sql += "'"+act.get("ATV")+"')";
						stmt.executeUpdate(sql);
						
						logger.info(NLabel.get(0x007A) + " [" + act.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
					}
					else if(act.get("ACT").equals("U"))
					{
						rs = stmt.executeQuery("SELECT COUNT(*) FROM PUBLIC.USER WHERE ID = '"+act.get("ID")+"'");
						rs.next();
						if(rs.getInt(1) <= 0) throw new NotFoundException(); 
						
						String sql = "UPDATE PUBLIC.USER SET ";
						sql += "ID = '"+act.get("ID")+"',";
						sql += "NAME = '"+act.get("NAME")+"',";
						sql += "PW = '"+Hash.getMD5Hash((String)act.get("PW"))+"',";
						sql += "AUTH = '"+act.get("ROLE")+"',";
						sql += "PHONE = '"+act.get("PHONE")+"',";
						sql += "ACTIVATION = '"+act.get("ATV")+"' ";
						sql += "WHERE ID = '" +act.get("ID")+ "'";
						stmt.executeUpdate(sql);
						logger.info(NLabel.get(0x007B) + " [" + act.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
					}
					else if(act.get("ACT").equals("D"))
					{
						stmt.executeUpdate("DELETE FROM PUBLIC.USER WHERE ID = '" +act.get("ID")+ "'");
						
						logger.info(NLabel.get(0x007C) + " [" + act.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
					}
				}
				connection.commit();
			}
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(NotFoundException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(KeyDuplicateException e){
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
