package com.nabsys.nabeeplus.views.model;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBCellClickListener;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.widgets.PopupList;

public class NBPopEditingSupport extends EditingSupport{

	private CellEditor editor = null;
	private int index = 0;
	private NBTableModifiedListener listener = null;
	private ISelectionChangedListener selListener = null; 
	private ColumnViewer viewer = null;
	
	public NBPopEditingSupport(ColumnViewer viewer, CellEditor editor, int index) {
		super(viewer);
		this.editor = editor;
		this.index = index;
		this.viewer = viewer ;
		
		viewer.addPostSelectionChangedListener(genPostSelectionListener());
	}
	
	private ISelectionChangedListener genPostSelectionListener()
	{
		return selListener = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				
			}
		};
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		
		PopupList popupList = new PopupList(editor.getControl().getShell(), SWT.NONE);

		ArrayList<String> list = new ArrayList<String>();
		
		if(cellClickListener != null)
		{
			list = cellClickListener.cellClick(element);
		}
		
		popupList.setItems(list.toArray(new String[list.size()]));

		String selected = popupList.open(new Rectangle(Display.getCurrent().getCursorLocation().x,
														Display.getCurrent().getCursorLocation().y,
														370,
														200));

		if(selected.equals(NBLabel.get(0x023A)))
		{
			return true;
		}
		else
		{
			setValue(element, selected);
			return false;
		}
	}

	@Override
	protected Object getValue(Object element) {
		return ((TableModel)element).getObjectValue(index);
	}

	@Override
	protected void setValue(Object element, Object value) {
		String keyBf = ((TableModel)element).getID();
		String modifiedField = ((TableModel)element).setObjectValue(index, value);
		super.getViewer().refresh();
		if(listener != null && !modifiedField.equals("__Non")) listener.modified((TableModel)element, keyBf, modifiedField, index);
	}
	
	public void addNBTableModifiedListener(NBTableModifiedListener listener)
	{
		this.listener = listener;
	}
	
	private NBCellClickListener cellClickListener = null;
	
	public void addNBCellClickListener(NBCellClickListener listener)
	{
		this.cellClickListener = listener;
	}
	
	public void removeNBTableModifiedListener(NBTableModifiedListener listener)
	{
		this.listener = null;
		this.viewer.removePostSelectionChangedListener(selListener);
	}
	
	public void removeNBCellClickListener()
	{
		this.cellClickListener = null;
	}
}
