package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class Protocol extends TableModel {
	private String name = "";
	private String clazz = "";
	
	public Protocol() {
		super();
	}
	
	public Protocol(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Protocol(TableModel parent, String id, Display display) {
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
			return getProtocolID();
		case 1:
			return getProtocolName();
		case 2:
			return getProtocolClass();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getProtocolID();
		case 1:
			return getProtocolName();
		case 2:
			return getProtocolClass();
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
			setProtocolName((String)value);
			break;
		case 2:
			if(!clazz.equals((String)value)) isModified = true;
			setProtocolClass((String)value);
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
				return "CLASS";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getProtocolID() {
		return super.getID();
	}

	public String getProtocolName() {
		return name;
	}

	public void setProtocolName(String name) {
		this.name = name;
	}
	
	public String getProtocolClass() {
		return clazz;
	}

	public void setProtocolClass(String clazz) {
		this.clazz = clazz;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
