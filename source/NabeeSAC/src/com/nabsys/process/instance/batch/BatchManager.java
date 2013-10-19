package com.nabsys.process.instance.batch;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.DateUtil;
import com.nabsys.database.DBPoolManager;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;
import com.nabsys.process.ResourceFactory;
import com.nabsys.process.instance.InstanceThread;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.ServiceContextList;
import com.nabsys.resource.service.TimeSector;

public class BatchManager extends InstanceThread {

	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;

	public BatchManager(ResourceFactory resourceFactory) {
		super();
		this.resourceFactory = resourceFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void runWorker() {

		logger.info(0x0061);

		DateUtil dateUtil = new DateUtil(ServerConfiguration.getTimeLocale());
		HashMap<TimeSector, Integer> curTimeMap = new HashMap<TimeSector, Integer>();
		HashMap<TimeSector, Integer> lastTimeMap = null;
		while (!exit) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			Date curTime = new Date();

			try {
				TimeSector[] sectors = TimeSector.values();
				for (int i = 0; i < sectors.length; i++) {
					if (sectors[i] == TimeSector.YER)
						curTimeMap.put(TimeSector.YER,
								dateUtil.getYear(curTime));
					if (sectors[i] == TimeSector.MON)
						curTimeMap.put(TimeSector.MON,
								dateUtil.getMonth(curTime));
					if (sectors[i] == TimeSector.DAY)
						curTimeMap.put(TimeSector.DAY,
								dateUtil.getDate(curTime));
					if (sectors[i] == TimeSector.HUR)
						curTimeMap.put(TimeSector.HUR,
								dateUtil.getHour(curTime));
					if (sectors[i] == TimeSector.MIN)
						curTimeMap.put(TimeSector.MIN,
								dateUtil.getMinute(curTime));
				}

				if (dateUtil.getHour(curTime) == 23
						&& dateUtil.getMinute(curTime) == 30) {
					removeHistory();
				}
			} catch (ParseException e) {
				continue;
			}

			if (lastTimeMap == null) {
				lastTimeMap = (HashMap<TimeSector, Integer>) curTimeMap.clone();
			}

			if (curTimeMap.get(TimeSector.MIN) != lastTimeMap
					.get(TimeSector.MIN)) {
				try {
					ServiceContextList serverContextList = resourceFactory.getBatchServiceList();
					Iterator<String> itr = serverContextList.keySet().iterator();
					Context ctx = new Context(resourceFactory);
					ctx.setPlugins(resourceFactory.getPluginList());
					while (itr.hasNext()) {
						String key = itr.next();
						ctx.setServiceID(key);
						serverContextList.get(key).getServiceDesign().execute(ctx, new NBFields());
					}
				} catch (SQLException e) {
					logger.warn(e, e.getMessage());
				} catch (IOException e) {
					logger.warn(e, e.getMessage());
				} catch (ClassNotFoundException e) {
					logger.warn(e, e.getMessage());
				} catch (Exception e) {
					logger.warn(e, e.getMessage());
				}
				lastTimeMap = (HashMap<TimeSector, Integer>) curTimeMap.clone();
			}
		}

		logger.info(0x0062);
	}

