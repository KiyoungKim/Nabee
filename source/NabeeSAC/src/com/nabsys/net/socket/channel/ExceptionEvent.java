package com.nabsys.net.socket.channel;

public class ExceptionEvent implements IEvent{
	private final Throwable e;
	private final Channel channel;
	private final IChannelResult result;
	
	public ExceptionEvent(Channel channel, Throwable e, IChannelResult result)
	{
		this.e = e;
		this.channel = channel;
		this.result = result;
	}
	
	public Throwable getCause()
	{
		return e;
	}

	public Channel getChannel() 
	{
		return channel;
	}

	public IChannelResult getChannelResult() 
	{
		return this.result;
	}
}
