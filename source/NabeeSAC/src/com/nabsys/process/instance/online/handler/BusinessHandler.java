package com.nabsys.process.instance.online.handler;

import java.util.HashMap;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.ExceptionEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;
import com.nabsys.net.socket.channel.handler.util.exception.ResponseIntervalTimeoutException;
import com.nabsys.process.Context;
import com.nabsys.process.ResourceFactory;
import com.nabsys.process.SessionData;
import com.nabsys.resource.ServiceContext;
import com.nabsys.resource.ServiceDesign;

public class BusinessHandler extends ChannelHandler{
	
	private final Context ctx;
	public BusinessHandler(ResourceFactory resourceFactory) throws Exception
	{
		super();
		ctx = new Context(resourceFactory);
		ctx.setPlugins(resourceFactory.getPluginList());
		ctx.setSessionData(new SessionData());
	}
	
	public void messageReceived(DataEvent e)
	{
		ctx.setRemoteAddress(e.getRemoteAddress());
		ctx.setChannel(e.getChannel());
		ctx.setServiceIDFieldID(e.getService().getServiceIDFieldID());
		ctx.setServiceID(e.getService().getServiceID());
		NBFields inboundData = (NBFields)e.getMessage();
		try {
			HashMap<String, Object> map = inboundData;
			ServiceContext service = e.getService();
			ServiceDesign serviceDesign = service.getServiceDesign();
			serviceDesign.execute(ctx, map);
		} catch (Exception ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
			return;
		} 
		super.messageReceived(e);
	}
	
	/*
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> setMap(NBFields fields){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<String> itr = fields.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			if((fields.get(key) instanceof ArrayList))
			{
				ArrayList<Object> list = new ArrayList<Object>();
				ArrayList<NBFields> listFields = (ArrayList<NBFields>)fields.get(key);
				
				for(int i=0; i<listFields.size(); i++)
				{
					NBFields fieldMap = (NBFields)listFields.get(i);
					list.add(setMap(fieldMap));
				}
				
				map.put(key, list);
			}
			else
			{
				map.put(key, fields.get(key));
			}
		}
		
		return map;
	}*/
	
	public void exceptionCaught(ExceptionEvent e)
	{
		if(e.getCause() instanceof ResponseIntervalTimeoutException)
		{
			e.getChannel().close();
		}
		
		super.exceptionCaught(e);
	}
}
