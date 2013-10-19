package com.nabsys.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nabsys.database.DBPoolManager;
import com.nabsys.resource.TelegramContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class MemDBPerf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DBPoolManager pool = new DBPoolManager();
		pool.setPoolName("CONFIG");
		pool.setDBDriver("org.hsqldb.jdbc.JDBCDriver");
		pool.setDBUrl("jdbc:hsqldb:hsql://localhost:9002/NABEE");
		pool.setDriverUrl("jdbc:apache:commons:dbcp:");
		pool.setUser("nabeeconfigdatabase");
		pool.setPassword("#!nabeeConfigDatabase#!");
		pool.setMaxPoolActive(1);
		pool.setPoolIdle(1);
		pool.setPoolMaxWait(2000);
		pool.setIsAutoCommit(false);
		pool.setIsReadOnly(false);
		try {
			pool.initConnectionPool();
			Connection connection = null;
			Statement stmt = null;
			
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			int seconds = 1;
			int num = 0;
			while((end - start) <= 1000 * seconds ){
				try{
					
					connection = pool.getConnection();
					stmt = connection.createStatement();
					stmt.executeQuery("SELECT * FROM SERVICE WHERE INSTANCE = 'ReadByLengthField' AND ID = 'PERN_TEST' ");
					if(connection != null) connection.close();
					
					
					connection = pool.getConnection();
					stmt = connection.createStatement();
					stmt.executeQuery("SELECT * FROM SQL_STORAGE WHERE INSTANCE = 'ReadByLengthField' AND ID = 'member_login' ");
					if(connection != null) connection.close();
					
					
					connection = pool.getConnection();
					stmt = connection.createStatement();
					stmt.executeQuery("SELECT * FROM SQL_STORAGE WHERE INSTANCE = 'ReadByLengthField' AND ID = 'member_login' ");
					if(connection != null) connection.close();
					
					for(int i=0; i<2; i++)
					{
						connection = pool.getConnection();
						stmt = connection.createStatement();
						String 	sql = "SELECT TG.ID TGID, TG.NAME TGNM, TG.HEADER_ID TGHDNM, TG.LOG_LEVEL TGLLV,\n";
								sql += "FD.ID FDID, FD.NAME FDNM, FD.INDEX FDIDX, FD.LENGTH FDLTH,\n";
								sql += "FD.MANDATORY FDMDT, FD.PADDING FDPD, FD.ALIGN FDALG, FD.TYPE FDTP\n";
								sql += "FROM TELEGRAM TG LEFT OUTER JOIN TELEGRAM_FIELDS FD \n";
								sql += "ON TG.ID = FD.TELEGRAM_ID AND TG.INSTANCE = FD.INSTANCE\n";
								sql += "WHERE TG.ID = 'MEMBER_LOGIN' AND TG.INSTANCE = 'ReadByLengthField'\n";
								sql += "ORDER BY FD.INDEX";
						ResultSet rs = stmt.executeQuery(sql);
						TelegramFieldContextList list = new TelegramFieldContextList();
						boolean chkTG = false;
						while(rs.next())
						{
							if(!chkTG)
							{
								new TelegramContext(rs.getString("TGID"), rs.getString("TGNM"), rs.getString("TGHDNM"), rs.getString("TGLLV").toCharArray()[0], "", list);
								chkTG = true;
							}
							
							if(rs.getString("FDID") != null && !rs.getString("FDID").equals(""))
							{
								list.put(rs.getString("FDID"), new TelegramFieldContext(rs.getString("FDID"), rs.getString("FDNM"), rs.getInt("FDIDX"), rs.getInt("FDLTH"), rs.getString("FDMDT").trim().equals("true"), rs.getString("FDPD").toCharArray()[0], rs.getString("FDALG").toCharArray()[0], rs.getString("FDTP").toCharArray()[0], ""));
							}
						}
						
						if(connection != null) connection.close();
					}
					num++;
					
				} finally {
					end = System.currentTimeMillis();
				}
			}
			
			System.out.println("Total transactions : " + num);
			System.out.println("Transactions per a Second : " + (num/seconds) * 5);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

}
