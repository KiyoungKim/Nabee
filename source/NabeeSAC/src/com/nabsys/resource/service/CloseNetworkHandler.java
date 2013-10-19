package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class CloseNetworkHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseNetworkHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}

	protected void execute(Context ctx, HashMap<String, Object> map)
			throws Exception{
		ctx.getChannel().close();
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Close Network : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}

}
