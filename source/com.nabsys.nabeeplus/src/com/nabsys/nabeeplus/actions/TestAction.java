package com.nabsys.nabeeplus.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.editors.TestEditor;
import com.nabsys.nabeeplus.editors.input.TestEditorInput;

public class TestAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.testAction";
	
	public TestAction(IWorkbenchWindow window)
	{
		this.window = window;
		
		setId(ID);
		setActionDefinitionId(ID);
	}
	
	public void run()
	{
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(new TestEditorInput(), TestEditor.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
