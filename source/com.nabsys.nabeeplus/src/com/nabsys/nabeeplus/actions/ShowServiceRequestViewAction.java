package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.ServiceRequestView;

public class ShowServiceRequestViewAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.showServiceRequestViewAction";
	
	public ShowServiceRequestViewAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setId(ID);

		setText(NBLabel.get(0x0243) + "                   ");
		setImageDescriptor(Activator.getImageDescriptor("/icons/servicetest.gif"));
	}
	
	public void run()
	{
		try {
			window.getActivePage().showView(ServiceRequestView.ID);
		} catch (PartInitException e) {
		}
	}
}
