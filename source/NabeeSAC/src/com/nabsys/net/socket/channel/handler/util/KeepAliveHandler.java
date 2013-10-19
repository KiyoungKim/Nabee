package com.nabsys.net.socket.channel.handler.util;

import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.EventState;
import com.nabsys.net.socket.channel.ExceptionEvent;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.net.socket.channel.handler.ChannelHandler;
import com.nabsys.net.socket.channel.handler.decoder.FullFrameDecoder;
import com.nabsys.net.socket.channel.handler.util.exception.RequestIntervalTimeoutException;

public class KeepAliveHandler extends FullFrameDecoder{

	private final IKeepAlive keepAlive;
	public KeepAliveHandler(IKeepAlive keepAlive)
	{
		this.keepAlive = keepAlive;
	}
	
	public void exceptionCaught(ExceptionEvent e)
	{
		if(e.getCause() instanceof RequestIntervalTimeoutException)
		{
			if (keepAlive == null)  throw new NullPointerException("keepAlive");
			IChannelResult result = new ChannelResult(e.getChannel());
			DataEvent event = new DataEvent(e.getChannel(), EventState.WRITE, result, e.getChannel().getRemoteAddress());
			event.setData(keepAlive.generateKeepAliveMessage());
			e.getChannel().getHandlerChain().sendNextDownstream((ChannelHandler)this, event);
		}
		else
		{
			super.exceptionCaught(e);
		}
	}
}
