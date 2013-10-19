package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.net.protocol.IPC.IPC;


public class Telegram extends TableModel {
	private String instance = "";
	private String name = "";
	private String remark = "";
	
	public Telegram() {
		super();
	}
	
	public Telegram(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Telegram(TableModel parent, String id, Display display) {
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
			return getTelegramID();
		case 2:
			return getTelegramName();
		case 3:
			return getTelegramRemark();
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
			return getTelegramID();
		case 2:
			return getTelegramName();
		case 3:
			return getTelegramRemark();
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
			setTelegramName((String)value);
			break;
		case 3:
			if(remark != (String)value) isModified = true;
			setTelegramRemark((String)value);
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

	public String getTelegramID() {
		return super.getID();
	}
	
	public String getInstanceName() {
		return instance;
	}

	public String getTelegramName() {
		return name;
	}
	
	public void setInstanceName(String instance) {
		this.instance = instance;
	}

	public void setTelegramName(String name) {
		this.name = name;
	}
	
	public String getTelegramRemark() {
		return this.remark;
	}

	public void setTelegramRemark(String remark) {
		this.remark = remark;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
