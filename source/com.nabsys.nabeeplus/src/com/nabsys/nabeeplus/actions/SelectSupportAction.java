package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;

public abstract class SelectSupportAction extends Action{

	public abstract void _selectionChanged(Object obj);
	
	public SelectSupportAction()
	{
		super();
	}
}
