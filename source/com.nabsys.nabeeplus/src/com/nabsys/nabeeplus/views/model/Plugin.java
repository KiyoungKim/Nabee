package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class Plugin extends TableModel {
	private String name = "";
	private int type = 0;
	
	public Plugin() {
		super();
	}
	
	public Plugin(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Plugin(TableModel parent, String id, Display display) {
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
			return getPluginID();
		case 1:
			return getPluginName();
		case 2:
			return getPluginType();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getPluginID();
		case 1:
			return getPluginName();
		case 2:
			return type;
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
			if(!name.equals((String)value)) isModified = true;
			setPluginName((String)value);
			break;
		case 2:
			if(type != (Integer)value) isModified = true;
			type = (Integer)value;
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
				return "NAME";
			case 2:
				return "TYPE";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getPluginID() {
		return super.getID();
	}

	public String getPluginName() {
		return name;
	}

	public void setPluginName(String name) {
		this.name = name;
	}
	
	public String getPluginType() {
		return PluginTypeArray.TYPE[type];
	}

	public void setPluginType(int type) {
		this.type = type;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
