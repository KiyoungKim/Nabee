package com.nabsys.process;

import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class InstanceHeaderFields extends TelegramFieldContextList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7046613513284826409L;
	
	String lengthFieldID = "";
	String depareFieldID = "";
	String serviceFieldID = "";
	String returnFieldID = "";
	String msgTypeFieldID = "";

	public void setLengthFieldIndex(String id)
	{
		lengthFieldID = id;
	}
	
	public void setDepartFieldIndex(String id)
	{
		depareFieldID = id;
	}
	
	public void setServiceFieldIndex(String id)
	{
		serviceFieldID = id;
	}
	
	public void setReturnFieldIndex(String id)
	{
		returnFieldID = id;
	}
	
	public void setMsgTypeFieldIndex(String id)
	{
		msgTypeFieldID = id;
	}
	
	public TelegramFieldContext getLengthField()
	{
		return super.get(lengthFieldID);
	}
	
	public TelegramFieldContext getDepartField()
	{
		return super.get(depareFieldID);
	}
	
	public TelegramFieldContext getServiceField()
	{
		return super.get(serviceFieldID);
	}
	
	public TelegramFieldContext getReturnField()
	{
		return super.get(returnFieldID);
	}
	
	public TelegramFieldContext getMsgTypeField()
	{
		return super.get(msgTypeFieldID);
	}

}
