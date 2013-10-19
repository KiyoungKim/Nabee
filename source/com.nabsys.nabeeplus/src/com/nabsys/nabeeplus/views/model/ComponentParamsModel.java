package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class ComponentParamsModel extends TableModel{
	private String type = "";
	private String value = "";
	
	public ComponentParamsModel() {
		super();
	}
	
	public ComponentParamsModel(TableModel parent, String id) {
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
			return getType();
		case 1:
			return getParam();
		case 2:
			return getValue();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getType();
		case 1:
			return getParam();
		case 2:
			return getValue();
		default:
		}
		return "";
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		switch(columnIndex)
		{
		case 0:
			if(!type.equals((String)value)) isModified = true;
			setType((String)value);
			break;
		case 1:
			if(!super.getID().equals((String)value)) isModified = true;
			super.setID((String)value);
			break;
		case 2:
			if(!this.value.equals((String)value)) isModified = true;
			setValue((String)value);
			break;
		default:
		}
		
		if(isModified)
		{
			switch(columnIndex)
			{
			case 0:
				return "TYPE";
			case 1:
				return "PARAM";
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
	
	public String getParam() {
		return super.getID();
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
