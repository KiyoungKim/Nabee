package com.nabsys.resource.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.nabsys.process.Context;

public abstract class ServiceHandler implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MappingList nextServiceList = null;
	private MappingList nextInsideServiceList = null;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private ServiceHandler parent = null;
	private ArrayList<ServiceHandler> children = new ArrayList<ServiceHandler>();
	protected boolean isBreak = false;
	private HashMap<String, Object> componentMap = null;
	private Object designObject = null;
	private int handlerID = 0;

	public ServiceHandler(ServiceHandler parent, int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		if(parent != null) 
		{
			this.parent = parent;
			parent.addChild(this);
		}
		
		Random rand = new Random();
		handlerID = rand.nextInt(100000000);
	}
	
	//하나의 서비스에서 초기화 값을 유지하기 위해
	protected Object getComponent(String componentName)
	{
		if(componentMap == null) return null;
		if(!componentMap.containsKey(componentName)) return null;
		return componentMap.get(componentName);
	}
	
	protected void setComponent(String componentName, Object component)
	{
		if(componentMap == null)componentMap = new HashMap<String, Object>();
		componentMap.put(componentName, component);
	}
	
	protected void addChild(ServiceHandler child)
	{
		children.add(child);
	}
	
	public int getHandlerID()
	{
		return this.handlerID;
	}
	
	public ServiceHandler getParent()
	{
		return this.parent;
	}
	
	public ArrayList<ServiceHandler> getChildren()
	{
		return this.children;
	}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	
	
	public void addNextService(MappingHandler o){
		if(nextServiceList == null) nextServiceList = new MappingList();
		nextServiceList.add(o);
	}
	
	public void addNextInsideService(MappingHandler o){
		if(nextInsideServiceList == null) nextInsideServiceList = new MappingList();
		nextInsideServiceList.add(o);
	}
	
	public MappingList getMappingList()
	{
		return nextServiceList;
	}
	
	public MappingList getInsideMappingList()
	{
		return nextInsideServiceList;
	}
	
	protected void moveToInside(Context ctx, HashMap<String, Object> map) throws Exception{
		isBreak = false;
		if(nextInsideServiceList != null)
		{
			nextInsideServiceList.moveToNextService(ctx, map);
		}
	}
	
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception{
		isBreak = false;
		if(nextServiceList != null)
		{
			nextServiceList.moveToNextService(ctx, map);
		}
	}
	
	protected final void moveToNext(Context ctx, HashMap<String, Object> map) throws Exception{
		isBreak = false;
		if(nextServiceList != null)
		{
			nextServiceList.moveToNextService(ctx, map);
		}
	}
	
	protected void setBreak()
	{
		isBreak = true;
	}

	public Object getDesignObject() {
		return designObject;
	}

	public void setDesignObject(Object designObject) {
		this.designObject = designObject;
	}
}
