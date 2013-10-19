package com.nabsys.nabeeplus.common;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import com.nabsys.nabeeplus.common.label.NBLabel;

public class EditActionManager {
	
	private IWorkbenchWindow 		window 			= null;
	private Display					display			= null;
	private StyledText				editor			= null;
	private IActionBars 			actionBars		= null;
	
	private UndoManager				undoManager		= null;
	
	private IAction 				undoAction		= null;
	private IAction 				redoAction		= null;
	private IAction 				copyAction		= null;
	private IAction 				cutAction		= null;
	private IAction 				pasteAction		= null;
	private IAction 				deleteAction	= null;
	
	private SelectionListener		selectListener	= null;
	
	public EditActionManager(Composite parent, IWorkbenchWindow window, IActionBars actionBars, StyledText editor)
	{
		this.window = window;
		this.display = parent.getDisplay();
		this.editor = editor;
		this.actionBars = actionBars;
		
		registContextMenu(parent);
		addRetargetableAction(true);
		
		undoManager = new UndoManager(10);
		undoManager.connect(editor);
	}
	
	private void addRetargetableAction(boolean isFirst)
	{
		if(isFirst)
		{
			undoAction = new Action() {
				public void run(){
					undoManager.undo();
				}
			};
			
			redoAction = new Action() {
				public void run(){
					undoManager.redo();
				}
			};
			
			copyAction = new Action() {
				public void run(){
					editor.copy();
					pasteAction.setEnabled(true);
				}
			};
			
			cutAction = new Action() {
				public void run(){
					editor.cut();
					pasteAction.setEnabled(true);
				}
			};
	
			
			pasteAction = new Action() {
				public void run(){
					editor.paste();
				}
			};
			pasteAction.setEnabled(false);
			
			deleteAction = new Action() {
				public void run(){
					int start = editor.getSelection().x;
					int end = editor.getSelection().y;
					
					String text = editor.getText();
					String tmpText = text.substring(0, start) + text.substring(end, text.length());
					editor.setText(tmpText);
				}
			};
		}
		
	
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cutAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		
		actionBars.updateActionBars();
	}
	
	private void registContextMenu(Composite parent)
	{
		MenuManager editManager = new MenuManager(NBLabel.get(0x0052), "mnuEdit");
    	editManager.add(ActionFactory.UNDO.create(window));
    	editManager.add(ActionFactory.REDO.create(window));
    	editManager.add(new Separator());
    	editManager.add(ActionFactory.CUT.create(window));
    	editManager.add(ActionFactory.COPY.create(window));
    	editManager.add(ActionFactory.PASTE.create(window));
    	editManager.add(new Separator());
    	editManager.add(ActionFactory.DELETE.create(window));
		
		Menu menu = editManager.createContextMenu(parent);
		editor.setMenu(menu);
	}
	
	public void setFocus()
	{
		addRetargetableAction(false);
		
		Clipboard clipboard = new Clipboard(display);
		String data = (String)clipboard.getContents(TextTransfer.getInstance());
		if (data != null && data.length() > 0)
			pasteAction.setEnabled(true);
		else
			pasteAction.setEnabled(false);
		
		Point point = editor.getSelection();
		
		if(point.x != point.y)
		{
			copyAction.setEnabled(true);
			cutAction.setEnabled(true);
			deleteAction.setEnabled(true);
		}
		else
		{
			copyAction.setEnabled(false);
			cutAction.setEnabled(false);
			deleteAction.setEnabled(false);
		}
	}
	
	public void dispose()
	{
		undoManager.disconnect();
		editor.removeSelectionListener(selectListener);
	}
}
