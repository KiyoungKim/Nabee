package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.nabeeplus.views.model.TableModel;

public class FileMappingModel extends TableModel{
	private int type = 0;
	private int size = 0;
	private String padding = "";
	public FileMappingModel() {
		super();
	}
	
	public FileMappingModel(TableModel parent, String id) {
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
			return super.getID();
		case 1:
			return FieldTypeArray.TYPE[type];
		case 2:
			return Integer.toString(size);
		case 3:
			return padding;
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return super.getID();
		case 1:
			return type;
		case 2:
			return Integer.toString(size);
		case 3:
			return padding;
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
			if(size != Integer.parseInt((String)value)) isModified = true;
			size = Integer.parseInt((String)value);
			break;
		case 3:
			if(!padding.equals((String)value)) isModified = true;
			padding = (String)value;
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
				return "TP";
			case 2:
				return "SZ";
			case 3:
				return "PD";
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
		return FieldTypeArray.TYPE[type];
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public String getPadding() {
		return padding;
	}
	
	public void setPadding(String padding) {
		this.padding = padding;
	}
}
