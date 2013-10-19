package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.views.model.Instance;

public class TerminateAction extends Action implements ISelectionListener{
	private final IWorkbenchWindow window;

	public TerminateAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		window.getSelectionService().addSelectionListener(this);
	}
	
	public void run()
	{
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
				if(selection.size() == 1 && selection.getFirstElement() instanceof Instance)
				{
					setEnabled(((Instance)selection.getFirstElement()).hasAuthority() && ((Instance)selection.getFirstElement()).isRunning());
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
