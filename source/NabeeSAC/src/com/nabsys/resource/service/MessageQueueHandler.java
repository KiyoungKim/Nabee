package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;
import com.nabsys.process.instance.messagequeue.Message;
import com.nabsys.process.instance.messagequeue.MessageQueue;

public class MessageQueueHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int queueSize = 0;
	private boolean shutdown = false;
	
	public MessageQueueHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(int queueSize)
	{
		this.queueSize = queueSize;
		((MessageQueueServiceHandler)getParent()).setMessageQueueSize(queueSize);
		((MessageQueueServiceHandler)getParent()).setMessageQueueHandler(this);
	}
	
	public int getQueueSize()
	{
		return queueSize;
	}

	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception {
		if(!shutdown && !((MessageQueueServiceHandler)getParent()).getMessageQueue().isEmpty())
		{
			MessageQueue mq = ctx.getResourceFactory().getQueueFactory().get(ctx.getServiceID());
			Message qMsg = null;
			while((qMsg = mq.poll()) != null)
			{
				try{
					ctx.setData("__QUEUE_MSG_DATA", qMsg.getData());
					ctx.setData("__OUT_TLGM_ID", qMsg.getTelegramID());
					super.execute(ctx, map);
				}catch(Exception e){
					Message queueData = null;
					if((queueData = (Message)ctx.getData("__QUEUE_MSG_FAULT_DATA")) != null)
					{
						ctx.removeData("__QUEUE_MSG_FAULT_DATA");
						if(!mq.offer(queueData))
						{
							//When Network is closed... and queue is full... 
							//But Queue size remains more one.
						}
					}
				} finally {
					ctx.removeData("__QUEUE_MSG_DATA");
				}
				
				if(shutdown) break;
			}
		}
		
		((MessageQueueServiceHandler)getParent()).processFinishCallback(ctx.getServiceID());
	}
	
	public void shutdown()
	{
		shutdown = true;
	}

}
