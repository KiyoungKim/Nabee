package com.nabsys.resource.service;

import java.sql.CallableStatement;
import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.process.Context;

public class DatabaseProcedureHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String						procedureName		= null;
	private ProcedureArgumentContext 	parameter			= null;
	private String						callString			= null;
	private boolean 					isLogging			= false;

	public DatabaseProcedureHandler(ServiceHandler parent, int x, int y,
			int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String procedureName, ProcedureArgumentContext parameter, boolean isLogging)
	{
		this.procedureName = procedureName;
		this.parameter = parameter;
		this.isLogging = isLogging;
		String prepareString = "";
		if(parameter != null) prepareString = parameter.getPrepareString();
		this.callString = "{call "+procedureName+"("+prepareString+")}";
	}
	
	public String getProcedureName()
	{
		return this.procedureName;
	}
	
	public ProcedureArgumentContext getParameter()
	{
		return this.parameter;
	}
	
	public boolean isLogging()
	{
		return this.isLogging;
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
			{
				logger.debug("PROCEDURE CALL : " + "{call "+procedureName+"("+parameter.getDebugString(map)+")}");
			}
			
			CallableStatement cstmt = connection.prepareCall(this.callString);
			parameter.setParams(cstmt, map);
			cstmt.executeQuery();
			parameter.getResult(cstmt, map);
		}
		
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Procedure : " + map + "", false, false);
		}
		
		super.execute(ctx, map);
	}

}
