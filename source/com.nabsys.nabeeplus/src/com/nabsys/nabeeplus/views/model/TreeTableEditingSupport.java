package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

public class TreeTableEditingSupport extends EditingSupport {
	private CellEditor 					editor 				= null;
	private int							columnIndex			= 0;
	private ColumnViewer 				viewer				= null;
	public TreeTableEditingSupport(ColumnViewer viewer, CellEditor editor, int columnIndex) {
		super(viewer);
		this.editor = editor;
		this.columnIndex = columnIndex;
		this.viewer = viewer;
	}
	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}
	@Override
	protected boolean canEdit(Object element) {
		return ((TreeTableModel)element).canEdit(columnIndex);
	}
	@Override
	protected Object getValue(Object element) {
		return ((TreeTableModel)element)._getValue(columnIndex);
	}
	@Override
	protected void setValue(Object element, Object value) {
		if(columnIndex == 0)
		{
			TreeTableModel parent = (TreeTableModel)((TreeTableModel)element).getParent();
			for(int i=0; i<parent.getChildren().size(); i++)
			{
				TreeTableModel child = (TreeTableModel)parent.getChildren().get(i);
				if(child == element) continue;
				
				if((child instanceof TreeFieldModel || element instanceof TreeFieldModel) && child.getName().equals((String)value))
				{
					return;
				}
			}
		}
		((TreeTableModel)element)._setValue(columnIndex, value);
		viewer.refresh();
	}

	
}
