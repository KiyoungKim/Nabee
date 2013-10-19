package com.nabsys.resource.script;

import java.io.Serializable;
import java.util.HashMap;

public class FuncFactory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Method m = null;
	//private String methodName = null;
	private Function function = null;
	private Object[] params = null;
	private Object value = null;
	
	public FuncFactory(Object value)
	{
		this.value = value;
	}
	
	public FuncFactory(Function function, Object[] params) throws Exception
	{
		Object[] tmpParams = new Object[params.length];
		for(int i=0; i<params.length; i++)
		{
			if(params[i] instanceof FuncFactory)
			{
				tmpParams[i] = ((FuncFactory)params[i]).getTmpReturnObject();
			}
			else
			{
				tmpParams[i] = params[i];
			}
		}
		
		if(!function.chkParamType(tmpParams)) throw new Exception("The Function '"+function+"' is not applicable for the parameters");
		this.function = function;
		//m = function.method(params.length);
		//methodName = function.method(params.length).getName();
		this.params = params;
	}
	
	protected Object getTmpReturnObject()
	{
		if(function == null) return null;
		return function.getTmpReturnObject();
	}
	
	public Object execute(HashMap<String, Object> map) throws Exception
	{
		if(value != null)
		{
			if(value instanceof FuncParam) return ((FuncParam)value).getValue(map);
			else return value;
		}
		if(function == null) return null;
		
		if(function == Function.FORMAT)
		{
			Object[] tmpParams = new Object[params.length - 1];
			for(int i=1; i<params.length; i++)
			{
				if(params[i] instanceof FuncFactory)
				{
					tmpParams[i-1] = ((FuncFactory)params[i]).execute(map);
				}
				else if(params[i] instanceof FuncParam)
				{
					tmpParams[i-1] = ((FuncParam)params[i]).getValue(map);
				}
				else
				{
					tmpParams[i-1] = params[i];
				}
			}
			return function.method(this.params.length).invoke(new StringBuilder(), params[0], tmpParams);
		}
		else
		{
			Object[] tmpParams = new Object[params.length - 1];
			Object obj = null;
			
			if(params[0] instanceof FuncFactory) obj = ((FuncFactory)params[0]).execute(map);
			else if(params[0] instanceof FuncParam) obj = ((FuncParam)params[0]).getValue(map);
			else obj = params[0];
			
			for(int i=1; i<params.length; i++)
			{
				if(params[i] instanceof FuncFactory)
				{
					tmpParams[i-1] = ((FuncFactory)params[i]).execute(map);
				}
				else if(params[i] instanceof FuncParam)
				{
					tmpParams[i-1] = ((FuncParam)params[i]).getValue(map);
				}
				else
				{
					tmpParams[i-1] = params[i];
				}
			}
			
			return function.method(this.params.length).invoke(obj, tmpParams);
		}
	}
	
	public Object execute(HashMap<String, Object> map, int index) throws Exception
	{
		if(value != null)
		{
			if(value instanceof FuncParam) return ((FuncParam)value).getValue(map, index);
			else return value;
		}
		if(function == null) return null;
		
		Object[] tmpParams = new Object[params.length - 1];
		Object obj = null;
		
		if(params[0] instanceof FuncFactory) obj = ((FuncFactory)params[0]).execute(map, index);
		else if(params[0] instanceof FuncParam) obj = ((FuncParam)params[0]).getValue(map, index);
		else obj = params[0];
			
		for(int i=1; i<params.length; i++)
		{
			if(params[i] instanceof FuncFactory)
			{
				tmpParams[i-1] = ((FuncFactory)params[i]).execute(map, index);
			}
			else if(params[i] instanceof FuncParam)
			{
				tmpParams[i-1] = ((FuncParam)params[i]).getValue(map, index);
			}
			else
			{
				tmpParams[i-1] = params[i];
			}
		}
		return function.method(this.params.length).invoke(obj, tmpParams);
	}
}
