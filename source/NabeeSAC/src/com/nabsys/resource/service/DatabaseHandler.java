package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.database.Connection;
import com.nabsys.database.DBPoolManager;
import com.nabsys.process.Context;

public class DatabaseHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String databaseName = null;
	private boolean isAutoCommit = false;
	private Connection connection = null;
	public DatabaseHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String databaseName, boolean autoCommit)
	{
		this.databaseName = databaseName;
		this.isAutoCommit = autoCommit;
	}
	
	public String getDatabaseName()
	{
		return this.databaseName;
	}
	
	public boolean isAutoCommit()
	{
		return this.isAutoCommit;
	}
	
	protected Connection getConnection(){
		return this.connection;
	}
	
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			try{
				DBPoolManager dbPool = (DBPoolManager)ctx.getPlugins(databaseName);
				if(dbPool == null) throw new RuntimeException("Plug-in '"+databaseName+"' is null.");
				connection = dbPool.getConnection();
				boolean orgIsAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(isAutoCommit);
				
				if(ctx.isTest())
				{
					ctx.offerTestMessage(getHandlerID(), "Database : " + map + "", false, false);
				}
				super.moveToInside(ctx, map);
				connection.commit();
				connection.setAutoCommit(orgIsAutoCommit);
			}catch(RuntimeException ex){
				if(connection != null) connection.rollback();
				throw new Exception(ex.getMessage());
			}catch(Exception ex){
				if(connection != null) connection.rollback();
				throw ex;
			}finally{
				if(connection != null){
					connection.close();
				}
			}
		}
		super.execute(ctx, map);
	}

}
