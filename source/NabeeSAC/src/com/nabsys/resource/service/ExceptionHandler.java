package com.nabsys.resource.service;

import java.util.HashMap;
import java.util.Iterator;

import com.nabsys.common.logger.NLogger;
import com.nabsys.process.Context;

public class ExceptionHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Object> mapping = null;
	private Exception exception = null;
	public ExceptionHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
		if(parent instanceof ThreadHandler) ((ThreadHandler)parent).setExceptionHandler(this);
	}
	
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	public void setData(HashMap<String, Object> mapping)
	{
		this.mapping = mapping;
	}
	
	public HashMap<String, Object> getMappingData()
	{
		return mapping;
	}

	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		if(mapping != null){
			Iterator<String> itr = mapping.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				Object value = null;
				if(mapping.get(key).equals("Exception Type"))
				{
					value = exception.toString();
				}
				else if(mapping.get(key).equals("Exception Message"))
				{
					value = exception.getMessage();
				}
				else if(mapping.get(key).equals("Stack Trace"))
				{
					value = "\n"+exception.getMessage();
					for(int i=0; i<exception.getStackTrace().length; i++)
					{
						value = value + 
							 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
									   	   , exception.getStackTrace()[i].getClassName()
									   	   , exception.getStackTrace()[i].getMethodName()
									   	   , String.valueOf(exception.getStackTrace()[i].getLineNumber()));
					}
				}
				else
				{
					value = mapping.get(key);
				}
				map.put(key, value);
			}
		}
		
		NLogger logger = ctx.getLogger(this.getClass());
		logger.error(exception, exception.getMessage());
		
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Exception : " + map + "", true, false);
		}
		super.execute(ctx, map);
	}
}
