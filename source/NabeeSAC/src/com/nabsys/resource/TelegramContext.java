package com.nabsys.resource;

import java.io.Serializable;

public class TelegramContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TelegramFieldContextList telegramFieldContextList = null;
	private String id = null;
	private String name = null;
	private String headerID = null;
	private char logLevel = 0;
	private String remark = null;
	public TelegramContext(String id, String name, String headerID, char logLevel, String remark, TelegramFieldContextList telegramFieldContextList)
	{
		this.id 						= id;
		this.name 						= name;
		this.headerID 					= headerID;
		this.logLevel 					= logLevel;
		this.remark 					= remark;
		this.telegramFieldContextList 	= telegramFieldContextList;
	}
	
	public TelegramFieldContextList getTelegramFieldContextList(){
		return telegramFieldContextList;
	}
	public String getID(){
		return id;
	}
	public String getName(){
		return name;
	}
	public String getHeaderID(){
		return headerID;
	}
	public char getLogLevel(){
		return logLevel;
	}
	public String getRemark(){
		return remark;
	}
}
