package com.nabsys.net.socket.channel;

import java.net.SocketAddress;
import java.util.HashMap;

public class ChannelEvent implements IChannelEvent{
	
	private final EventState state;
	private final Channel channel;
	private final SocketAddress remoteAddress;
	private Object value;
	private final IChannelResult result;
	private final HashMap<String, Object> data = new HashMap<String, Object>();
	
	public ChannelEvent(Channel channel, EventState state, IChannelResult result, SocketAddress remoteAddress)
	{
		this.state = state;
		this.channel = channel;
		this.remoteAddress = remoteAddress;
		this.result = result;
	}
	
	public ChannelEvent(Channel channel, EventState state, IChannelResult result)
	{
		this(channel, state, result, null);
	}
	
	public EventState getState()
	{
		return this.state;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public Object getValue()
	{
		return this.value;
	}
	
	public IChannelResult getChannelResult()
	{
		return this.result;
	}
	
	public Channel getChannel()
	{
		return this.channel;
	}
	
	public SocketAddress getRemoteAddress()
	{
		return this.remoteAddress;
	}
	
	public void setData(String key, Object data)
	{
		this.data.put(key, data);
	}
	
	public Object getData(String key)
	{
		return this.data.get(key);
	}
}
