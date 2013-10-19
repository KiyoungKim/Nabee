package com.nabsys.nabeeplus.design.model;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import com.nabsys.nabeeplus.views.model.Model;

public class TreeEditingSupport extends EditingSupport {
	private CellEditor 					editor 				= null;
	public TreeEditingSupport(ColumnViewer viewer, CellEditor editor) {
		super(viewer);
		this.editor = editor;
	}
	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}
	@Override
	protected Object getValue(Object element) {
		return ((Model)element).getName();
	}
	@Override
	protected void setValue(Object element, Object value) {
		((Model)element).setName((String)value);
		getViewer().refresh();
	}

}
