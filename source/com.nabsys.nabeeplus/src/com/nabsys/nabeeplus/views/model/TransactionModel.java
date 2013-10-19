package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class TransactionModel extends TableModel{
	private String name = "";
	private String remark = "";
	
	public TransactionModel() {
		super();
	}
	
	public TransactionModel(TableModel parent, String id) {
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
			return getTransactionID();
		case 1:
			return getTransactionName();
		case 2:
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
			return getTransactionID();
		case 1:
			return getTransactionName();
		case 2:
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
			if(!super.getID().equals((String)value)) isModified = true;
			super.setID((String)value);
			break;
		case 1:
			if(!name.equals((String)value)) isModified = true;
			setTransactionName((String)value);
			break;
		case 2:
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

	public String getTransactionID() {
		return super.getID();
	}
	
	public String getTransactionName() {
		return name;
	}
	
	public void setTransactionName(String name) {
		this.name = name;
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
