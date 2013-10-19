package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.views.ServerView;

public class DisconnectAction extends Action{
	private final IWorkbenchWindow window;

	public DisconnectAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setEnabled(false);
	}

	public void run()
	{
		((ServerView)window.getActivePage().findView(ServerView.ID)).disconnectServer(true);
	}
}
