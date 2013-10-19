package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class InboundHandler extends ServiceHandler{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String protocolID = null;
	private String telegramID = null;

	public InboundHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String protocolID, String telegramID)
	{
		this.protocolID = protocolID;
		this.telegramID = telegramID;
		((OnlineServiceHandler)getParent()).setProtocolID(protocolID);
		((OnlineServiceHandler)getParent()).setInboundTelegramID(telegramID);
	}
	
	public String getProtocolID()
	{
		return protocolID;
	}
	
	public String getTelegramID()
	{
		return telegramID;
	}
	
	public void testExecute(Context ctx, HashMap<String, Object> map)
			throws Exception {
	}

	protected void execute(Context ctx, HashMap<String, Object> map)
			throws Exception {
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Inbound Network : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
}
