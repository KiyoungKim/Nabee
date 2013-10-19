package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.net.protocol.IPC.IPC;


public class Component extends TableModel {
	private String instance = "";
	private String name = "";
	private String clazz = "";
	private String file = "";
	
	public Component() {
		super();
	}
	
	public Component(TableModel parent, String id) {
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
			return getInstanceName();
		case 1:
			return getComponentID();
		case 2:
			return getComponentName();
		case 3:
			return getComponentClass();
		case 4:
			return getComponentFile();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getInstanceName();
		case 1:
			return getComponentID();
		case 2:
			return getComponentName();
		case 3:
			return getComponentClass();
		case 4:
			return getComponentFile();
		default:
		}
		return "";
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		switch(columnIndex)
		{
		case 0:
			if(!instance.equals((String)value)) isModified = true;
			setInstanceName((String)value);
			break;
		case 1:
			if(!super.getID().equals((String)value)) isModified = true;
			super.setID((String)value);
			break;
		case 2:
			if(!name.equals((String)value)) isModified = true;
			setComponentName((String)value);
			break;
		case 3:
			if(!clazz.equals((String)value)) isModified = true;
			setComponentClass((String)value);
			break;
		case 4:
			if(!file.equals((String)value)) isModified = true;
			setComponentFile((String)value);
			break;
		default:
		}
		
		if(isModified)
		{
			switch(columnIndex)
			{
			case 0:
				return IPC.NB_INSTNCE_ID;
			case 1:
				return "ID";
			case 2:
				return "NAME";
			case 3:
				return "CLASS";
			case 4:
				return "METHOD";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public void setInstanceName(String instance) {
		this.instance = instance;
	}
	
	public String getInstanceName() {
		return instance;
	}
	
	public String getComponentID() {
		return super.getID();
	}

	public String getComponentName() {
		return name;
	}

	public void setComponentName(String name) {
		this.name = name;
	}
	
	public String getComponentClass() {
		return clazz;
	}

	public void setComponentClass(String clazz) {
		this.clazz = clazz;
	}
	
	public String getComponentFile() {
		return file;
	}
	
	public void setComponentFile(String file) {
		this.file = file;
	}
}
