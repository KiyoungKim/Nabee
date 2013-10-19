package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class ThreadHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExceptionHandler exceptionHandler = null;
	public ThreadHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	protected void setExceptionHandler(ExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}
	
	protected ExceptionHandler getExceptionHandler()
	{
		return exceptionHandler;
	}
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Thread : " + map + "", false, false);
		}
		
		{
			ThreadExecutor te = new ThreadExecutor(this, ctx, map);
			te.start();
		}
		super.execute(ctx, map);
	}

}
