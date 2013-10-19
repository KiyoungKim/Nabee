package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.net.protocol.IPC.IPC;


public class Transaction extends TableModel {
	private String instance = "";
	private String name = "";
	private String actType = "";
	private String remark = "";
	
	public Transaction() {
		super();
	}
	
	public Transaction(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Transaction(TableModel parent, String id, Display display) {
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
			return getTransactionID();
		case 2:
			return getTransactionName();
		case 3:
			return getTransactionActType();
		case 4:
			return getTransactionRemark();
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
			return getTransactionID();
		case 2:
			return getTransactionName();
		case 3:
			return getTransactionActType();
		case 4:
			return getTransactionRemark();
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
			setTransactionName((String)value);
			break;
		case 3:
			if(!actType.equals((String)value)) isModified = true;
			setTransactionActType((String)value);
			break;
		case 4:
			if(remark != (String)value) isModified = true;
			setTransactionRemark((String)value);
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
				return "ACT_TYPE";
			case 4:
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

	public String getTransactionID() {
		return super.getID();
	}
	
	public String getInstanceName() {
		return instance;
	}
	
	public String getTransactionActType() {
		return actType;
	}

	public String getTransactionName() {
		return name;
	}
	
	public void setInstanceName(String instance) {
		this.instance = instance;
	}

	public void setTransactionName(String name) {
		this.name = name;
	}
	
	public void setTransactionActType(String actType) {
		this.actType = actType;
	}
	
	public String getTransactionRemark() {
		return this.remark;
	}

	public void setTransactionRemark(String remark) {
		this.remark = remark;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
