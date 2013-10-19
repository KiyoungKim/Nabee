package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.nabeeplus.views.model.TableModel;

public class MappingModel extends TableModel{
	private String mappingData = "";
	private int type = 0;
	public MappingModel() {
		super();
	}
	
	public MappingModel(TableModel parent, String id) {
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
			return mappingData;
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
			return mappingData;
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
			if(!mappingData.equals((String)value)) isModified = true;
			mappingData = (String)value;
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
				return "MD";
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
	public String getMappingData() {
		return mappingData;
	}
	
	public void setMappingData(String mappingData) {
		this.mappingData = mappingData;
	}
}
