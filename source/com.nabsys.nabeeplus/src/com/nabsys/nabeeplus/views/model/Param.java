package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class Param extends TableModel {
	private String value = "";
	private int type = 0;
	private String[] typeStr = new String[]{"CHAR", "NUMBER"};
	public Param() {
		super();
	}
	
	public Param(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Param(TableModel parent, String id, Display display) {
		super(parent, id);
	}

	public Image _getImage(int columnIndex)
	{
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getParamID();
		case 1:
			return typeStr[type];
		case 2:
			return getParamValue();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getParamID();
		case 1:
			return type;
		case 2:
			return getParamValue();
		default:
		}
		return "";
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		switch(columnIndex)
		{
		case 0:
			if(!super.getID().equals((String)value)) isModified = true;
			super.setID((String)value);
			break;
		case 1:
			if(type != (Integer)value) isModified = true;
			type = (Integer)value;
			break;
		case 2:
			if(!this.value.equals((String)value)) isModified = true;
			setParamValue((String)value);
			break;
		default:
		}


		if(isModified)
		{
			switch(columnIndex)
			{
			case 0:
				return "ID";
			case 1:
				return "TYPE";
			case 2:
				return "VALUE";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getParamID() {
		return super.getID();
	}
	
	public String getType() {
		return typeStr[type];
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getParamValue() {
		return value;
	}

	public void setParamValue(String value) {
		this.value = value;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
