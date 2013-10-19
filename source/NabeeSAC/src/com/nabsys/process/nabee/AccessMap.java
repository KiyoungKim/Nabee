package com.nabsys.process.nabee;

import java.util.HashMap;

public class AccessMap extends HashMap<Long, ThreadControler> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4174354321669244531L;
	private int instanceCnt = 0;
	
	public void put(long key, ThreadControler threadControler)
	{
		super.put(key, threadControler);
	}
	
	public void remove(long key, boolean isInstance)
	{
		super.remove(key);
		
		if(isInstance)
			instanceCnt--;
	}
	
	public void addInstanceThread()
	{
		instanceCnt++;
	}
	
	public int getInstanceCnt()
	{
		return instanceCnt;
	}
}
