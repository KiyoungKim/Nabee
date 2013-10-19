package com.nabsys.nabeeplus.views.model;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

public abstract class TableModel {
	protected TableModel parent = null;
	protected String name = null;
	private ArrayList<TableModel> list = null;
	protected boolean isModified = false;
	
	public TableModel()
	{
		list = new ArrayList<TableModel> ();
		parent = null;
	}
	
	public TableModel(TableModel parent, String name) 
	{
		this.name = name;
		this.parent = parent;
		this.parent.getChildren().add(this);
	}
	
	public void setID(String name) {
		this.name = name;
	}
	
	public TableModel getParent()
	{
		return parent;
	}
	
	public String getID()
	{
		return name;
	}
	
	public abstract String _getTxtValue(int getTxtValue);
	public abstract Object _getObjectValue(int getTxtValue);
	public abstract String _setObjectValue(int columnIndex, Object value);
	public abstract Image _getImage(int columnIndex);
	
	public Image getImage(int columnIndex)
	{
		return _getImage(columnIndex);
	}
	
	public String getValue(int columnIndex)
	{
		return _getTxtValue(columnIndex);
	}
	
	public Object getObjectValue(int columnIndex)
	{
		return _getObjectValue(columnIndex);
	}
	
	public String setObjectValue(int columnIndex, Object value)
	{
		return _setObjectValue(columnIndex, value);
	}
	
	public TableModel getRoot()
	{
		if(parent == null) return parent;
		else return parent.getRoot();
	}
	
	public ArrayList<TableModel> getChildren()
	{
		return list;
	}
	
	public void remove()
	{
		this.parent.getChildren().remove(this);
	}
	
	public boolean isModified()
	{
		return this.isModified;
	}
	
	public void setModified(boolean modified)
	{
		this.isModified = modified;
	}
}