	private void removeHistory() {
		Thread t = new Thread() {
			public void run() {
				DBPoolManager manager = (DBPoolManager) resourceFactory
						.getConfigDBPool();
				Connection connection = null;
				Statement stmt = null;

				try {
					connection = manager.getConnection();
					connection.setAutoCommit(false);
					stmt = connection.createStatement();

					String sql = "";
					sql += "DELETE FROM PUBLIC.TELEGRAM_FIELDS_H\n";
					sql += "WHERE (INSTANCE, TELEGRAM_ID, ID, MODIFY_TIME) IN (\n";
					sql += "SELECT INSTANCE, TELEGRAM_ID, ID, TTL.MODIFY_TIME\n";
					sql += "FROM (\n";
					sql += "SELECT INSTANCE, TELEGRAM_ID, ID, MAX(MT) MT\n";
					sql += "FROM\n";
					sql += "(\n";
					sql += "SELECT  INSTANCE, TELEGRAM_ID, ID,  TO_CHAR(MODIFY_TIME, 'YYYYMMDD') MT\n";
					sql += "FROM PUBLIC.TELEGRAM_FIELDS_H\n";
					sql += "GROUP BY INSTANCE, TELEGRAM_ID, ID, MT\n";
					sql += "ORDER BY INSTANCE, TELEGRAM_ID,  ID, MT DESC\n";
					sql += ")\n";
					sql += "GROUP BY INSTANCE, TELEGRAM_ID, ID\n";
					sql += ") RMN, PUBLIC.TELEGRAM_FIELDS_H TTL\n";
					sql += "WHERE 1 = 1\n";
					sql += "AND RMN.INSTANCE = TTL.INSTANCE\n";
					sql += "AND RMN.TELEGRAM_ID = TTL.TELEGRAM_ID\n";
					sql += "AND RMN.ID = TTL.ID\n";
					sql += "AND RMN.MT != TO_CHAR(TTL.MODIFY_TIME, 'YYYYMMDD'))";
					stmt.executeUpdate(sql);
					sql = "";
					sql += "DELETE FROM PUBLIC.TELEGRAM_H\n";
					sql += "WHERE (INSTANCE, ID, MODIFY_TIME) IN (\n";
					sql += "SELECT INSTANCE, ID, TTL.MODIFY_TIME\n";
					sql += "FROM (\n";
					sql += "SELECT INSTANCE, ID, MAX(MT) MT\n";
					sql += "FROM\n";
					sql += "(\n";
					sql += "SELECT  INSTANCE, ID,  TO_CHAR(MODIFY_TIME, 'YYYYMMDD') MT\n";
					sql += "FROM PUBLIC.TELEGRAM_H\n";
					sql += "GROUP BY INSTANCE, ID, MT\n";
					sql += "ORDER BY INSTANCE, ID, MT DESC\n";
					sql += ")\n";
					sql += "GROUP BY INSTANCE, ID\n";
					sql += ") RMN, PUBLIC.TELEGRAM_H TTL\n";
					sql += "WHERE 1 = 1\n";
					sql += "AND RMN.INSTANCE = TTL.INSTANCE\n";
					sql += "AND RMN.ID = TTL.ID\n";
					sql += "AND RMN.MT != TO_CHAR(TTL.MODIFY_TIME, 'YYYYMMDD'))";
					stmt.executeUpdate(sql);
					sql = "";
					sql += "DELETE FROM PUBLIC.SQL_STORAGE_H\n";
					sql += "WHERE (INSTANCE, ID, MODIFY_TIME) IN (\n";
					sql += "SELECT INSTANCE, ID, TTL.MODIFY_TIME\n";
					sql += "FROM (\n";
					sql += "SELECT INSTANCE, ID, MAX(MT) MT\n";
					sql += "FROM\n";
					sql += "(\n";
					sql += "SELECT  INSTANCE, ID,  TO_CHAR(MODIFY_TIME, 'YYYYMMDD') MT\n";
					sql += "FROM PUBLIC.SQL_STORAGE_H\n";
					sql += "GROUP BY INSTANCE, ID, MT\n";
					sql += "ORDER BY INSTANCE, ID, MT DESC\n";
					sql += ")\n";
					sql += "GROUP BY INSTANCE, ID\n";
					sql += ") RMN, PUBLIC.SQL_STORAGE_H TTL\n";
					sql += "WHERE 1 = 1\n";
					sql += "AND RMN.INSTANCE = TTL.INSTANCE\n";
					sql += "AND RMN.ID = TTL.ID\n";
					sql += "AND RMN.MT != TO_CHAR(TTL.MODIFY_TIME, 'YYYYMMDD'))";
					stmt.executeUpdate(sql);
					sql = "";
					sql += "DELETE FROM PUBLIC.COMPONENT_H\n";
					sql += "WHERE (INSTANCE, ID, MODIFY_TIME) IN (\n";
					sql += "SELECT INSTANCE, ID, TTL.MODIFY_TIME\n";
					sql += "FROM (\n";
					sql += "SELECT INSTANCE, ID, MAX(MT) MT\n";
					sql += "FROM\n";
					sql += "(\n";
					sql += "SELECT  INSTANCE, ID,  TO_CHAR(MODIFY_TIME, 'YYYYMMDD') MT\n";
					sql += "FROM PUBLIC.COMPONENT_H\n";
					sql += "GROUP BY INSTANCE, ID, MT\n";
					sql += "ORDER BY INSTANCE, ID, MT DESC\n";
					sql += ")\n";
					sql += "GROUP BY INSTANCE, ID\n";
					sql += ") RMN, PUBLIC.COMPONENT_H TTL\n";
					sql += "WHERE 1 = 1\n";
					sql += "AND RMN.INSTANCE = TTL.INSTANCE\n";
					sql += "AND RMN.ID = TTL.ID\n";
					sql += "AND RMN.MT != TO_CHAR(TTL.MODIFY_TIME, 'YYYYMMDD'))";
					stmt.executeUpdate(sql);
					sql = "";
					sql += "DELETE FROM PUBLIC.SERVICE_H\n";
					sql += "WHERE (INSTANCE, ID, MODIFY_TIME) IN (\n";
					sql += "SELECT INSTANCE, ID, TTL.MODIFY_TIME\n";
					sql += "FROM (\n";
					sql += "SELECT INSTANCE, ID, MAX(MT) MT\n";
					sql += "FROM\n";
					sql += "(\n";
					sql += "SELECT  INSTANCE, ID,  TO_CHAR(MODIFY_TIME, 'YYYYMMDD') MT\n";
					sql += "FROM PUBLIC.SERVICE_H\n";
					sql += "GROUP BY INSTANCE, ID, MT\n";
					sql += "ORDER BY INSTANCE, ID, MT DESC\n";
					sql += ")\n";
					sql += "GROUP BY INSTANCE, ID\n";
					sql += ") RMN, PUBLIC.SERVICE_H TTL\n";
					sql += "WHERE 1 = 1\n";
					sql += "AND RMN.INSTANCE = TTL.INSTANCE\n";
					sql += "AND RMN.ID = TTL.ID\n";
					sql += "AND RMN.MT != TO_CHAR(TTL.MODIFY_TIME, 'YYYYMMDD'))";
					stmt.executeUpdate(sql);

					connection.commit();
				} catch (SQLException e) {
					if (connection != null) {
						try {
							connection.rollback();
						} catch (SQLException e1) {
						}
					}
				} finally {
					try {
						if (stmt != null)
							stmt.close();
						if (connection != null)
							connection.close();
					} catch (SQLException e) {
					}
				}
			}
		};
		t.start();
	}

	public synchronized void exit() {
		this.exit = true;
		this.interrupt();
	}

}
