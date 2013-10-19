package com.nabsys.resource.service;

import java.io.Serializable;

public class ComponentArgumentContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type[]			= null;
	private String argumentName[] 	= null;
	private Object argument[] 		= null;
	private boolean isSystemValue[]	= null;
	
	public ComponentArgumentContext(String[] type, String[] argumentName, Object[] argument, boolean[] isSystemValue)
	{
		this.type = type;
		this.argumentName = argumentName;
		this.argument = argument;
		this.isSystemValue = isSystemValue;
	}
	
	public String[] getSignature()
	{
		return type;
	}
	
	public Class<?>[] getType() throws ClassNotFoundException
	{
		Class<?>[] clazzArray = new Class<?>[type.length]; 
		for(int i=0; i<type.length; i++)
		{
			if(type[i].equals("I")){clazzArray[i] = int.class;}//int
			else if(type[i].equals("J")){clazzArray[i] = long.class;}//long
			else if(type[i].equals("D")){clazzArray[i] = double.class;}//double
			else if(type[i].equals("F")){clazzArray[i] = float.class;}//float
			else if(type[i].equals("B")){clazzArray[i] = byte.class;}//byte
			else if(type[i].equals("C")){clazzArray[i] = char.class;}//char
			else if(type[i].equals("Z")){clazzArray[i] = boolean.class;}//boolean
			else if(type[i].substring(0, 1).equals("L")){clazzArray[i] = Class.forName(type[i].substring(1).replaceAll("/", ".").replace(";", ""));}
			else if(type[i].substring(0, 1).equals("["))
			{
				String chkType = type[i].substring(1, 2);
				if(chkType.equals("I")){clazzArray[i] = int[].class;}//int
				else if(chkType.equals("J")){clazzArray[i] = long[].class;}//long
				else if(chkType.equals("D")){clazzArray[i] = double[].class;}//double
				else if(chkType.equals("F")){clazzArray[i] = float[].class;}//float
				else if(chkType.equals("B")){clazzArray[i] = byte[].class;}//byte
				else if(chkType.equals("C")){clazzArray[i] = char[].class;}//char
				else if(chkType.equals("Z")){clazzArray[i] = boolean[].class;}//boolean
				else if(chkType.equals("L")){clazzArray[i] = Class.forName(type[i]);}
			}
		}
		return clazzArray;
	}
	
	public String[] getArgumentName()
	{
		return this.argumentName;
	}
	
	public Object[] getArgument()
	{
		return this.argument;
	}
	
	public void setArgument(int index, Object arg)
	{
		this.argument[index] = arg;
	}
	
	public boolean[] isSystemValue()
	{
		return this.isSystemValue;
	}
	public int getSize()
	{
		return type.length;
	}
}
