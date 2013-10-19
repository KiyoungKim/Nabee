package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;

public class ServiceModel extends TableModel{
	private String name = "";
	private String type = "";
	private String remark = "";
	private static Image img = null;
	public ServiceModel() {
		super();
	}
	
	public ServiceModel(TableModel parent, String id, Display display) {
		super(parent, id);
		img = Activator.getImageDescriptor("/icons/field_public_obj.gif").createImage(display);
	}

	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 0)return img;
		else return null;
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
		case 3:
			return remark;
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
	
	public String getServiceID() {
		return super.getID();
	}
	
	public String getServiceName() {
		return name;
	}

	public void setServiceName(String name) {
		this.name = name;
	}
	
	public String getServiceType() {
		return type;
	}

	public void setServiceType(String type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
