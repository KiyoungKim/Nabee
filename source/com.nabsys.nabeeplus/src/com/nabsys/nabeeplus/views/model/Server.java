package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class Server extends Model{

	private ServerConnect serverConnect = null;
	
	public Server()
	{
		
	}
	
	public Server(Model parent, String name, Image image) {
		super(parent, name, image);
	}
	
	public void setConnection(ServerConnect serverConnect)
	{
		this.serverConnect = serverConnect;
	}
	
	public ServerConnect getConnect()
	{
		return serverConnect;
	}
	
	public ServerConfig getConfig()
	{
		for(int i=0; i<getChildren().size(); i++) if(getChildren().get(i) instanceof ServerConfig) return (ServerConfig)getChildren().get(i);
		return null;
	}
	
	public UserList getUserList()
	{
		for(int i=0; i<getChildren().size(); i++) if(getChildren().get(i) instanceof UserList) return (UserList)getChildren().get(i);
		return null;
	}
	
	public InstanceList getInstance()
	{
		for(int i=0; i<getChildren().size(); i++) if(getChildren().get(i) instanceof InstanceList) return (InstanceList)getChildren().get(i);
		return null;
	}
	
	public void removeConfig()
	{
		for(int i=0; i<getChildren().size(); i++)
		{
			if(getChildren().get(i) instanceof ServerConfig)
			{
				getChildren().remove(i);
				return;
			}
		}
	}
	
	public void removeUserList()
	{
		for(int i=0; i<getChildren().size(); i++)
		{
			if(getChildren().get(i) instanceof UserList)
			{
				getChildren().remove(i);
				return;
			}
		}
	}
	
	public void removeInstance()
	{
		for(int i=0; i<getChildren().size(); i++)
		{
			if(getChildren().get(i) instanceof InstanceList)
			{
				getChildren().remove(i);
				return;
			}
		}
	}


}
