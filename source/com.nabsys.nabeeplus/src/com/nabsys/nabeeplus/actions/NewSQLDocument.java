package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.views.model.QueryStorageList;

public class NewSQLDocument  extends Action implements ISelectionListener{
	private final IWorkbenchWindow window;

	public NewSQLDocument(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		window.getSelectionService().addSelectionListener(this);
	}
	
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if(incoming instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection)incoming;
			
			if(selection.getFirstElement() != null)
			{
				if(selection.size() == 1 && selection.getFirstElement() instanceof QueryStorageList)
				{
					setEnabled(true);
				}
				else
				{
					setEnabled(false);
				}
			}
			else
			{
				setEnabled(false);
			}
		}
		else
		{
			setEnabled(false);
		}
	}

}
