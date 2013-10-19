package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.common.logger.NLogger;
import com.nabsys.process.Context;

public class ThreadExecutor extends Thread {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ServiceHandler serviceHandler = null;
	private Context ctx;
	private HashMap<String, Object>map;
	
	public ThreadExecutor(ServiceHandler serviceHandler, Context ctx, HashMap<String, Object>map)
	{
		this.serviceHandler = serviceHandler;
		this.ctx = ctx;
		this.map = map;
	}
	public void run()
	{
		if(serviceHandler instanceof ThreadHandler)
		{
			try{
				serviceHandler.moveToInside(ctx, map);
			} catch(Exception e){
				logger.error(e, e.getMessage());
				try {
					if(((ThreadHandler)serviceHandler).getExceptionHandler() != null)
					((ThreadHandler)serviceHandler).getExceptionHandler().execute(ctx, map);
				} catch (Exception e1) {
					logger.error(e, e.getMessage());
				}
			}
		}
		else
		{
			try{
				serviceHandler.moveToNext(ctx, map);
			} catch (Exception e) {
				logger.error(e, e.getMessage());
			} 
		}
	}
}
