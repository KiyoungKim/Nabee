package com.nabsys.resource.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.process.Context;
import com.nabsys.process.instance.messagequeue.Message;

public class NetworkWriteHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String telegramID = null;
	public NetworkWriteHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String telegramID)
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
			else
			{
				parentHandler = parentHandler.getParent();
			}
		}
		clientNetworkHandler.setOutboundTelegramID(telegramID);
		this.telegramID = telegramID;
	}
	public String getTelegramID()
	{
		return telegramID;
	}
	
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
			
			NBFields queueData = null;
			if((queueData = (NBFields)ctx.getData("__QUEUE_MSG_DATA")) != null)
			{
				IChannelResult result = channel.write(queueData);
				if(!result.isSuccess())
				{
					ctx.setData("__QUEUE_MSG_FAULT_DATA", new Message(queueData,(String)ctx.getData("__OUT_TLGM_ID")));
					throw new Exception(result.getCause().getMessage());
				}
			}
			else
			{
				NBFields fields = setNBFields(map);
				IChannelResult result = channel.write(fields);
				if(!result.isSuccess())
				{
					throw new Exception(result.getCause().getMessage());
				}
			}
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Write Network : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
	
	@SuppressWarnings("unchecked")
	private NBFields setNBFields(HashMap<String, Object> map){
		NBFields fields = new NBFields();
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			if((map.get(key) instanceof ArrayList))
			{
				ArrayList<Object> list = (ArrayList<Object>)map.get(key);
				ArrayList<NBFields> listFields = new ArrayList<NBFields>();
				
				for(int i=0; i<list.size(); i++)
				{
					HashMap<String, Object> listMap = (HashMap<String, Object>)list.get(i);
					listFields.add(setNBFields(listMap));
				}
				
				fields.put(key, listFields);
			}
			else
			{
				fields.put(key, map.get(key));
			}
		}
		
		return fields;
	}
}
