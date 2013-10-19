package com.nabsys.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public final class TagDocument implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6431263940644756377L;
	private HashMap<String, String> properties 	= null;
	private String 					textValue 	= null;
	private ArrayList<TagDocument> 	children 	= null;
	private TagDocument 			parent 		= null;
	private ArrayList<String> 		params		= null;
	private int						type		= 0;
	protected static int			PROPERTY_NODE = 0;
	protected static int			TEXT_NODE = 1;
	
	protected TagDocument(TagDocument parent)
	{
		this.parent = parent;
		this.children = new ArrayList<TagDocument>();
		this.params = new ArrayList<String>();
		
		if(parent != null)
			this.parent.addChildren(this);
	}
	
	protected void addChildren(TagDocument child)
	{
		children.add(child);
	}
	
	protected ArrayList<TagDocument> getChildren()
	{
		return children;
	}
	
	protected void setProperty(HashMap<String, String> properties)
	{
		this.type = PROPERTY_NODE;
		this.properties = properties;
	}
	
	protected HashMap<String, String> getProperty()
	{
		return this.properties;
	}
	
	protected void addParams(String name)
	{
		params.add(name);
	}
	
	protected ArrayList<String> getParams()
	{
		return this.params;
	}
	
	protected void setTextValue(String textValue)
	{
		textValue = textValue.replaceAll("\n+\\p{javaWhitespace}*$", "");
		textValue = textValue.replaceAll("^\\p{javaWhitespace}*\n", "");
		
		if(textValue.trim().length() <= 0) return;
		
		if(textValue.substring(textValue.length()-1).equals("\n"))
			this.textValue = textValue.substring(0, textValue.length()-1);
		else
			this.textValue = textValue;
		
		this.type = TEXT_NODE;
	}
	
	protected int getType()
	{
		return this.type;
	}
	
	protected String getTextValue()
	{
		return this.textValue;
	}
}
