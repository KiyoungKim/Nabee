package com.nabsys.nabeeplus.views.model;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

public class InstanceList extends Model {

	private ArrayList<String> list = null;
	
	public InstanceList(Model parent, String name, Image image) {
		super(parent, name, image);
	}
	
	public void setList(ArrayList<String> list)
	{
		this.list = list;
	}

	public ArrayList<String> getList()
	{
		return this.list;
	}
}
