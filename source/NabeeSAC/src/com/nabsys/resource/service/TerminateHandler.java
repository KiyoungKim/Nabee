package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class TerminateHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminateHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	protected void execute(Context ctx, HashMap<String, Object> map)
			throws Exception{
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Terminate : " + map + "", false, true);
		}
		return;
	}

}
