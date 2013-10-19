package com.nabsys.process.exception;

import com.nabsys.common.label.NLabel;

public class PluginInitializationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1721179935536573017L;
	private Object returnObject = null;
	public PluginInitializationException(String message, Object returnObject)
	{
		super(message);
		this.returnObject = returnObject;
	}
	
	public PluginInitializationException(int code, Object returnObject)
	{
		super(NLabel.get(code));
		this.returnObject = returnObject;
	}
	
	public Object getReturnObject()
	{
		return this.returnObject;
	}
}
