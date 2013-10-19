package com.nabsys.nabeeplus.views.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;


public class Model {
	private ViewerRow row = null;
	protected Model parent = null;
	protected String name = null;
	protected Image image = null;
	private ArrayList<Model> list = null;
	private boolean visited = false;
	private HashMap<String, Object> data = null;
	private int index = 0;
	public Model()
	{
		list = new ArrayList<Model> ();
		parent = null;
	}
	
	public Model(Model parent, String name, Image image) 
	{
		this.name = name;
		this.image = image;
		this.parent = parent;
		this.parent.getChildren().add(this);
		list = new ArrayList<Model> ();
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public void setData(String key, Object data)
	{
		if(this.data == null) this.data = new HashMap<String, Object>();
		
		this.data.put(key, data);
	}
	
	public Object getData(String key)
	{
		if(this.data == null) return null;
		else return this.data.get(key);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Model getParent()
	{
		return parent;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Model getRoot()
	{
		if(parent == null) return parent;
		else return parent.getRoot();
	}
	
	public ArrayList<Model> getChildren()
	{
		return list;
	}
	
	public String getPath()
	{
		if(parent != null)
			return parent.getPath() + "/" + name;
		else
			return null;
	}
	
	public void removeChild(String name)
	{
		for(int i=0; i<getChildren().size(); i++)
		{
			if(getChildren().get(i).getName().equals(name))
			{
				getChildren().remove(i);
				return;
			}
		}
	}
	
	public void removeListener(INBListener listener)
	{
	}
	
	public void addListener(INBListener listener)
	{
	}

	public boolean isVisited() {
		return visited;
	}
	public void setViewerRow(ViewerRow row)
	{
		if(this.row == null)
		{
			this.row = (ViewerRow)row.clone();
		}
	}
	public Rectangle getLinkRectangle()
	{
		if(this.row == null) return null;
		return this.row.getBounds(0);
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	
	public Object getColumnValue(int columnIndex)
	{
		return name;
	}
	
	public void setColumnValue(int columnIndex, Object value)
	{
		this.name = (String)value;
	}
}
