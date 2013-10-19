package com.nabsys.net.socket.channel.handler;

import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.ExceptionEvent;
import com.nabsys.net.socket.channel.IChannelEvent;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.net.socket.channel.IEvent;

public class ChannelHandler{
	private volatile ChannelHandler prev = null;
	private volatile ChannelHandler next = null;
	private String name;
	
	protected void fireExceptionStream(Channel c, Throwable t, IChannelResult r)
	{
		r.setFailure(t);
		c.getHandlerChain().sendUpstream(new ExceptionEvent(c, t, r));
	}

	public void messageReceived(DataEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void connectRequested(IChannelEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void writeRequested(DataEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void bindRequested(IChannelEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}

	public void closeRequested(IChannelEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void disconnectRequested(IChannelEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void unbindRequested(IChannelEvent e)
	{
		try{
			if(next!= null) next.sendDownstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelConnected(IChannelEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void writeComplete(DataEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelBound(IChannelEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelOpen(IChannelEvent e)
	{
		try{
			e.getChannel().setReadable(true);
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelClosed(IChannelEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelDisconnected(IChannelEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void channelUnbound(IChannelEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}

	public void exceptionCaught(ExceptionEvent e)
	{
		try{
			if(prev!= null) prev.sendUpstream(e);
		}catch(Exception ex){
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void setPrev(ChannelHandler prev)
	{
		this.prev = prev;
	}
	
	public void setNext(ChannelHandler next)
	{
		this.next = next;
	}
	
	public ChannelHandler getPrev()
	{
		return prev;
	}
	
	public ChannelHandler getNext()
	{
		return next;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void sendDownstream(IEvent e)
	{
		if(e instanceof IChannelEvent)
		{
			IChannelEvent event = (IChannelEvent) e;
			switch(event.getState())
			{
				case CONNECT :
					connectRequested(event);
					break;
				case BIND :
					bindRequested(event);
					break;
				case CLOSE :
					closeRequested(event);
					break;
				case DISCONNECT :
					disconnectRequested(event);
					break;
				default :
			}
		}
		else if(e instanceof DataEvent)
		{
			DataEvent event = (DataEvent) e;
			switch(event.getState())
			{
				case WRITE :
					writeRequested(event);
					break;
				default :
			}
		}

	}
	
	public void sendUpstream(IEvent e)
	{
		if(e instanceof IChannelEvent)
		{
			IChannelEvent event = (IChannelEvent) e;
			switch(event.getState())
			{
				case CONNECT :
					channelConnected(event);
					break;
				case OPEN :
					channelOpen(event);
					break;
				case BIND :
					channelBound(event);
					break;
				case CLOSE :
					channelClosed(event);
					break;
				case DISCONNECT :
					channelDisconnected(event);
					break;
				default :
			}
		}
		else if(e instanceof DataEvent)
		{
			DataEvent event = (DataEvent) e;
			switch(event.getState())
			{
				case READ :
					messageReceived((DataEvent)e);
					break;
				case WRITE :
					writeComplete(event);
					break;
				default :
			}
		}
		else if(e instanceof ExceptionEvent)
		{
			exceptionCaught((ExceptionEvent)e);
		}
	}
}
