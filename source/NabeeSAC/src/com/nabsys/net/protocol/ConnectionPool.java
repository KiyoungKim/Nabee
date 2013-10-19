package com.nabsys.net.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.socket.channel.AsyncChannel;
import com.nabsys.net.socket.channel.AsyncChannelFactory;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.ClientSocketConstructor;
import com.nabsys.net.socket.channel.DefaultHandlerChainFactory;
import com.nabsys.net.socket.channel.HandlerChain;
import com.nabsys.net.socket.channel.IChannelFactory;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.net.socket.channel.IHandlerChainFactory;
import com.nabsys.net.socket.channel.handler.MappingFrameAssignHandler;
import com.nabsys.net.socket.channel.handler.util.TimeoutHandler;
import com.nabsys.net.socket.channel.handler.util.TimeoutSide;

public class ConnectionPool {
	
	private BlockingQueue<Channel> 		queue 		= null;
	private ConnectionPoolSource 		source 		= null;
	private int							curCnt		= 0;
    private final Object				trafficLock = new Object();
    private Class<?>					type		= null;

	public ConnectionPool(ConnectionPoolSource source)
	{
		this.source = source;
		queue = new LinkedBlockingQueue<Channel>(source.getMaxIdle());
	}
	
	public void setProtocolType(Class<?> type)
	{
		this.type = type;
	}
	
	private Channel genChannel()
	{
		if(source.isKeepAlive() && source.getKeepAliveSecond() <= 0) source.setKeepAliveSecond(10);
		
		IChannelFactory channelFactory = new AsyncChannelFactory();
		HandlerChain handlerChain =  new HandlerChain();
		
		if(source.isKeepAlive())
		{
			handlerChain.addLast("writeTimeoutHandler"	, new TimeoutHandler(TimeoutSide.WRITE, source.getKeepAliveSecond() * 1000));
		}
		handlerChain.addLast("mappingFrameAssignHandler", new MappingFrameAssignHandler(
														type,
														source.getIDFieldID(),
														source.getLengthFieldID(),
														source.getLengthFieldOffset(),
														source.getLengthFieldLength(),
														source.getLengthFieldAdjustment(),
														source.getEncoding()));
			
		IHandlerChainFactory chainFactory = new DefaultHandlerChainFactory();
		chainFactory.setHandlerChain(handlerChain);
		
		ClientSocketConstructor socketConstructor = new ClientSocketConstructor(channelFactory);
		socketConstructor.setHandlerChainFactory(chainFactory);
		
		InetSocketAddress remoteAddress = new InetSocketAddress(source.getAddress(), source.getPort());
		IChannelResult result = socketConstructor.connect(remoteAddress);
		Channel channel = result.getChannel();
		channel.setMaxBufferSize(source.getMaxSocketBuff());
		
		((AsyncChannel)result.getChannel()).attatchPool(this);

		curCnt++;
		return result.getChannel();
	}

	public Channel getChannel()
	{
		Channel channel = null;
		
		if((channel = queue.poll()) == null)
		{
			if(curCnt < source.getMaxActive())
			{
				synchronized(trafficLock) {
					channel = genChannel();
				}
			}
			else
			{
				try {
					channel = queue.poll(source.getMaxWait(), TimeUnit.MILLISECONDS);
				} catch (InterruptedException ex) {
					throw new TimeoutException(0x0051);
				}
				
				if(channel == null) throw new TimeoutException(0x0051);
			}
		}
			
		if(!channel.isConnected())
		{
			channel.connect(new InetSocketAddress(source.getAddress(), source.getPort()));
		}
		
		return channel;
	}
	
	public IChannelResult close(AsyncChannel channel)
	{
		if(!channel.isConnected())
		{
			synchronized(trafficLock) {
				curCnt--;
			}
			
			IChannelResult channelResult = new ChannelResult(channel);
			channelResult.setSuccess();
			return channelResult;
		}
		
		IChannelResult channelResult = null;
		if(queue.offer(channel))
		{
			channelResult = new ChannelResult(channel);
			channelResult.setSuccess();
		}
		else
		{
			channelResult = channel.callCloseByPool();
			synchronized(trafficLock) {
				curCnt--;
			}
		}
		
		return channelResult;
	}
	
	public void closeAll() throws IOException
	{
		AsyncChannel channel = null;
		
		while((channel = (AsyncChannel)queue.poll())!= null)
		{
			channel.callCloseByPool();
			curCnt--;
		}
	}
}
