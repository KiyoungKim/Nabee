package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;

public class PopupListModel extends TableModel{
	
	private Image IMG = null;
	
	public PopupListModel() {
		super();
	}
	
	public PopupListModel(TableModel parent, String value, Display display) {
		super(parent, value);

		if(value.equals("NBFields") || value.equals("Context"))
			IMG 	= Activator.getImageDescriptor("/icons/class_default_obj.gif").createImage(display);
		else if(value.equals(NBLabel.get(0x023A)))
			IMG 	= Activator.getImageDescriptor("/icons/field_default_obj.gif").createImage(display);
		else
			IMG 	= Activator.getImageDescriptor("/icons/field_public_obj.gif").createImage(display);
	}

	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 1)
		{
			return IMG;
		}
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 1:
			return "";
		case 2:
			return super.getID();
		default:
		}
		return "";
	}

	@Override
	public Object _getObjectValue(int getTxtValue) {
		return null;
	}

	@Override
	public String _setObjectValue(int columnIndex, Object value) {
		return null;
	}
}
