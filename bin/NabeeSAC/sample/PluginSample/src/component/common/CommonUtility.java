package component.common;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.DateUtil;
import com.nabsys.database.Connection;
import com.nabsys.database.DBPoolManager;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class CommonUtility {

	@SuppressWarnings("unchecked")
	public void moveUploadTmpFile(Context context, String url, String listName, String parameterName, HashMap<String, Object> map) throws SQLException
	{
		NLogger logger = context.getLogger(this.getClass());
		logger.debug(">>>>>>>>>>>>>>>moveUploadTmpFile START<<<<<<<<<<<<<<<<<<<<");
		Connection connection = ((DBPoolManager)context.getPlugins("PMS_DB")).getConnection();
		if(map.containsKey(listName))
		{
			boolean isAutoCommit = connection.getAutoCommit();
			try{
				connection.setAutoCommit(false);
				if(map.get(listName) instanceof ArrayList)
				{
					ArrayList<NBFields> fileList = (ArrayList<NBFields>)map.get(listName);
					for(int i=0; i<fileList.size(); i++)
					{
						map.put(parameterName, (String)fileList.get(i).get("VALUE"));
						if(logger.isEnabledFor(Level.DEBUG));
						logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, map));
						connection.update(url, map);
					}
				}
				else if(map.get(listName) instanceof String)
				{
					map.put(parameterName, (String)map.get(listName));
					if(logger.isEnabledFor(Level.DEBUG));
					logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, map));
					connection.update(url, map);
				}
				connection.commit();
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				connection.rollback();
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				connection.rollback();
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				connection.rollback();
			}  catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				connection.rollback();
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				connection.rollback();
			} finally{
				connection.setAutoCommit(isAutoCommit);
			}
		}
	}
	
	public void removeLogFile(Context context, int daysBefore, String logDir)
	{
		
		NLogger logger = context.getLogger(this.getClass());
		
		if(!logDir.endsWith("/") && !logDir.endsWith("\\")) logDir+="/";
			
		DateUtil du = new DateUtil();
		String currentDate = du.getCurrentDate("yyyy-MM-dd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA);
		try {
			File dir = new File(logDir);
			String[] fileNames = dir.list();
			logger.info("Total files : " + fileNames.length);
			for(int i=0; i<fileNames.length; i++)
			{
				sdf.parse(currentDate);
				Calendar cal = sdf.getCalendar();
				boolean delete = true;
				
				for(int j=0; j<daysBefore; j++)
				{
					cal.add(Calendar.DATE, -1);
					String format = sdf.format(cal.getTime());
					if(fileNames[i].matches(".*[0-9]+\\-+[0-9]+\\-+[0-9]+.*"))
					{
						if(fileNames[i].contains(format)) delete = false;
					}
				}
				
				if(delete)
				{
					File file = new File(logDir + fileNames[i]);
					
					if(file.delete())
					{
						logger.info("Backup file removed : " + fileNames[i]);
					}

				}
			}
		} catch (ParseException e) {
			logger.error(e, e.getMessage());
		}
	}
}
