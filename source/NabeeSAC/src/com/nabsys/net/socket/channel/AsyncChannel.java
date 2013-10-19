package com.nabsys.net.socket.channel;

import java.net.SocketAddress;

import com.nabsys.net.protocol.ConnectionPool;

public class AsyncChannel extends DefaultChannel{
	
	private ConnectionPool pool = null;
	private volatile SocketAddress remoteAddress;

	public AsyncChannel()
	{
		super();
	}
	
	public AsyncChannel(IChannelFactory factory, HandlerChain handlerChain)
	{
		super();
		
		this.factory = factory;
		this.handlerChain = handlerChain;
		this.core = new ChannelCore();
		this.handlerChain.attachChannelCore(core);
	}

	public SocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}
	
	public IChannelResult connect(SocketAddress remoteAddress)
	{
		if (remoteAddress == null)  throw new NullPointerException("remoteAddress");
		this.remoteAddress = remoteAddress;
		IChannelResult result = new ChannelResult(this);
		if(this.core == null)
		{
			this.core = new ChannelCore();
			this.handlerChain.attachChannelCore(core);
		}
		handlerChain.sendDownstream(new ChannelEvent(this, EventState.CONNECT, result, remoteAddress));

		isConnected = result.isSuccess();
		
		return result;
	}
	
	public IChannelResult write(Object message)
	{
		if (message == null)  throw new NullPointerException("message");
		
		IChannelResult result = new ChannelResult(this);
		DataEvent event = new DataEvent(this, EventState.WRITE, result, remoteAddress);
		event.setData(message);
		handlerChain.sendDownstream(event);
		
		return result;
	}

	public IChannelResult disconnect() {
		IChannelResult result = null;
		if(pool != null)
		{
			result = pool.close(this);
		}
		else
		{
			result = new ChannelResult(this);
			ChannelEvent event = new ChannelEvent(this, EventState.DISCONNECT, result, remoteAddress);
			handlerChain.sendDownstream(event);
		}
		
		return result;
	}

	public IChannelResult close() {
		IChannelResult result = null;
		if(pool != null)
		{
			result = pool.close(this);
		}
		else
		{
			result = new ChannelResult(this);
			ChannelEvent event = new ChannelEvent(this, EventState.CLOSE, result, remoteAddress);
			handlerChain.sendDownstream(event);
			this.remoteAddress = null;
		}

		return result;
	}
	
	public void setDisconnected()
	{
		this.isConnected = false;
	}
	
	public void setUnbound()
	{
		this.isBound = false;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean isBound() {
		return isBound;
	}
	
	public void attatchPool(ConnectionPool pool)
	{
		this.pool = pool;
	}
	
	public IChannelResult callCloseByPool()
	{
		IChannelResult result = new ChannelResult(this);
		ChannelEvent event = new ChannelEvent(this, EventState.DISCONNECT, result, remoteAddress);
		handlerChain.sendDownstream(event);
		
		return result;
	}
}
