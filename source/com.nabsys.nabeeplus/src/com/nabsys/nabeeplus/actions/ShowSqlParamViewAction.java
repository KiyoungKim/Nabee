package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.SqlParameterView;

public class ShowSqlParamViewAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.showSqlParamViewAction";
	
	public ShowSqlParamViewAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setId(ID);

		setText(NBLabel.get(0x023C) + "                   ");
		setImageDescriptor(Activator.getImageDescriptor("/icons/sql.gif"));
	}
	
	public void run()
	{
		try {
			SqlParameterView parameterView = (SqlParameterView) window.getActivePage().showView(SqlParameterView.ID);
			parameterView.setFocus();
			
		} catch (PartInitException e) {
		}
	}
}
