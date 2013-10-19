package com.nabsys.resource.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.nabsys.process.Context;

public class MappingList extends ArrayList<MappingHandler>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void moveToNextService(Context ctx, HashMap<String, Object> map) throws Exception{
		MappingHandler nextMappingHandler = null;
		DefaultMappingHandler defaultMappingHandler = null;
		boolean isFind = false;
		
		for(int i=0; i<size(); i++)
		{
			nextMappingHandler = get(i);
			if(nextMappingHandler instanceof DefaultMappingHandler)
			{
				defaultMappingHandler = (DefaultMappingHandler)nextMappingHandler;
				continue;
			}
			if(nextMappingHandler.compare(map))
			{
				isFind = true;
				break;
			}
		}
		
		if(isFind)
		{
			 nextMappingHandler.execute(ctx, map);
		}
		else
		{
			if(defaultMappingHandler != null) defaultMappingHandler.execute(ctx, map);
		}
	}
	
}
