package com.nabsys.resource.service;

import java.io.Serializable;

import com.nabsys.resource.script.FunctionExecutor;

public class MappingObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String script = null;
	private String targetMapKey = null;
	private FunctionExecutor functionExecutor = null;
	private int index = 0;
	
	public MappingObject(String targetKey, FunctionExecutor functionExecutor, String script, int index)
	{
		this.targetMapKey = targetKey;
		this.functionExecutor = functionExecutor;
		this.script = script;
		this.index = index;
	}
	
	public String getScript()
	{
		return this.script;
	}
	
	public String getTargetMapKey()
	{
		return targetMapKey;
	}
	
	public FunctionExecutor getFunctionExecutor()
	{
		return this.functionExecutor;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}
