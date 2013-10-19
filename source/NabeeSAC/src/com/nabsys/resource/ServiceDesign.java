package com.nabsys.resource;

import java.io.Serializable;
import java.util.HashMap;

import com.nabsys.common.logger.NLogger;
import com.nabsys.process.Context;
import com.nabsys.resource.service.ExceptionHandler;
import com.nabsys.resource.service.GateServiceHandler;
import com.nabsys.resource.service.MessageQueueServiceHandler;
import com.nabsys.resource.service.OnlineServiceHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ServiceDesign implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = null;
	private String name = null;
	private String type = null;
	private String remark = null;
	private boolean activate = false;
	private GateServiceHandler service = null;
	private ExceptionHandler exceptionHandler = null;
	
	public ServiceDesign(String id, String name, String type, String remark, boolean activate, GateServiceHandler service)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.remark = remark;
		this.service = service;
		this.activate = activate;
	}
	
	public HashMap<String, String> getNetworkInformation()
	{
		if(!type.equals("Online")) return null;
		
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("PID", ((OnlineServiceHandler)service).getProtocolID());
		rtnMap.put("ITID", ((OnlineServiceHandler)service).getInboundTelegramID());
		rtnMap.put("OTID", ((OnlineServiceHandler)service).getOutboundTelegramID());
		return rtnMap;
	}
	
	public int getMessageQueueSize()
	{
		if(!(service instanceof MessageQueueServiceHandler)) return 0;
		return ((MessageQueueServiceHandler)service).getMessageQueueSize();
	}
	
	public void setWorkingQueue(HashMap<String, String> workingQueueProcess)
	{
		if(!(service instanceof MessageQueueServiceHandler)) return;
		((MessageQueueServiceHandler)service).setWorkingQueue(workingQueueProcess);
	}
	
	public void shutdown()
	{
		if(service instanceof MessageQueueServiceHandler)
		{
			((MessageQueueServiceHandler)service).shutdown();
		}
	}
	public void setExceptionHandler(ExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}

	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		try {
			service.execute(ctx, map);
		} catch(RuntimeException e) {
			if(exceptionHandler != null)
			{
				exceptionHandler.setException(e);
				exceptionHandler.execute(ctx, map);
			}
			else
			{
				NLogger logger = ctx.getLogger(this.getClass());
				logger.error(e, e.getMessage());
				if(ctx.isTest())
				{
					ctx.offerTestMessage(0, e.getMessage(), true, true);
				}
				throw e;
			}
		} catch(Exception e) {
			if(exceptionHandler != null)
			{
				exceptionHandler.setException(e);
				exceptionHandler.execute(ctx, map);
			}
			else
			{
				NLogger logger = ctx.getLogger(this.getClass());
				logger.error(e, e.getMessage());
				if(ctx.isTest())
				{
					ctx.offerTestMessage(0, e.getMessage(), true, true);
				}
				throw e;
			}
		}
	}
	public String getID(){
		return id;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getRemark() {
		return remark;
	}
	public boolean isActivate(){
		return activate;
	}
	public ServiceHandler getHandler(){
		return service;
	}
}
