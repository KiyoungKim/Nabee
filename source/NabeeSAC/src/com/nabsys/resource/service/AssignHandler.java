package com.nabsys.resource.service;

import java.util.HashMap;
import java.util.Iterator;

import com.nabsys.process.Context;

public class AssignHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Object> mapping = null;
	public AssignHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(HashMap<String, Object> mapping)
	{
		this.mapping = mapping;
	}
	
	public HashMap<String, Object> getMappingData()
	{
		return mapping;
	}

	public void execute(Context ctx, HashMap<String, Object> map)
			throws Exception{
		if(mapping != null)
		{
			Iterator<String> itr = mapping.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				Object value = null;
				value = mapping.get(key);
				map.put(key, value);
			}
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Assign : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
}
