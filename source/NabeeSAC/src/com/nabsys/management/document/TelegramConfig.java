package com.nabsys.management.document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.DateUtil;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;

public class TelegramConfig implements IManagementClass{
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());

	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();

		if(fromClient.get("CMD_CODE").equals("L"))
		{
			try {
				toClient = getTelegramlist(context, fromClient);
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
		else if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getTelegramConfig(context, fromClient);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
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
				setTelegram(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting telegram information.");
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
		else if(fromClient.get("CMD_CODE").equals("RP"))
		{
			try {
				toClient = getTelegramPacket(context, fromClient);
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting telegram information.");
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
		
		return toClient;
	}
	
	@SuppressWarnings("unchecked")
	private void setTelegram(ManagementContext context, NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			
			if(fromClient.containsKey("ACT_FLG"))
			{
				boolean isExists = false;
				String sql = "";
				rs = stmt.executeQuery("SELECT COUNT(*) CNT FROM TELEGRAM WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+fromClient.get("ID")+"'");
				rs.next();
				int count = rs.getInt("CNT");
				if(((String)fromClient.get("ACT_FLG")).equals("I"))
				{
					if(count >= 1) throw new KeyDuplicateException(0x0024);
					sql += "INSERT INTO TELEGRAM VALUES (\n";
					sql += "'"+context.getInstanceID()+"'";
					sql += ",'"+fromClient.get("ID")+"'\n";
					sql += ",'"+(fromClient.containsKey("NAME")?fromClient.get("NAME"):"")+"'\n";
					sql += ",'"+(fromClient.containsKey("HDR")?fromClient.get("HDR"):"")+"'\n";
					sql += ",'"+(fromClient.containsKey("LGNG")?((String)fromClient.get("LGNG")).toCharArray()[0]:"")+"'\n";
					sql += ",'"+(fromClient.containsKey("RMK")?((String)fromClient.get("RMK")).replaceAll("'", "''"):"")+"'\n";
					sql += ")";
					isExists = true;
				}
				else if(((String)fromClient.get("ACT_FLG")).equals("U"))
				{
					if(count <= 0) throw new NotFoundException();
					sql += "UPDATE TELEGRAM SET \n";
					sql += "ID = '"+fromClient.get("ID")+"'\n";
					if(fromClient.containsKey("HDR"))
					{
						sql += ",HEADER_ID = '"+fromClient.get("HDR")+"'\n";
						isExists = true;
					}
					if(fromClient.containsKey("LGNG"))
					{
						sql += ",LOG_LEVEL = '"+((String)fromClient.get("LGNG")).toCharArray()[0]+"'\n";
						isExists = true;
					}
					if(fromClient.containsKey("NAME"))
					{
						sql += ",NAME = '"+fromClient.get("NAME")+"'\n";
						isExists = true;
					}
					if(fromClient.containsKey("RMK"))
					{
						sql += ",REMARK = '"+((String)fromClient.get("RMK")).replaceAll("'", "''")+"'\n";
						isExists = true;
					}
					sql += "WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+fromClient.get("ID")+"'";
				}
				DateUtil du = new DateUtil();
				String timeStamp = du.getCurrentDate("yyyy-MM-dd HH:mm:ss.SS");
				if(isExists)
				{
					stmt.executeUpdate(sql);
					stmt.executeUpdate("INSERT INTO TELEGRAM_H SELECT INSTANCE, ID, '"+timeStamp+"' MODIFY_TIME, NAME, HEADER_ID, LOG_LEVEL, REMARK FROM TELEGRAM WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+fromClient.get("ID")+"'");
					if(((String)fromClient.get("ACT_FLG")).equals("I"))
					{
						logger.info(NLabel.get(0x0083) + " [" + fromClient.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
					}
					else if(((String)fromClient.get("ACT_FLG")).equals("U"))
					{
						logger.info(NLabel.get(0x0084) + " [" + fromClient.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
					}
				}
				
				if(fromClient.containsKey("FLD"))
				{
					ArrayList<NBFields> fieldList = (ArrayList<NBFields>)fromClient.get("FLD");
					if(((String)fromClient.get("ACT_FLG")).equals("U"))
					{
						stmt.executeUpdate("DELETE FROM TELEGRAM_FIELDS WHERE INSTANCE = '"+context.getInstanceID()+"' AND TELEGRAM_ID = '"+fromClient.get("ID")+"'");
					}
					
					if(!isExists)//필드만 수정 된 경우는 전문 히스토리를 따로 등록 해 주어야 됨.
					{
						stmt.executeUpdate("INSERT INTO TELEGRAM_H SELECT INSTANCE, ID, '"+timeStamp+"' MODIFY_TIME, NAME, HEADER_ID, LOG_LEVEL, REMARK FROM TELEGRAM WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+fromClient.get("ID")+"'");
					}

					for(int i=0; i<fieldList.size(); i++)
					{
						NBFields fields = fieldList.get(i);
						char padding;
						if(((String)fields.get("PADDING")).equals("")) padding = ' ';
						else padding = ((String)fields.get("PADDING")).toCharArray()[0];
						
						sql = "INSERT INTO TELEGRAM_FIELDS VALUES (";
						sql += "'"+context.getInstanceID()+"','"+fromClient.get("ID")+"','"+fields.get("ID")+"','"+fields.get("NAME")+"',"+i+","+fields.get("LENGTH")+",'"+fields.get("MANDATORY")+"','"+padding+"','"+((String)fields.get("ALIGN")).toCharArray()[0]+"','"+(((String)fields.get("TYPE")).equals("BA")?((String)fields.get("TYPE")).toCharArray()[1]:((String)fields.get("TYPE")).toCharArray()[0])+"','"+((String)fields.get("REMARK")).replaceAll("'", "''")+"')";
						stmt.executeUpdate(sql);
					}
					
					sql = "INSERT INTO TELEGRAM_FIELDS_H\n";
					sql += "SELECT INSTANCE, TELEGRAM_ID, ID, '"+timeStamp+"'MODIFY_TIME, NAME, INDEX, LENGTH, MANDATORY, PADDING, ALIGN, TYPE, REMARK FROM TELEGRAM_FIELDS WHERE INSTANCE = '"+context.getInstanceID()+"' AND TELEGRAM_ID = '"+fromClient.get("ID")+"'";
					stmt.executeUpdate(sql);
				}
			}
			
			if(fromClient.containsKey("DEL_LST"))
			{
				ArrayList<NBFields> delList = (ArrayList<NBFields>)fromClient.get("DEL_LST");
				for(int i=0; i<delList.size(); i++)
				{
					NBFields fields = delList.get(i);
					stmt.executeUpdate("DELETE FROM TELEGRAM_FIELDS WHERE INSTANCE = '"+context.getInstanceID()+"' AND TELEGRAM_ID = '"+fields.get("ID")+"'");
					stmt.executeUpdate("DELETE FROM TELEGRAM WHERE INSTANCE = '"+context.getInstanceID()+"' AND ID = '"+fields.get("ID")+"'");
					logger.info(NLabel.get(0x0085) + " [" + fields.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
				}
			}
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null) connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null) connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
	}
	
	private void getTelegramPacket(ManagementContext context, ArrayList<NBFields> list, String headerID) throws SQLException
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FD.INDEX ,TG.HEADER_ID, \n";
			sql += "FD.ID FDID, FD.NAME FDNAME, FD.TYPE \n";
			sql += "FROM TELEGRAM TG, TELEGRAM_FIELDS FD\n";
			sql += "WHERE TG.ID = FD.TELEGRAM_ID\n";
			sql += "AND TG.INSTANCE = FD.INSTANCE\n";
			sql += "AND TG.ID = '"+headerID+"'\n";
			sql += "AND TG.INSTANCE = '"+context.getInstanceID()+"'\n";
			sql += "ORDER BY FD.INDEX ASC";
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				if(rs.getString("HEADER_ID") != null && !rs.getString("HEADER_ID").equals(""))
				{
					getTelegramPacket(context, list, rs.getString("HEADER_ID"));
				}
				
				NBFields telegramFields = new NBFields();
				
				telegramFields.put("ID"			, rs.getString("FDID"));
				telegramFields.put("NAME"		, rs.getString("FDNAME"));
				telegramFields.put("TYPE"		, rs.getString("TYPE").trim());
				
				list.add(telegramFields);
			}
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
	}
	
	private NBFields getTelegramPacket(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FD.INDEX ,TG.HEADER_ID, \n";
			sql += "FD.ID FDID, FD.NAME FDNAME, FD.TYPE \n";
			sql += "FROM TELEGRAM TG, TELEGRAM_FIELDS FD\n";
			sql += "WHERE TG.ID = FD.TELEGRAM_ID\n";
			sql += "AND TG.INSTANCE = FD.INSTANCE\n";
			sql += "AND TG.ID = '"+fields.get("ID")+"'\n";
			sql += "AND TG.INSTANCE = '"+context.getInstanceID()+"'\n";
			sql += "ORDER BY FD.INDEX ASC";
			rs = stmt.executeQuery(sql);
			
			ArrayList<NBFields> fieldList = new ArrayList<NBFields>();
			boolean isInit = true;
			while(rs.next())
			{
				if(isInit && rs.getString("HEADER_ID") != null && !rs.getString("HEADER_ID").equals(""))
				{
					getTelegramPacket(context, fieldList, rs.getString("HEADER_ID"));
					isInit = false;
				}
				
				NBFields telegramFields = new NBFields();
				
				telegramFields.put("ID"			, rs.getString("FDID"));
				telegramFields.put("NAME"		, rs.getString("FDNAME"));
				telegramFields.put("TYPE"		, rs.getString("TYPE").trim());
				
				fieldList.add(telegramFields);
			}
			rtnFields.put("FLD", fieldList);
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getTelegramConfig(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT TG.INSTANCE, FD.INDEX ,TG.ID TGID, TG.NAME TGNAME, TG.HEADER_ID, TG.LOG_LEVEL, TG.REMARK TGREMARK , \n";
			sql += "FD.ID FDID, FD.NAME FDNAME, FD.LENGTH, FD.MANDATORY, FD.PADDING, FD.ALIGN, FD.TYPE,\n";
			sql += "FD.REMARK FDREMARK\n";
			sql += "FROM TELEGRAM TG, TELEGRAM_FIELDS FD\n";
			sql += "WHERE TG.ID = FD.TELEGRAM_ID\n";
			sql += "AND TG.INSTANCE = FD.INSTANCE\n";
			sql += "AND TG.ID = '"+fields.get("ID")+"'\n";
			sql += "AND TG.INSTANCE = '"+context.getInstanceID()+"'\n";
			sql += "ORDER BY TG.INSTANCE ASC, TG.ID ASC, FD.INDEX ASC";
			rs = stmt.executeQuery(sql);
			
			boolean isFirst = true;
			ArrayList<NBFields> fieldList = new ArrayList<NBFields>();
			while(rs.next())
			{
				if(isFirst)
				{
					rtnFields.put("ID"		, rs.getString("TGID"));
					rtnFields.put("NAME"	, rs.getString("TGNAME"));
					rtnFields.put("HDR"		, rs.getString("HEADER_ID") == null?"":rs.getString("HEADER_ID"));
					rtnFields.put("LGNG"	, rs.getString("LOG_LEVEL").trim());
					rtnFields.put("RMK"		, rs.getString("TGREMARK"));
					isFirst = false;
				}
				
				NBFields telegramFields = new NBFields();
				
				telegramFields.put("ID"			, rs.getString("FDID"));
				telegramFields.put("NAME"		, rs.getString("FDNAME"));
				telegramFields.put("LENGTH"		, rs.getInt("LENGTH"));
				telegramFields.put("MANDATORY"	, rs.getString("MANDATORY").trim());
				telegramFields.put("PADDING"	, rs.getString("PADDING").trim());
				telegramFields.put("ALIGN"		, rs.getString("ALIGN").trim());
				telegramFields.put("TYPE"		, rs.getString("TYPE").trim());
				telegramFields.put("REMARK"		, rs.getString("FDREMARK"));
				
				fieldList.add(telegramFields);
			}
			
			if(isFirst) throw new NotFoundException();
			rtnFields.put("FLD", fieldList);
			
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		return rtnFields;
	}
	
	private NBFields getTelegramlist(ManagementContext context, NBFields fields) throws SQLException
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
			sql += "SELECT INSTANCE, ID, NAME, REMARK FROM PUBLIC.TELEGRAM\n";
			sql += "WHERE 1=1 \n";
			if(((String)fields.get("SCH_TYPE")).equals("SCH_ID"))
			{
				sql += "AND ID LIKE '%"+fields.get("SCH")+"%'\n";
			}
			else if(((String)fields.get("SCH_TYPE")).equals("SCH_NAME"))
			{
				sql += "AND NAME LIKE '%"+fields.get("SCH")+"%'\n";
			}

			if(!context.getInstanceID().equals(""))
			{
				sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			}
			sql += " ORDER BY ID ASC LIMIT 0, 100";
			
			rs = stmt.executeQuery(sql);
			ArrayList<NBFields> telegramList = new ArrayList<NBFields>();
			while(rs.next())
			{
				NBFields telegramFields = new NBFields();
				
				telegramFields.put(IPC.NB_INSTNCE_ID	, rs.getString("INSTANCE"));
				telegramFields.put("ID"					, rs.getString("ID"));
				telegramFields.put("NAME"				, rs.getString("NAME"));
				telegramFields.put("RMK"				, rs.getString("REMARK"));
				
				telegramList.add(telegramFields);
			}
			
			rtnFields.put("TLGM_LST", telegramList);
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
