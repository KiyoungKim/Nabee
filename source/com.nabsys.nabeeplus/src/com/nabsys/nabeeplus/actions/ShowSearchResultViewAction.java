package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.SearchResultView;

public class ShowSearchResultViewAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.showSearchResultViewAction";
	
	public ShowSearchResultViewAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setId(ID);

		setText(NBLabel.get(0x0240) + "                   ");
		setImageDescriptor(Activator.getImageDescriptor("/icons/search_src.gif"));
	}
	
	public void run()
	{
		try {
			window.getActivePage().showView(SearchResultView.ID);
		} catch (PartInitException e) {
		}
	}
}
