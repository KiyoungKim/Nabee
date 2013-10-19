package com.nabsys.nabeeplus.actions;

import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.InstanceView;
import com.nabsys.nabeeplus.views.model.Instance;
import com.nabsys.nabeeplus.widgets.window.SimpleInputBox;


public class NewInstanceAction extends Action implements ISelectionListener{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.newInstanceAction";
	
	public NewInstanceAction(IWorkbenchWindow window)
	{
		this.window = window;

		setId(ID);
		window.getSelectionService().addSelectionListener(this);
	}
	
	public void run()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		SimpleInputBox newInstance = new SimpleInputBox(this.window.getShell(), params);
		
		newInstance.setTitle(NBLabel.get(0x019E));
		newInstance.setImage("/icons/pview.gif");
		newInstance.setLabel(NBLabel.get(0x0124));
		newInstance.open(window, false);
		
		if(params.get("EVENT").equals("OK"))
		{
			((InstanceView)window.getActivePage().findView(InstanceView.ID)).addInstance(params.get("TEXT"));
		}
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}


	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if(incoming.isEmpty())
		{
			setEnabled(part instanceof InstanceView && ((InstanceView)window.getActivePage().findView(InstanceView.ID)).hasAuthority());
		}
		else
		{
			setEnabled(((IStructuredSelection)incoming).getFirstElement() instanceof Instance && ((InstanceView)window.getActivePage().findView(InstanceView.ID)).hasAuthority());
		}
	}

}
