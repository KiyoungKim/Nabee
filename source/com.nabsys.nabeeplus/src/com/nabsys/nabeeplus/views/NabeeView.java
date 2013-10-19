package com.nabsys.nabeeplus.views;

import org.eclipse.ui.part.ViewPart;

import com.nabsys.nabeeplus.views.model.Server;

public abstract class NabeeView extends ViewPart{
	private Server server = null;
	
	public NabeeView()
	{
		super();
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}
	
	public Server getServer()
	{
		return this.server;
	}
	
	public void closeConnection()
	{
		if(server != null)
		((ServerView) getSite().getWorkbenchWindow().getActivePage().findView(ServerView.ID)).closeConnection(server);
	}
}
