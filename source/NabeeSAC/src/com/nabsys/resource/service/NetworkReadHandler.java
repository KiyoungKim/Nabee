package com.nabsys.resource.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.handler.util.BlockingReadHandler;
import com.nabsys.process.Context;

public class NetworkReadHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long timeout = 0L;
	private String telegramID;
	public NetworkReadHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	public void setData(String telegramID, long timeout)
	{
		this.timeout = timeout;
		ServiceHandler parentHandler = getParent();
		ClientNetworkHandler clientNetworkHandler = null;
		while(true)
		{
			if(parentHandler instanceof ClientNetworkHandler)
			{
				clientNetworkHandler = (ClientNetworkHandler)parentHandler;
				break;
			}
			else
			{
				parentHandler = parentHandler.getParent();
			}
		}
		clientNetworkHandler.setInboundTelegramID(telegramID);
		this.telegramID = telegramID;
	}
	public long getTimeout()
	{
		return this.timeout;
	}
	public String getTelegramID()
	{
		return telegramID;
	}
	
	@SuppressWarnings("rawtypes")
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			ServiceHandler parentHandler = getParent();
			ClientNetworkHandler clientNetworkHandler = null;
			while(true)
			{
				if(parentHandler instanceof ClientNetworkHandler)
				{
					clientNetworkHandler = (ClientNetworkHandler)parentHandler;
					break;
				}
				else if(parentHandler == null)
				{
					throw new Exception("Can't find client network handler");
				}
				else
				{
					parentHandler = parentHandler.getParent();
				}
			}
			Channel channel = clientNetworkHandler.getChannel();
			NBFields fields = (NBFields)((BlockingReadHandler)channel.getHandlerChain().getHandler("blockingReadHandler")).read(timeout, TimeUnit.SECONDS);
			HashMap<String, Object> newMap = setMap(fields);
			map.putAll(newMap);
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Read Network : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
	
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
	}
}
