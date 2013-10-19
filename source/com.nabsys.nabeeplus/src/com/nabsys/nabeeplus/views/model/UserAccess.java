package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class UserAccess extends TableModel {
	private int 			auth = 2;
	private String 			name = "";
	private String 			phone = "";
	
	public UserAccess() {
		super();
	}
	
	public UserAccess(TableModel parent, String id) {
		super(parent, id);
	}
	
	public UserAccess(TableModel parent, String id, Display display) {
		super(parent, id);
	}

	public String getUserId()
	{
		return super.getID();
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
			return getUserId();
		case 1:
			return getAuth();
		case 2:
			return getName();
		case 3:
			return getPhone();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getUserId();
		case 1:
			return auth;
		case 2:
			return getName();
		case 3:
			return getPhone();
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
			if(auth != (Integer)value) isModified = true;
			auth = (Integer)value;
			break;
		case 2:
			if(!name.equals((String)value)) isModified = true;
			setName((String)value);
			break;
		case 3:
			if(!phone.equals((String)value)) isModified = true;
			setPhone((String)value);
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
				return "ROLE";
			case 2:
				return "NAME";
			case 3:
				return "PHONE";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getAuth() {
		return AuthArray.AUTH[auth];
	}

	public void setAuth(int auth) {
		this.auth = auth;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
