package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class TimerHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long sleepTime = 0L;
	
	public TimerHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	public void setData(long sleepTime){
		this.sleepTime = sleepTime;
	}
	public long getSleepTime()
	{
		return this.sleepTime;
	}
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		try{
			if(ctx.isTest())
			{
				ctx.offerTestMessage(getHandlerID(), "Timer : " + map + "", false, false);
			}
			
			Thread.sleep(sleepTime);
		}catch(InterruptedException e){
			(ctx.getLogger(this.getClass())).debug(e.getMessage());
		}
		super.execute(ctx, map);
	}

}
