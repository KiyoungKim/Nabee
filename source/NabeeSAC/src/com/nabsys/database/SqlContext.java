package com.nabsys.database;

import java.util.HashMap;

public class SqlContext {
	private String id = null;
	private int level = 0;
	private String type = null;
	private boolean needParam = false;
	private String contents = null;
	private TagDocument tagDocument = null;
	public SqlContext(String id, int level, String type, boolean needParam, String contents, TagDocument tagDocument){
		this.id = id;
		this.level = level;
		this.type = type;
		this.needParam = needParam;
		this.contents = contents;
		this.tagDocument = tagDocument;
	}
	public String getID(){
		return id;
	}
	public int getLevel(){
		return level;
	}
	public String getType(){
		return type;
	}
	public boolean isNeedParam(){
		return needParam;
	}
	public String getContents(){
		return contents;
	}
	public TagDocument getTagDocument(){
		return tagDocument;
	}
	public SqlComposite getSql(HashMap<String, Object> paramMap)
	{
		return compositeSql(tagDocument, paramMap);
	}
	private SqlComposite compositeSql(TagDocument doc, HashMap<String, Object> paramMap)
	{
		SqlComposite sqlComposite = new SqlComposite();
		for(int i=0; i<doc.getChildren().size(); i++)
		{
			TagDocument child = doc.getChildren().get(i);
			
			if(child.getType() == TagDocument.PROPERTY_NODE)
			{
				if(child.getProperty() == null)
				{
					SqlComposite tmp = compositeSql(child, paramMap);
					sqlComposite.setSql(tmp.getSql());
					sqlComposite.setParams(tmp.getParams());
				}
				else
				{
					if(child.getProperty().size() <= 0)
					{
						SqlComposite tmp = compositeSql(child, paramMap);
						sqlComposite.setSql(tmp.getSql());
						sqlComposite.setParams(tmp.getParams());
					}
					else if(compareValues(paramMap.get(child.getProperty().get("parameter")), child.getProperty().get("operator"), child.getProperty().get("value")))
					{
						SqlComposite tmp = compositeSql(child, paramMap);
						sqlComposite.setSql(tmp.getSql());
						sqlComposite.setParams(tmp.getParams());
					}
				}
				
			}
			else if(child.getType() == TagDocument.TEXT_NODE)
			{
				sqlComposite.setSql(child.getTextValue());
				sqlComposite.setParams(child.getParams());
			}
		}
		
		return sqlComposite;
	}
	private boolean compareValues(Object src, String operator, String target)
	{
		try{
			if(target.toUpperCase().equals("NULL"))
			{
				if(operator.equals("=="))
					return src == null;
				else if(operator.equals("!=") || operator.equals("<>"))
					return src != null;
				else
					return false;
			}
			else
			{
				if(operator.equals("=="))
				{
					if(src instanceof String) 	return src.equals(target);
					if(src instanceof Integer) 	return ((Integer)src) 	== Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	== Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	== Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		== Integer.parseInt(target);
					else return false;
				}
				else if(operator.equals("!=") || operator.equals("<>"))
				{
					if(src instanceof String) 	return !src.equals(target);
					if(src instanceof Integer) 	return ((Integer)src) 	!= Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	!= Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	!= Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		!= Integer.parseInt(target);
					else return false;
				}
				else if(operator.equals(">="))
				{
					if(src instanceof String) 	return false;
					if(src instanceof Integer) 	return ((Integer)src) 	>= Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	>= Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	>= Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		>= Integer.parseInt(target);
					else return false;
				}
				else if(operator.equals("<="))
				{
					if(src instanceof String) 	return false;
					if(src instanceof Integer) 	return ((Integer)src) 	<= Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	<= Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	<= Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		<= Integer.parseInt(target);
					else return false;
				}
				else if(operator.equals(">"))
				{
					if(src instanceof String) 	return false;
					if(src instanceof Integer) 	return ((Integer)src) 	> Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	> Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	> Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		> Integer.parseInt(target);
					else return false;
				}
				else if(operator.equals("<"))
				{
					if(src instanceof String) 	return false;
					if(src instanceof Integer) 	return ((Integer)src) 	< Integer.parseInt(target);
					if(src instanceof Float) 	return ((Float)src) 	< Integer.parseInt(target);
					if(src instanceof Double)	return ((Double)src) 	< Integer.parseInt(target);
					if(src instanceof Long)		return ((Long)src) 		< Integer.parseInt(target);
					else return false;
				}
				else
				{
					return false;
				}
			}
		}catch(Exception e){
			return false;
		}
	}
}
