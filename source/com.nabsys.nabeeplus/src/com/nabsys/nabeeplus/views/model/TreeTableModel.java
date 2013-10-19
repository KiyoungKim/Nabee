package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class TreeTableModel extends Model {
	public TreeTableModel()
	{
		super();
	}
	
	public TreeTableModel(Model parent, String name, Image image){
		super(parent, name, image);
	}
	
	public boolean canEdit(int columnIndex)
	{
		return false;
	}
	
	public Object _getValue(int columnIndex)
	{
		return null;
	}
	
	public void _setValue(int columnIndex, Object object)
	{
	}
}
