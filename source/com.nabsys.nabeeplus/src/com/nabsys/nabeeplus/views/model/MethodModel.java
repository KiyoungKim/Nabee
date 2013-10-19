package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class MethodModel  extends TableModel{
	private String type = "";
	private String method = "";
	private String params = "";
	
	public MethodModel() {
		super();
	}
	
	public MethodModel(TableModel parent, String id) {
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
			return getMethod();
		case 2:
			return getParams();
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
			return getMethod();
		case 2:
			return getParams();
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
			if(!method.equals((String)value)) isModified = true;
			setMethod((String)value);
			break;
		case 2:
			if(!params.equals((String)value)) isModified = true;
			setParams((String)value);
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
				return "METHOD";
			case 2:
				return "PARAMS";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}

	public String getParams() {
		return params;
	}
	
	public void setParams(String params) {
		this.params = params;
	}
}
