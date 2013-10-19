package com.nabsys.database;

import java.io.Serializable;

public class SqlStructure implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5690288962938278212L;
	
	private SqlStructureChildren				 	children 		= new SqlStructureChildren();
	private SqlStructure 							parent 			= null;
	private String									id				= "";
	private int										descendantsCnt	= 0;
	private String									path			= "";
	
	public SqlStructure(String id)
	{
		this.id = id;
		path = "/";
	}
	
	public SqlStructure(SqlStructure parent, String id)
	{
		this.parent = parent;
		this.parent.addDescendantsCnt();
		this.id = id;
		if(parent != null)
		{
			this.parent.getChildren().put(id, this);
			path = this.parent.getPath() + id + "/";
		}
		else
		{
			path = "/";
		}
	}
	
	public int getDescendatnCnt()
	{
		return this.descendantsCnt;
	}
	
	protected void addDescendantsCnt()
	{
		this.descendantsCnt++;
		if(this.parent != null)
		{
			this.parent.addDescendantsCnt();
		}
	}
	
	protected void removeDesencendantCnt(int number)
	{
		this.descendantsCnt -= number;
		if(this.parent != null)
		{
			this.parent.removeDesencendantCnt(number);
		}
	}
	
	public SqlStructure getRoot()
	{
		if(parent == null) return this;
		else return parent.getRoot();
	}
	
	public String getID()
	{
		return this.id;
	}
	
	public void setID(String id)
	{
		if(parent.getChildren().containsKey(this.id)) parent.getChildren().remove(this.id);
		parent.getChildren().put(id, this);
		this.id = id;
	}
	
	public SqlStructureChildren getChildren()
	{
		return this.children;
	}
	
	public SqlStructure getParent()
	{
		return this.parent;
	}
	
	public String getPath()
	{
		if(this.path == null) return "/";
		else return this.path;
	}
}
