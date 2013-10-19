package com.nabsys.resource.script;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionExecutor extends ArrayList<Comparison>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Object execute(HashMap<String, Object> map) throws Exception
	{
		for(int i=0; i<size(); i++)
		{
			Object obj = null;
			if((obj = get(i).execute(map)) != null)
			{
				if(obj instanceof StringBuilder) return ((StringBuilder)obj).toString();
				else if(obj instanceof BigDecimal) return ((BigDecimal)obj).getValue();
				else return obj;
			}
		}
		
		return null;
	}
	
	public Object execute(HashMap<String, Object> map, int index) throws Exception
	{
		for(int i=0; i<size(); i++)
		{
			Object obj = null;
			if((obj = get(i).execute(map, index)) != null)
			{
				if(obj instanceof StringBuilder) return ((StringBuilder)obj).toString();
				else if(obj instanceof BigDecimal) return ((BigDecimal)obj).getValue();
				else return obj;
			}
		}
		
		return null;
	}
}
