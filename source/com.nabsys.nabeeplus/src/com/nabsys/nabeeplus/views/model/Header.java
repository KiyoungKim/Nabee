package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class Header extends TableModel {
	private String name = "";
	private String remark = "";
	
	public Header() {
		super();
	}
	
	public Header(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Header(TableModel parent, String id, Display display) {
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
			return getHeaderID();
		case 1:
			return getHeaderName();
		case 2:
			return getHeaderRemark();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getHeaderID();
		case 1:
			return getHeaderName();
		case 2:
			return getHeaderRemark();
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
			setHeaderName((String)value);
			break;
		case 2:
			if(remark != (String)value) isModified = true;
			setHeaderRemark((String)value);
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
				return "REMARK";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getHeaderID() {
		return super.getID();
	}

	public String getHeaderName() {
		return name;
	}

	public void setHeaderName(String name) {
		this.name = name;
	}
	
	public String getHeaderRemark() {
		return this.remark;
	}

	public void setHeaderRemark(String remark) {
		this.remark = remark;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
