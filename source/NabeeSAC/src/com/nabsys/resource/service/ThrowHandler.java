package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class ThrowHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;
	
	public ThrowHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}

	public void setData(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void execute(Context ctx, HashMap<String, Object> map)
			throws Exception{
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Throw Exception : " + map + "", false, false);
		}
		throw new ServiceException(message);
	}
	
	
}
