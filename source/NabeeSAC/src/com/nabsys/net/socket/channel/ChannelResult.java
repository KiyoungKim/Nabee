package com.nabsys.net.socket.channel;

import java.util.ArrayList;


public class ChannelResult implements IChannelResult {
	private Channel channel;
	private final ArrayList<IChannelAction> actionList = new ArrayList<IChannelAction>();
	private boolean isDone = false;
	private Throwable cause;
	
	public ChannelResult(Channel channel)
	{
		super();
		this.channel = channel;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public synchronized boolean isDone() {
		return this.isDone;
	}
	
	public synchronized boolean isSuccess() {
		return this.isDone && cause == null;
	}
	
	public synchronized Throwable getCause() {
		return cause;
	}
	
	public synchronized void registFinalAction(IChannelAction action)
	{
		actionList.add(action);
	}
	
	public synchronized boolean removeFinalAction(IChannelAction action)
	{
		return actionList.remove(action);
	}
	
	private synchronized void executeFinalAction()
	{
		if(actionList.size() > 0)
		{
			int size = actionList.size();
			for(int i=0; i<size; i++)
			{
				actionList.get(0).completedEvent(this);
				actionList.remove(0);
			}
		}
	}
	
	public synchronized boolean setSuccess()
	{
        synchronized (this) {
            if (isDone) {
                return false;
            }

            isDone = true;
        }
        executeFinalAction();
        return true;
	}
	
	public synchronized boolean setFailure(Throwable cause)
	{
		synchronized (this) {
            if (isDone) {
                return false;
            }

            this.cause = cause;
            isDone = true;
		}
		
		executeFinalAction();
		return true;
	}
	
}
