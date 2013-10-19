package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import com.nabsys.nabeeplus.design.model.ComponentArgumentModel;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;

public class NBEditingSupport extends EditingSupport {

	private CellEditor 					editor 				= null;
	private int 						index 				= 0;
	private NBTableModifiedListener 	listener 			= null;
	
	public NBEditingSupport(ColumnViewer viewer, CellEditor editor, int index) {
		super(viewer);
		this.editor = editor;
		this.index = index;
	}
	
	public NBEditingSupport(ColumnViewer viewer, CellEditor editor, int index, boolean numberOnly) {
		super(viewer);
		this.editor = editor;
		this.index = index;
		
		if(editor instanceof TextCellEditor && numberOnly)
		{
			((Text)editor.getControl()).addVerifyListener(new VerifyListener(){
				public void verifyText(VerifyEvent e) {
					e.doit = e.text.matches("[0-9]*");
				}
			});
		}
	}
	
	public NBEditingSupport(ColumnViewer viewer, CellEditor editor, int index, int textLimit) {
		super(viewer);
		this.editor = editor;
		this.index = index;
		
		if(editor instanceof TextCellEditor)
		{
			((Text)editor.getControl()).setTextLimit(textLimit);
		}
	}
	
	public NBEditingSupport(ColumnViewer viewer, CellEditor editor, int index, boolean numberOnly, int textLimit) {
		super(viewer);
		this.editor = editor;
		this.index = index;
		
		if(editor instanceof TextCellEditor && numberOnly)
		{
			((Text)editor.getControl()).addVerifyListener(new VerifyListener(){
				public void verifyText(VerifyEvent e) {
					e.doit = e.text.matches("[0-9]*");
				}
			});
		}
		
		if(editor instanceof TextCellEditor)
		{
			((Text)editor.getControl()).setTextLimit(textLimit);
		}
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof ComponentArgumentModel)
		{
			ComponentArgumentModel model = (ComponentArgumentModel)element;
			if(!model.isUseSystemValue() && index == 3)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}

	@Override
	protected Object getValue(Object element) {
		return ((TableModel)element).getObjectValue(index);
	}

	@Override
	protected void setValue(Object element, Object value) {
		String keyBf = ((TableModel)element).getID();
		String valueBf = ((TableModel)element).getValue(index);
		String modifiedField = ((TableModel)element).setObjectValue(index, value);
		super.getViewer().refresh();

		if(listener != null && !modifiedField.equals("__Non") && !valueBf.equals(value)) 
			listener.modified((TableModel)element, keyBf, modifiedField, index);
	}
	
	public void addNBTableModifiedListener(NBTableModifiedListener listener)
	{
		this.listener = listener;
	}
	
	public void removeNBTableModifiedListener(NBTableModifiedListener listener)
	{
		this.listener = null;
	}
}
