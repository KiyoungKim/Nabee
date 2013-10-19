package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.process.Context;

public class OutboundHandler extends ServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String telegramID = null;
	
	public OutboundHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String telegramID)
	{
		this.telegramID = telegramID;
		((OnlineServiceHandler)getParent()).setOutboundTelegramID(telegramID);
	}
	
	public String getTelegramID()
	{
		return this.telegramID;
	}

	public void testExecute(Context ctx, HashMap<String, Object> map)
			throws Exception {
	}
	
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception 
	{
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Outbound Network : " + map + "", false, false);
		}
		else
		{
			//NBFields fields = setNBFields(map);
			NBFields fields = (NBFields)map;
			IChannelResult result = ctx.getChannel().write(fields);
			if(!result.isSuccess())
			{
				try {
					throw result.getCause();
				} catch (Throwable e) {
					throw new Exception(e.getMessage());
				}
			}
		}
		
		super.execute(ctx, map);
	}
	
	/*@SuppressWarnings("unchecked")
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
	}*/
}
