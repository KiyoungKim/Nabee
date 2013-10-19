package com.nabsys.resource;

import java.io.Serializable;

public class NetworkContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int 	bufferSize			= 0;
	private int 	readTimeout			= 0;
	private String 	serverEncoding		= null;
	private String 	lengthFieldID		= null;
	private String 	idFieldID			= null;
	public NetworkContext(int bufferSize, int readTimeout, String serverEncoding,String	lengthFieldID, String idFieldID)
	{
		this.bufferSize =bufferSize;
		this.readTimeout = readTimeout;
		this.serverEncoding = serverEncoding;
		this.lengthFieldID = lengthFieldID;
		this.idFieldID = idFieldID;
	}
	public int getBufferSize(){
		return bufferSize;
	}
	public int getReadTimeout(){
		return readTimeout;
	}
	public String getServerEncoding(){
		return serverEncoding;
	}
	public String getLengthFieldID(){
		return lengthFieldID;
	}
	public String getIDFieldID(){
		return idFieldID;
	}
}
