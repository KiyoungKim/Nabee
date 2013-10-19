package com.nabsys.resource.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Comparator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object target 			= null;
	private String methodName		= null;
	private String parameterName	= null;
	public Comparator(String operator, Object target) throws SecurityException, NoSuchMethodException
	{
		this(null, operator, target);
	}
	public Comparator(String parameterName, String operator, Object target) throws SecurityException, NoSuchMethodException
	{
		this.parameterName = parameterName;
		this.target = target;
		if(operator.equals("<"))
		{
			methodName = "BIGGER";
		}
		else if(operator.equals("<="))
		{
			methodName = "BIGGER_OR_EQUAL";
		}
		else if(operator.equals("="))
		{
			methodName = "EQUAL";
		}
		else if(operator.equals(">"))
		{
			methodName = "SMALLER";
		}
		else if(operator.equals(">="))
		{
			methodName = "SMALLER_OR_EQUAL";
		}
		else if(operator.equals("!="))
		{
			methodName = "NOT_EQUAL";
		}
	}
	
	public boolean execute(HashMap<String, Object> map) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		Method method = getClass().getMethod(methodName, new Class[]{Object.class});
		return (Boolean) method.invoke(this, new Object[]{map.get(parameterName)});
	}
	
	public boolean execute(Object source) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		Method method = getClass().getMethod(methodName, new Class[]{Object.class});
		return (Boolean) method.invoke(this, new Object[]{source});
	}
	
	public boolean BIGGER(Object source)
	{
		if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source < (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Integer)source < (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Integer)source < (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Integer)source < (Double)target;
			}
			else return false;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source < (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Long)source < (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Long)source < (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Long)source < (Double)target;
			}
			else return false;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source < (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Float)source < (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Float)source < (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Float)source < (Double)target;
			}
			else return false;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source < (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Double)source < (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Double)source < (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Double)source < (Double)target;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean BIGGER_OR_EQUAL(Object source)
	{
		if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source <= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Integer)source <= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Integer)source <= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Integer)source <= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source <= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Long)source <= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Long)source <= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Long)source <= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source <= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Float)source <= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Float)source <= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Float)source <= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source <= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Double)source <= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Double)source <= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Double)source <= (Double)target;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean EQUAL(Object source)
	{
		if(source instanceof String)
		{
			if(target instanceof String)
			{
				return source.equals(target);
			}
			else
			{
				return false;
			}
		}
		else if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source == (Integer)target;
			}
			else if(target instanceof Long)
			{
				return ((Integer)source).longValue() == (Long)target;
			}
			else if(target instanceof Float)
			{
				return ((Integer)source).floatValue() == (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Integer)source).doubleValue() == (Double)target;
			}
			else return false;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source == ((Integer)target).longValue();
			}
			else if(target instanceof Long)
			{
				return (Long)source == (Long)target;
			}
			else if(target instanceof Float)
			{
				return ((Long)source).floatValue() == (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Long)source).doubleValue() == (Double)target;
			}
			else return false;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source == ((Integer)target).floatValue();
			}
			else if(target instanceof Long)
			{
				return (Float)source == ((Long)target).floatValue();
			}
			else if(target instanceof Float)
			{
				return (Float)source == (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Float)source).doubleValue() == (Double)target;
			}
			else return false;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source == ((Integer)target).doubleValue();
			}
			else if(target instanceof Long)
			{
				return (Double)source == ((Long)target).doubleValue();
			}
			else if(target instanceof Float)
			{
				return (Double)source == ((Float)target).doubleValue();
			}
			else if(target instanceof Double)
			{
				return (Double)source == (Double)target;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean SMALLER(Object source)
	{
		if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source > (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Integer)source > (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Integer)source > (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Integer)source > (Double)target;
			}
			else return false;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source > (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Long)source > (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Long)source > (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Long)source > (Double)target;
			}
			else return false;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source > (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Float)source > (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Float)source > (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Float)source > (Double)target;
			}
			else return false;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source > (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Double)source > (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Double)source > (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Double)source > (Double)target;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean SMALLER_OR_EQUAL(Object source)
	{
		if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source >= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Integer)source >= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Integer)source >= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Integer)source >= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source >= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Long)source >= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Long)source >= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Long)source >= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source >= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Float)source >= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Float)source >= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Float)source >= (Double)target;
			}
			else return false;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source >= (Integer)target;
			}
			else if(target instanceof Long)
			{
				return (Double)source >= (Long)target;
			}
			else if(target instanceof Float)
			{
				return (Double)source >= (Float)target;
			}
			else if(target instanceof Double)
			{
				return (Double)source >= (Double)target;
			}
			else return false;
		}
		else return false;
	}
	
	public boolean NOT_EQUAL(Object source)
	{
		if(source instanceof String)
		{
			if(target instanceof String)
			{
				return !source.equals(target);
			}
			else
			{
				return true;
			}
		}
		else if(source instanceof Integer)
		{
			if(target instanceof Integer)
			{
				return (Integer)source != (Integer)target;
			}
			else if(target instanceof Long)
			{
				return ((Integer)source).longValue() != (Long)target;
			}
			else if(target instanceof Float)
			{
				return ((Integer)source).floatValue() != (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Integer)source).doubleValue() != (Double)target;
			}
			else return true;
		}
		else if(source instanceof Long)
		{
			if(target instanceof Integer)
			{
				return (Long)source != ((Integer)target).longValue();
			}
			else if(target instanceof Long)
			{
				return (Long)source != (Long)target;
			}
			else if(target instanceof Float)
			{
				return ((Long)source).floatValue() != (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Long)source).doubleValue() != (Double)target;
			}
			else return true;
		}
		else if(source instanceof Float)
		{
			if(target instanceof Integer)
			{
				return (Float)source != ((Integer)target).floatValue();
			}
			else if(target instanceof Long)
			{
				return (Float)source != ((Long)target).floatValue();
			}
			else if(target instanceof Float)
			{
				return (Float)source != (Float)target;
			}
			else if(target instanceof Double)
			{
				return ((Float)source).doubleValue() != (Double)target;
			}
			else return true;
		}
		else if(source instanceof Double)
		{
			if(target instanceof Integer)
			{
				return (Double)source != ((Integer)target).doubleValue();
			}
			else if(target instanceof Long)
			{
				return (Double)source != ((Long)target).doubleValue();
			}
			else if(target instanceof Float)
			{
				return (Double)source != ((Float)target).doubleValue();
			}
			else if(target instanceof Double)
			{
				return (Double)source != (Double)target;
			}
			else return true;
		}
		else return true;
	}
	
}
