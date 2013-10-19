package com.nabsys.resource.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.database.ResultSet;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;
public class DatabaseSelectHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sqlURL = null;
	private String listName = null;
	private boolean isLogging = false;
	private boolean isOneRow = false;
	private boolean isSetReturnValue = false;
	private String returnMapKey = null;
	public DatabaseSelectHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String sqlURL, String listName, boolean isLogging, boolean isOneRow, boolean isSetReturnValue, String returnMapKey)
	{
		this.sqlURL = sqlURL;
		this.listName = listName;
		this.isLogging = isLogging;
		this.isOneRow = isOneRow;
		this.isSetReturnValue = isSetReturnValue;
		this.returnMapKey = returnMapKey;
	}
	
	public String getSqlURL()
	{
		return sqlURL;
	}
	
	public String getListName()
	{
		return listName;
	}
	
	public boolean isOneRow()
	{
		return isOneRow;
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
	
	@SuppressWarnings("unchecked")
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

			ResultSet rs = connection.select(sqlURL, map);
			
			if(ctx.isTest())
			{
				ctx.offerTestMessage(getHandlerID(), "Select : " + map + "", false, false);
			}
			
			ArrayList<Object> list = new ArrayList<Object>();
			while(rs.next())
			{
				HashMap<String, Object> tmp = new NBFields();
				
				for(int i=0; i<rs.getColumnCount(); i++)
				{
					if(rs.getObject(i + 1) instanceof byte[])
					{
						byte[] value = (byte[])rs.getObject(i + 1);
						tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
					}
					else
					{
						String value = String.valueOf(rs.getObject(i + 1));
						tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
					}
				}
				super.moveToInside(ctx, tmp);
				
				list.add(tmp);
				if(isBreak)
				{
					isBreak = false;
					break;
				}
			}
			
			if(isOneRow)
			{
				if(list.size() > 0)
				{
					map.putAll((HashMap<String, Object>)list.get(list.size() - 1));
				}
			}
			else
			{
				map.put(listName, list);
			}
			if(isSetReturnValue) map.put(returnMapKey, list.size());
		}
		
		super.execute(ctx, map);
	}

}
