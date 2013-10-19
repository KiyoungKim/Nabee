package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class ComponentModel extends TableModel{
	private String name = "";
	private String clazz = "";
	private String method = "";
	private String paramStr = "";
	private String returnType = "";
	
	public ComponentModel() {
		super();
	}
	
	public ComponentModel(TableModel parent, String id) {
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
			return getComponentID();
		case 1:
			return getComponentName();
		case 2:
			return getComponentClass();
		case 3:
			return getComponentMethod();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getComponentID();
		case 1:
			return getComponentName();
		case 2:
			return getComponentClass();
		case 3:
			return getComponentMethod();
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
			setComponentName((String)value);
			break;
		case 2:
			if(!clazz.equals((String)value)) isModified = true;
			setComponentClass((String)value);
			break;
		case 3:
			if(!method.equals((String)value)) isModified = true;
			setComponentMethod((String)value);
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
			case 3:
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
	
	public String getComponentMethod() {
		return method;
	}

	public void setComponentMethod(String method) {
		this.method = method;
	}
	
	public void setComponentParamStr(String paramStr)
	{
		this.paramStr = paramStr;
	}
	
	public String getComponentParamStr()
	{
		return this.paramStr;
	}
	
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
	
	public String getReturnType()
	{
		return this.returnType;
	}
	
	
}
