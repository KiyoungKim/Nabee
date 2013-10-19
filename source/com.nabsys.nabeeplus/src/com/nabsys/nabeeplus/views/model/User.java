package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;


public class User extends TableModel {
	private String 			password = "";
	private int 			auth = 2;
	private String 			name = "";
	private String 			phone = "";
	private boolean 		active = false;
	private static Image 	CHECKED = null;
	private static Image 	UNCHECKED = null;

	
	public User() {
		super();
	}
	
	public User(TableModel parent, String id) {
		super(parent, id);
	}
	
	public User(TableModel parent, String id, Display display) {
		super(parent, id);
		CHECKED = Activator.getImageDescriptor("/icons/checked.gif").createImage(display);
		UNCHECKED = Activator.getImageDescriptor("/icons/unchecked.gif").createImage(display);
	}

	public String getUserId()
	{
		return super.getID();
	}
	
	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 5)
		{
			if(isActive()) return CHECKED;
			else return UNCHECKED;
		}
		
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getUserId();
		case 1:
			return "*********";
		case 2:
			return getAuth();
		case 3:
			return getName();
		case 4:
			return getPhone();
		case 5:
			//if(isActive()) return NBLabel.get(0x0122);
			//else return NBLabel.get(0x0123);
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
			return "";
		case 2:
			return auth;
		case 3:
			return getName();
		case 4:
			return getPhone();
		case 5:
			return isActive();
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
			if(!password.equals((String)value)) isModified = true;
			setPassword((String)value);
			break;
		case 2:
			if(auth != (Integer)value) isModified = true;
			auth = (Integer)value;
			break;
		case 3:
			if(!name.equals((String)value)) isModified = true;
			setName((String)value);
			break;
		case 4:
			if(!phone.equals((String)value)) isModified = true;
			setPhone((String)value);
			break;
		case 5:
			if(active != (Boolean)value) isModified = true;
			setActive((Boolean)value);
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
				return "PW";
			case 2:
				return "ROLE";
			case 3:
				return "NAME";
			case 4:
				return "PHONE";
			case 5:
				return "ATV";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
