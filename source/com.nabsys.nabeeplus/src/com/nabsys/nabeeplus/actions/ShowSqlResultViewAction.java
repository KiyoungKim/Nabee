package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.SqlResultView;

public class ShowSqlResultViewAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.showSqlResultViewAction";
	
	public ShowSqlResultViewAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setId(ID);

		setText(NBLabel.get(0x023D) + "                   ");
		setImageDescriptor(Activator.getImageDescriptor("/icons/table.gif"));
	}
	
	public void run()
	{
		try {
			window.getActivePage().showView(SqlResultView.ID);
		} catch (PartInitException e) {
		}
	}
}
