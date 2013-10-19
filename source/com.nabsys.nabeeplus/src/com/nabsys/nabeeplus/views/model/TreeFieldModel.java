package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class TreeFieldModel extends TreeTableModel {
	int type = 0;
	String value = "";

	public TreeFieldModel(Model parent, String name, Image image){
		super(parent, name, image);
	}
	
	public boolean canEdit(int columnIndex)
	{
		return true;
	}
	
	public Object _getValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0: 
			return getName();
		case 1:
			return type;
		case 2:
			return value;
		}
		
		return null;
	}
	
	public void _setValue(int columnIndex, Object object)
	{
		switch(columnIndex)
		{
		case 0: 
			setName((String)object);
			break;
		case 1:
			type = (Integer)object;
			break;
		case 2:
			value = (String)object;
			break;
		}
	}
	
	public int getType()
	{
		return type;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public void setValue(Object value)
	{
		this.value = value + "";
	}
}
