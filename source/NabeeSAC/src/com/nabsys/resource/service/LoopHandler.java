package com.nabsys.resource.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.nabsys.process.Context;

public class LoopHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String listKey = "";

	public LoopHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String listKey)
	{
		this.listKey = listKey;
	}
	
	public String getListKey(){return listKey;}
	
	@SuppressWarnings("unchecked")
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			if(listKey != null && !listKey.equals("") && map.containsKey(listKey))
			{
				int index = 0;
				ArrayList<Object> list = (ArrayList<Object>)map.get(listKey);
				if(ctx.isTest())
				{
					ctx.offerTestMessage(getHandlerID(), "Loop : " + map + "", false, false);
				}
				while(index < list.size())
				{
					super.moveToInside(ctx, (HashMap<String, Object>)list.get(index));
					index++;
					if(isBreak)
					{
						isBreak = false;
						break;
					}
				}
			}
			else
			{
				throw new Exception("Mapping list needed.");
			}
		}
		
		super.execute(ctx, map);
	}
}
