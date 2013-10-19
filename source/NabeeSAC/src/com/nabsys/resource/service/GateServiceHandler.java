package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class GateServiceHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GateServiceHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Service Information : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
}
