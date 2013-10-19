package com.nabsys.resource.service;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class ProcedureArgumentContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] argument = null;
	private boolean[] isInput = null;
	private boolean[] isMapValue = null;
	private int[] sqlTypes = null;
	
	public ProcedureArgumentContext(Object[] argument, boolean[] isInput, boolean[] isMapValue, int[] sqlTypes)
	{
		this.argument = argument;
		this.isInput = isInput;
		this.isMapValue = isMapValue;
		this.sqlTypes = sqlTypes;
	}
	
	public boolean hasArguments()
	{
		return argument.length > 0;
	}
	
	public int size()
	{
		return argument.length;
	}
	
	public String getArgument(int index)
	{
		return argument[index] + "";
	}
	
	public boolean isInput(int index)
	{
		return isInput[index];
	}
	
	public boolean isMapValue(int index)
	{
		return isMapValue[index];
	}
	
	public int getSqlType(int index)
	{
		return sqlTypes[index];
	}
	
	public String getPrepareString()
	{
		String argString = "";
		for(int i=0; i<argument.length; i++)
		{
			argString += i==0?"?":",?";
		}
		return argString;
	}
	
	public void setParams(CallableStatement cstmt, HashMap<String, Object> map) throws SQLException
	{
		for(int i=0; i<argument.length; i++)
		{
			if(isInput[i])
			{
				if(isMapValue[i])
				{
					cstmt.setObject(i+1, map.get(argument[i]));
				}
				else
				{
					cstmt.setObject(i+1, argument[i]);
				}
			}
			else
			{
				cstmt.registerOutParameter(i+1, sqlTypes[i]);
			}
		}
	}
	
	public String getDebugString(HashMap<String, Object> map)
	{
		String debugString = "";
		for(int i=0; i<argument.length; i++)
		{
			if(isMapValue[i])
			{
				if(isInput[i])
					debugString += i==0?"'" + map.get(argument[i]) + "'":", '" + map.get(argument[i]) + "'";
				else
					debugString += i==0?"?":", ?";
			}
			else
			{
				if(isInput[i])
					debugString += i==0?"'" + argument[i] + "'":", '" + argument[i] + "'";
				else
					debugString += i==0?"?":", ?";
			}	
		}
		return debugString;
	}
	
	public void getResult(CallableStatement cstmt, HashMap<String, Object> map) throws SQLException
	{
		for(int i=0; i<argument.length; i++)
		{
			if(!isInput[i])
			{
				map.put((String) argument[i], cstmt.getObject(i+1));
			}
		}
	}
}
