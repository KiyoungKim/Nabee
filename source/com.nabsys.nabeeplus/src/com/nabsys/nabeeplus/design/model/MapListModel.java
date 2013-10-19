package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.nabeeplus.views.model.Model;

public class MapListModel extends Model {
	private String instanceID = null;
	public MapListModel(Model input, String string, Image createImage) {
		super(input, string, createImage);
	}
	
	public String getInstanceID()
	{
		return this.instanceID;
	}
	
	public void setInstanceID(String instanceID)
	{
		this.instanceID = instanceID;
	}
}
