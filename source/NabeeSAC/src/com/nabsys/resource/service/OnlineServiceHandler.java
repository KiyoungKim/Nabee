package com.nabsys.resource.service;

public class OnlineServiceHandler extends GateServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String protocolID = null;
	private String inboundTelegramID = null;
	private String outboundTelegramID = null;

	public OnlineServiceHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(null, x, y, width, height);
	}
	
	public void setProtocolID(String protocolID)
	{
		this.protocolID = protocolID;
	}
	
	public void setInboundTelegramID(String telegramID)
	{
		this.inboundTelegramID = telegramID;
	}
	
	public void setOutboundTelegramID(String telegramID)
	{
		this.outboundTelegramID = telegramID;
	}
	
	public String getProtocolID()
	{
		return protocolID;
	}
	
	public String getInboundTelegramID()
	{
		return inboundTelegramID;
	}
	
	public String getOutboundTelegramID()
	{
		return outboundTelegramID;
	}
}
