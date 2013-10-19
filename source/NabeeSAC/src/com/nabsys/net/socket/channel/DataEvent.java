package com.nabsys.net.socket.channel;

import java.net.SocketAddress;

import com.nabsys.process.Context;
import com.nabsys.resource.OnlineServiceContext;

public class DataEvent implements IEvent {

	private final Channel channel;
	private final SocketAddress remoteAddress;
	private Object message = null;
	private final IChannelResult result;
	private final EventState state;
	private Context context;
	private OnlineServiceContext service= null;
	
	public DataEvent(Channel channel, EventState state, IChannelResult result, SocketAddress remoteAddress) 
	{
		this.channel = channel;
		this.remoteAddress = remoteAddress;
		this.result = result;
		this.state = state;
	}
	
	public EventState getState()
	{
		return this.state;
	}
	
	public void setData(Object data)
	{
		this.message = data;
	}

	public Object getMessage()
	{
		return this.message;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public SocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	public IChannelResult getChannelResult() {
		return this.result;
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}
	
	public Context getContext()
	{
		return this.context;
	}
	
	public void setService(OnlineServiceContext service)
	{
		this.service = service;
	}
	
	public OnlineServiceContext getService()
	{
		return this.service;
	}
}
