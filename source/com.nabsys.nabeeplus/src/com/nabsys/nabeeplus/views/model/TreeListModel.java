package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class TreeListModel extends TreeTableModel {
	
	public TreeListModel(Model parent, String name, Image image){
		super(parent, name, image);
	}
	
	public boolean canEdit(int columnIndex)
	{
		return columnIndex == 0;
	}
	
	public Object _getValue(int columnIndex)
	{
		if(columnIndex == 0) return getName();
		else return "";
	}
	
	public void _setValue(int columnIndex, Object object)
	{
		if(columnIndex == 0) setName((String)object);
	}
}
