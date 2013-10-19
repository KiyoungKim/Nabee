package com.nabsys.process.instance.messagequeue;

import java.io.Serializable;

import com.nabsys.net.protocol.NBFields;

public class Message implements Comparable<Message>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NBFields data = null;
	String telegramID = null;
	
	public Message(NBFields data, String telegramID)
	{
		this.data = data;
		this.telegramID = telegramID;
	}
	
	public NBFields getData()
	{
		return this.data;
	}
	
	public String getTelegramID()
	{
		return telegramID;
	}
	
	public int compareTo(Message o) {
		return 1;
	}
}
