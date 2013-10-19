package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.nabeeplus.views.model.TableModel;

public class TelegramModel  extends TableModel{
	private String name = "";
	private String type = "";
		
	public TelegramModel() {
		super();
	}
	
	public TelegramModel(TableModel parent, String id) {
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
			return name;
		case 2:
			return type;
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		return null;
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
		
	public void setType(String type) {
		this.type = type;
	}
}
