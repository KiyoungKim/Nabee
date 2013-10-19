package com.nabsys.process.instance.management.back;

import java.sql.Connection;
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

public class TelegramManagement {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;
	
	public TelegramManagement(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public NBFields execute(NBFields fields)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fields.get(IPC.NB_MGR_SQNC));
		toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_TLGM_UPDATE);
		
		if(fields.get("CMD_CODE").equals("S"))
		{
			try {
				setTelegram(fields);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (KeyDuplicateException e) {
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
		return toClient;
	}
	
	@SuppressWarnings("unchecked")
	private void setTelegram(NBFields fromClient) throws Exception
	{
		DBPoolManager 		configDBPool 	= resourceFactory.getConfigDBPool();
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
				rs = stmt.executeQuery("SELECT COUNT(*) CNT FROM TELEGRAM WHERE ID = '"+fromClient.get("ID")+"'");
				rs.next();
				int count = rs.getInt("CNT");
				if(((String)fromClient.get("ACT_FLG")).equals("I"))
				{
					if(count >= 1) throw new KeyDuplicateException(0x0024);
					sql += "INSERT INTO TELEGRAM VALUES (\n";
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
					sql += "WHERE ID = '"+fromClient.get("ID")+"'";
				}
				
				if(isExists)
				{
					stmt.executeUpdate(sql);
					if(((String)fromClient.get("ACT_FLG")).equals("I"))
					{
						logger.info(NLabel.get(0x0083) + " [" + fromClient.get("ID") + "]");
					}
					else if(((String)fromClient.get("ACT_FLG")).equals("U"))
					{
						logger.info(NLabel.get(0x0084) + " [" + fromClient.get("ID") + "]");
					}
				}
				
				if(fromClient.containsKey("FLD"))
				{
					ArrayList<NBFields> fieldList = (ArrayList<NBFields>)fromClient.get("FLD");
					if(((String)fromClient.get("ACT_FLG")).equals("U"))
					{
						stmt.executeUpdate("DELETE FROM TELEGRAM_FIELDS WHERE TELEGRAM_ID = '"+fromClient.get("ID")+"'");
					}
					
					for(int i=0; i<fieldList.size(); i++)
					{
						NBFields fields = fieldList.get(i);
						char padding;
						if(((String)fields.get("PADDING")).equals("")) padding = ' ';
						else padding = ((String)fields.get("PADDING")).toCharArray()[0];
						
						sql = "INSERT INTO TELEGRAM_FIELDS VALUES (";
						sql += "'"+fromClient.get("ID")+"','"+fields.get("ID")+"','"+fields.get("NAME")+"',"+i+","+fields.get("LENGTH")+",'"+fields.get("MANDATORY")+"','"+padding+"','"+((String)fields.get("ALIGN")).toCharArray()[0]+"','"+(((String)fields.get("TYPE")).equals("BA")?((String)fields.get("TYPE")).toCharArray()[1]:((String)fields.get("TYPE")).toCharArray()[0])+"','"+((String)fields.get("REMARK")).replaceAll("'", "''")+"')";
						stmt.executeUpdate(sql);
					}
				}
			}
			
			if(fromClient.containsKey("DEL_LST"))
			{
				ArrayList<NBFields> delList = (ArrayList<NBFields>)fromClient.get("DEL_LST");
				for(int i=0; i<delList.size(); i++)
				{
					NBFields fields = delList.get(i);
					stmt.executeUpdate("DELETE FROM TELEGRAM_FIELDS WHERE TELEGRAM_ID = '"+fields.get("ID")+"'");
					stmt.executeUpdate("DELETE FROM TELEGRAM WHERE ID = '"+fields.get("ID")+"'");
					logger.info(NLabel.get(0x0085) + " [" + fromClient.get("ID") + "]");
				}
			}
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null) connection.rollback();
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
