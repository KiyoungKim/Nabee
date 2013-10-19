package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.net.protocol.IPC.IPCProtocol;

public class ServerConnect extends Model {

	private IPCProtocol protocol = null;
	private String user = "";
	private String password = "";
	private String addr = "";
	private String channel = "";
	private String serverEncoding = "";
	private int port = 0;
	private int channelport = 0;
	
	public ServerConnect(String name, Image image) {
	}
	
	public void setProtocol(IPCProtocol protocol)
	{
		this.protocol = protocol;
	}
	
	public boolean isConnect()
	{
		if(this.protocol == null) return false;
		return this.protocol.isConnected();
	}
	
	public IPCProtocol getProtocol()
	{
		return protocol;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setHostAddr(String addr)
	{
		this.addr = addr;
	}
	
	public void setHostPort(int port)
	{
		this.port = port;
	}
	
	public void setChannel(String channel)
	{
		this.channel = channel;
	}
	
	public void setChannelPort(int channelport)
	{
		this.channelport = channelport;
	}
	
	
	
	public String getUser()
	{
		return this.user;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public String getHostAddr()
	{
		return this.addr;
	}
	
	public int getHostPort()
	{
		return this.port;
	}
	
	public String getChannel()
	{
		return this.channel;
	}
	
	public int getChannelPort()
	{
		return this.channelport;
	}

	public String getServerEncoding() {
		return serverEncoding;
	}

	public void setServerEncoding(String serverEncoding) {
		this.serverEncoding = serverEncoding;
	}

}
