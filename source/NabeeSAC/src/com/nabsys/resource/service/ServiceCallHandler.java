package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;
import com.nabsys.process.exception.ServiceTypeException;
import com.nabsys.resource.ServiceContext;

public class ServiceCallHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String serviceID = null;
	private String telegramID = null;
	
	public ServiceCallHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String serviceID, String telegramID){
		this.serviceID = serviceID;
		this.telegramID = telegramID;
	}
	
	public String getServiceID()
	{
		return this.serviceID;
	}
	
	public String getTelegramID()
	{
		return telegramID;
	}

	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
		
			//MessageQueue Service 와 General Service 만 호출 가능...
			ServiceContext service = ctx.getResourceFactory().getService(serviceID);
			if(!service.getType().equals("MessageQueue") && !service.getType().equals("General"))
			{
				throw new ServiceTypeException();
			}
			String orginalServiceID = ctx.getServiceID();
			ctx.setServiceID(serviceID);
			if(service.getType().equals("MessageQueue"))
			{
				ctx.setData("__TELEGRAM_ID", telegramID);
			}
			
			if(ctx.isTest())
			{
				ctx.offerTestMessage(getHandlerID(), "Service Caller : " + map + "", false, false);
			}
			
			service.getServiceDesign().execute(ctx, map);
			ctx.setServiceID(orginalServiceID);
		}
		
		super.execute(ctx, map);
	}

}
