package com.nabsys.nabeeplus.widgets;

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.PopupListModel;
import com.nabsys.nabeeplus.views.model.TableModel;

/**
* A PopupList is a list of selectable items that appears in its own shell positioned above
* its parent shell.  It is used for selecting items when editing a Table cell (similar to the
* list that appears when you open a Combo box).
*
* The list will be positioned so that it does not run off the screen and the largest number of items
* are visible.  It may appear above the current cursor location or below it depending how close you 
* are to the edge of the screen.
*
* @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
*/
public class PopupList {
	protected Shell  shell;
	protected TableViewer list;
	protected Listener deactivateListener;
	protected Label label;
/** 
* Creates a PopupList above the specified shell.
* 
* @param parent a Shell control which will be the parent of the new instance (cannot be null)
*/
public PopupList(Shell parent) {
	this (parent, 0);
}
/** 
* Creates a PopupList above the specified shell.
* 
* @param parent a widget which will be the parent of the new instance (cannot be null)
* @param style the style of widget to construct
* 
* @since 3.0 
*/
public PopupList(Shell parent, int style) {
	shell = new Shell(parent, checkStyle(style));
	
	GridLayout layout = new GridLayout();
	layout.marginWidth = 2;
	layout.marginHeight = 2;
	layout.verticalSpacing = 0;
	layout.numColumns = 1;

	shell.setLayout(layout);

	
	list = new TableViewer(shell, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);	
	list.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	
	TableViewerColumn column = new TableViewerColumn(list, SWT.NONE);
	column.getColumn().setWidth(0);
	
	column = new TableViewerColumn(list, SWT.NONE);
	column.getColumn().setWidth(30);
	
	column = new TableViewerColumn(list, SWT.NONE);
	column.getColumn().setWidth(300);
	
	list.getTable().setHeaderVisible(false);
	list.getTable().setLinesVisible(false);
	list.getTable().setFont(new Font(shell.getDisplay(), "Arial", 9, SWT.NONE));
	list.getTable().getColumn(0).pack();
	list.setContentProvider(new NBTableContentProvider());
	list.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
	
	PopupListModel root = new PopupListModel();
	list.setInput(root);
	
	GridData labelLayoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
	labelLayoutData.heightHint = 17;
	
	label = new Label(shell, SWT.NONE);
	label.setText(NBLabel.get(0x023B));
	label.setLayoutData(labelLayoutData);
	label.setAlignment(SWT.RIGHT);


	// close dialog if user selects outside of the shell
	shell.addListener(SWT.Deactivate, deactivateListener = new Listener() {
		public void handleEvent(Event e){	
			shell.setVisible (false);
		}
	});
	
	// return list selection on Mouse Up or Carriage Return
	list.getTable().addMouseListener(new MouseListener() {
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseDown(MouseEvent e){}
		public void mouseUp(MouseEvent e){
			shell.setVisible (false);
		}
	});
	list.getTable().addKeyListener(new KeyListener() {
		public void keyReleased(KeyEvent e){}
		public void keyPressed(KeyEvent e){
			if (e.character == '\r'){
				shell.setVisible (false);
			}
		}
	});
	
}
private static int checkStyle (int style) {
	int mask = SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
	return style & mask;
}

/**
* Launches the Popup List, waits for an item to be selected and then closes the PopupList.
*
* @param rect the initial size and location of the PopupList; the dialog will be
*        positioned so that it does not run off the screen and the largest number of items are visible
*
* @return the text of the selected item or null if no item is selected
*/
public String open (Rectangle rect) {
	shell.setBounds(rect);
	
	shell.open();

	Display display = shell.getDisplay();
	while (!shell.isDisposed () && shell.isVisible ()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	
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


/**
* Sets all items.
* <p>
* The previous selection is cleared.
* The previous items are deleted.
* The new items are added.
* The top index is set to 0.
*
* @param strings the array of items
*
* This operation will fail when an item is null
* or could not be added in the OS.
* 
* @exception IllegalArgumentException <ul>
*    <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
*    <li>ERROR_INVALID_ARGUMENT - if an item in the items array is null</li>
* </ul>
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
*	</ul>
*/
public void setItems (String[] strings) {
	PopupListModel root = null;
	list.setInput(root = new PopupListModel());
	for(int i=0; i<strings.length; i++)
	{
		new PopupListModel(root, strings[i], shell.getDisplay());
	}
	
	list.refresh();
}
private String prevKey = "";
public boolean setItems (String key) {
	if(key.equals("")) return true;
	if(!prevKey.equals("") && key.contains(prevKey) && ((PopupListModel)list.getInput()).getChildren().size() > 0)
	{
		PopupListModel root = (PopupListModel)list.getInput();
		int length = root.getChildren().size();
		ArrayList<TableModel> rmList = new ArrayList<TableModel>();
		for(int i=0; i<length; i++)
		{
			TableModel child = root.getChildren().get(i);
			if(!child.getID().contains(key)) rmList.add(child);
		}
		
		for(int i=0; rmList.size()>i; i++)
		{
			root.getChildren().remove(rmList.get(i));
		}
		
		prevKey = key;
		list.refresh();
		return true;
	}
	else
	{
		//NEW SEARCH
		prevKey = key;
		return false;
	}
	
	/*if(root.getChildren().size() != length && root.getChildren().size() != 0)
	{
		list.refresh();
		return true;
	}
	else
	{
		return false;
	}*/
		
}

}
