package com.nabsys.resource.service;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;
import com.nabsys.process.instance.messagequeue.MessageQueue;

public class MessageQueueServiceHandler extends GateServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int queueSize = 0;
	private MessageQueueHandler messageQueueHandler = null;
	private MessageQueue messageQueue = null;
	private HashMap<String, String> workingQueue = null;
	private final ReadWriteLock	lock = new ReentrantReadWriteLock(false);
    private final Lock writeLock = lock.writeLock();
	
	public MessageQueueServiceHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(null, x, y, width, height);
	}
	
	protected void setMessageQueueHandler(MessageQueueHandler messageQueueHandler)
	{
		this.messageQueueHandler = messageQueueHandler;
	}
	
	protected void setMessageQueueSize(int queueSize)
	{
		this.queueSize = queueSize;
	}
	
	public int getMessageQueueSize()
	{
		return queueSize;
	}
	
	public MessageQueue getMessageQueue()
	{
		return messageQueue;
	}
	
	public void shutdown()
	{
		messageQueueHandler.shutdown();
	}
	
	public void setWorkingQueue(HashMap<String, String> workingQueue)
	{
		this.workingQueue = workingQueue;
	}

	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		if(ctx.getData("__CALL_BY_MANAGER") != null && (Boolean)ctx.getData("__CALL_BY_MANAGER"))
		{
			this.messageQueue = (MessageQueue)ctx.getData("__MESSAGE_QUEUE");
			if(!messageQueue.isEmpty())
			{
				writeLock.lock();
				if(!workingQueue.containsKey(ctx.getServiceID()))
				{
					workingQueue.put(ctx.getServiceID(), "");
					writeLock.unlock();
				}
				else
				{
					writeLock.unlock();
					return;
				}
			}
			else
			{
				return;
			}
			
			//execute when queue is not empty.
			if(map == null) map = new NBFields();
			ThreadExecutor te = new ThreadExecutor(this, ctx, map);
			te.start();
		}
		else
		{
			//Calling by Service Caller
			MessageQueue mq = ctx.getResourceFactory().getQueueFactory().get(ctx.getServiceID());
			if(!mq.offer(map, (String)ctx.getData("__TELEGRAM_ID")))
			{
				throw new Exception("Message Queue is full.");
			}
		}
	}
	
	
	//When queue is empty and thread finishes working call this.
	public void processFinishCallback(String serviceName)
	{
		writeLock.lock();
		if(workingQueue.containsKey(serviceName))
		{
			workingQueue.remove(serviceName);
		}
		writeLock.unlock();
	}
}
