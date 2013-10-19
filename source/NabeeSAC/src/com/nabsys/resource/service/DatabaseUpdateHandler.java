package com.nabsys.resource.service;

import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.process.Context;

public class DatabaseUpdateHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sqlURL = null;
	private boolean isLogging = false;
	private boolean isSetReturnValue = false;
	private String returnMapKey = null;
	public DatabaseUpdateHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	public void setData(String sqlURL, boolean isLogging, boolean isSetReturnValue, String returnMapKey)
	{
		this.sqlURL = sqlURL;
		this.isLogging = isLogging;
		this.isSetReturnValue = isSetReturnValue;
		this.returnMapKey = returnMapKey;
	}
	
	public String getSqlURL()
	{
		return sqlURL;
	}
	
	public boolean isLogging()
	{
		return isLogging;
	}

	public boolean isSetReturnValue()
	{
		return isSetReturnValue;
	}
	
	public String getReturnMapKey()
	{
		return returnMapKey;
	}
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			NLogger logger = ctx.getLogger(this.getClass());
			ServiceHandler parentHandler = getParent();
			DatabaseHandler databaseHandler = null;
			while(true)
			{
				if(parentHandler instanceof DatabaseHandler)
				{
					databaseHandler = (DatabaseHandler)parentHandler;
					break;
				}
				else if(parentHandler == null)
				{
					throw new Exception("Can't find database handler");
				}
				else
				{
					parentHandler = parentHandler.getParent();
				}
			}
			Connection connection = databaseHandler.getConnection();
			
			if(isLogging && logger.isEnabledFor(Level.DEBUG))
				logger.debug("EXECUTE ID : " + sqlURL + "\r" + connection.getQuery(sqlURL, map));
			
			int updateCount = connection.update(sqlURL, map);
			if(isSetReturnValue) map.put(returnMapKey, updateCount);
		}
		
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Update : " + map + "", false, false);
		}
		
		super.execute(ctx, map);
	}

}
