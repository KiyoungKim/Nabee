package com.nabsys.resource;

import java.sql.SQLException;
import java.util.HashMap;

import com.nabsys.process.ResourceFactory;

public class OnlineServiceContext extends ServiceContext{

	private String serviceIDFieldID = null;
	private String serviceID = null;
	public OnlineServiceContext(String id, String name,
			String type, String remark, byte[] serviceData,
			boolean activate) {
		super(id, name, type, remark, serviceData, activate);
	}
	public void setNetworkInformation(ResourceFactory resourceFactory) throws SQLException
	{
		ServiceDesign serviceDesign = getServiceDesign();
		if(serviceDesign == null) throw new RuntimeException("Service design is null.");
		HashMap<String, String> netInfo = serviceDesign.getNetworkInformation();
		if(netInfo == null) throw new RuntimeException("Network information is null.");
		String protocolID = netInfo.get("PID");
		String inboundTelegramID = netInfo.get("ITID");
		String outboundTelegramID = netInfo.get("OTID");
		
		if(protocolID != null)protocolClass = resourceFactory.getProtocol(protocolID);
		if(inboundTelegramID != null)telegramInboundContext = resourceFactory.getTelegram(inboundTelegramID);
		if(outboundTelegramID != null)telegramOutboundboundContext = resourceFactory.getTelegram(outboundTelegramID);
	}
	private Class<?> protocolClass = null;
	private TelegramContext telegramInboundContext = null;
	private TelegramContext telegramOutboundboundContext = null;
	public Class<?> getOnlineProtocol()
	{
		return protocolClass;
	}
	public TelegramContext getInboundTelegramContext()
	{
		return telegramInboundContext;
	}
	public TelegramContext getOutboundTelegramContext()
	{
		return telegramOutboundboundContext;
	}
	public void setServiceIDFieldID(String serviceIDFieldID){
		this.serviceIDFieldID = serviceIDFieldID;
	}
	public void setServiceID(String serviceId){
		this.serviceID = serviceId;
	}
	public String getServiceIDFieldID(){
		return serviceIDFieldID;
	}
	public String getServiceID(){
		return serviceID;
	}
}
