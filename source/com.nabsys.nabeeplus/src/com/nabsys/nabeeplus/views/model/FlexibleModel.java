package com.nabsys.nabeeplus.views.model;

import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Image;

public class FlexibleModel extends TableModel {
	
	LinkedHashMap<Integer, String> data = null;
	
	public FlexibleModel()
	{
		super();
	}
	
	public FlexibleModel(FlexibleModel parent, LinkedHashMap<Integer, String> data) {
		super(parent, "");
		this.data = data;
	}

	@Override
	public  String _getTxtValue(int columnIndex) {
		return data.get(columnIndex);
	}

	@Override
	public  Object _getObjectValue(int columnIndex) {
		return data.get(columnIndex);
	}

	@Override
	public String _setObjectValue(int columnIndex, Object value) {
		data.put(columnIndex, (String)value);
		return "__Non";
	}
	
	public LinkedHashMap<Integer, String> getData()
	{
		return this.data;
	}
	
	public String getValue(int columnIndex)
	{
		return data.get(columnIndex);
	}

	@Override
	public Image _getImage(int columnIndex) {
		return null;
	}

}
