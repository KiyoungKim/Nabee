package com.nabsys.nabeeplus.editors;

import org.eclipse.ui.part.EditorPart;

import com.nabsys.nabeeplus.views.ServerView;
import com.nabsys.nabeeplus.views.model.Server;

public abstract class NabeeEditor extends EditorPart {
	private Server server = null;

	public NabeeEditor()
	{
		super();
	}
	
	public Server getServer()
	{
		return this.server;
	}
	
	public void setServer(Server server)
	{
		this.server = server;
	}
	
	public void closeConnection()
	{
		if(server != null)
		((ServerView) getSite().getWorkbenchWindow().getActivePage().findView(ServerView.ID)).closeConnection(server);
	}
}
