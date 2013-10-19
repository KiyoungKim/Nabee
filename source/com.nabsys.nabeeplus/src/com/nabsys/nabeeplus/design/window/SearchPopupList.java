package com.nabsys.nabeeplus.design.window;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.nabsys.nabeeplus.views.model.PopupListModel;
import com.nabsys.nabeeplus.widgets.PopupList;

public class SearchPopupList extends PopupList{
	private Composite caller = null;
	private boolean isShellDisposed = true;
	
	public SearchPopupList(Shell parent, Composite caller) {
		this(parent, SWT.NONE, caller);
	}
	
	public SearchPopupList(Shell parent, int style, Composite caller) {
		super(parent, style);
		this.caller = caller;
		shell.removeListener(SWT.Deactivate, deactivateListener);
		
		list.getTable().getColumn(1).setWidth(20);
		label.dispose();
	}
	
	public boolean isShellDisposed()
	{
		return isShellDisposed;
	}
	
	public void close()
	{
		shell.setVisible(false);
	}
	
	public String open (Rectangle rect) {
		shell.setBounds(rect);
		
		list.getTable().getColumn(2).setWidth(rect.width - 70);
		shell.open();
		isShellDisposed = false;
		caller.forceFocus();

		
		Display display = shell.getDisplay();
		while (!shell.isDisposed () && shell.isVisible ()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		isShellDisposed = true;
		
		String result = null;
		if (!shell.isDisposed ()) {
			IStructuredSelection selection = (IStructuredSelection)list.getSelection();
			
			if(!selection.isEmpty())
			{
				PopupListModel popupListModel = (PopupListModel)selection.getFirstElement();
				result = popupListModel.getID();
			}
			
			shell.dispose();
		}
		return result;
	}
}
