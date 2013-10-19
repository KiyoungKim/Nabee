package com.nabsys.net.socket.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;

import com.nabsys.net.exception.NetException;

public class AsyncServerChannel extends DefaultChannel{

	private SocketAddress localAddress = null;
	private final ServerSocketChannel serverSocket;
	private volatile Selector selector;
	private ChannelList children = null;
	private int maxClientNumber = 0;
	
	AsyncServerChannel(
			IChannelFactory factory, 
			HandlerChain handlerChain, 
			ExecutorService server,
			int maxClientNumber)
	{
		super();
		
		if(maxClientNumber < 0)
		{
			throw new IllegalStateException("maxClientNumber must be a non-negative integer: " + maxClientNumber);
		}
		else if(maxClientNumber == 0)
		{
			children = new ChannelList();
		}
		else
		{
			children = new ChannelList(maxClientNumber);
			this.maxClientNumber = maxClientNumber;
		}
		
		this.factory = factory;
		this.handlerChain = handlerChain;
		
		core = new ChannelCore(server);
		handlerChain.attachChannelCore(core);

		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.socket().setReuseAddress(true);
		} catch (IOException e) {
			throw new NetException(0x0001);
		}
		
		try {
			serverSocket.configureBlocking(false);
		} catch (IOException e) {
			throw new NetException(0x0002);
		}
		
		ChannelEvent event = new ChannelEvent(this, EventState.OPEN, new ChannelResult(this));
		handlerChain.sendUpstream(event);
	}
	
	public ServerSocketChannel getServerSocketChannel()
	{
		return this.serverSocket;
	}
	
	public SocketAddress getRemoteAddress() 
	{
		return null;
	}

	
	public IChannelResult bind(SocketAddress localAddress) 
	{
		if(this.localAddress != null) throw new IllegalStateException("bind already");
		
		if(localAddress == null)  throw new NullPointerException("localAddress");
		
		this.localAddress = localAddress;
		
		IChannelResult result = new ChannelResult(this);
		ChannelEvent event = new ChannelEvent(this, EventState.BIND, result);
		event.setData("localAddress", localAddress);
		handlerChain.sendDownstream(event);
		
		isBound = result.isSuccess();
		return result;
	}

	public IChannelResult unbind() 
	{
		IChannelResult result = new ChannelResult(this);
		ChannelEvent event = new ChannelEvent(this, EventState.BIND, result, this.localAddress);
		handlerChain.sendDownstream(event);
		
		this.localAddress = null;
		
		return result;
	}

	public IChannelResult close() {
		IChannelResult result = new ChannelResult(this);
		ChannelEvent event = new ChannelEvent(this, EventState.CLOSE, result, this.localAddress);
		handlerChain.sendDownstream(event);
		
		this.localAddress = null;
		
		return result;
	}
	
	public void setSelector(Selector selector)
	{
		this.selector = selector;
	}
	
	public Selector getSelector()
	{
		return this.selector;
	}

	public boolean addChild(Channel channel) {
		boolean result = children.add(channel);
		if(result)
		{
			((AsyncAcceptChannel)channel).setParent(this);
		}
		
		return result;
	}
	
	public int getMaxClientNumber() {
		return maxClientNumber;
	}

	public ChannelList getChildren() {
		return children;
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

}
