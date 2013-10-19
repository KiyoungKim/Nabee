package com.nabsys.net.socket.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.exception.NetException;


public class AsyncAcceptChannel extends AsyncChannel {
	private Channel parent;
	private SocketChannel socket;
	final NLogger logger = NLogger.getLogger(this.getClass());
	public AsyncAcceptChannel(IChannelFactory factory, HandlerChain handlerChain, SocketChannel socket) throws SocketException
	{
		super();
		this.factory = factory;
		this.handlerChain = handlerChain;
		
		this.socket = socket;
		
		try{
			this.socket.configureBlocking(false);
		} catch (IOException e) {
			throw new NetException(0x0002);
		}
		
		this.socket.socket().setSoLinger(false, 0);
		this.socket.socket().setReuseAddress(true);
		
		core = new ChannelCore(socket);
		handlerChain.attachChannelCore(core);
	}

	public void invokeMessageListener(ExecutorService messageListener) throws IOException
	{
		Selector readSelector = null;
		try {
			readSelector = Selector.open();
		} catch (IOException e) {
			throw new NetException(0x0000);
		}
		
		try {
			socket.register(readSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			throw new NetException(0x000A);
		}
		
		core.invokeMessageListener(this, readSelector, messageListener);
		isConnected = socket.isConnected();
	}

	protected void free(){
		parent = null;
		socket = null;
		super.free();
	}
	
	protected void setParent(Channel parent)
	{
		this.parent = parent;
	}
	
	public Channel getParent() 
	{
		return this.parent;
	}
	
	public SocketAddress getRemoteAddress() {
		return (SocketAddress)this.socket.socket().getRemoteSocketAddress();
	}
	
	public void setMaxBufferSize(int size) 
	{
		try {
			socket.socket().setReceiveBufferSize(size);
			socket.socket().setSendBufferSize(size);
		} catch (SocketException e) {
			throw new NetException("Socket buffer size : " + e.getMessage());
		}
		
		super.setMaxBufferSize(size);
	}
	
	public IChannelResult connect(SocketAddress remoteAddress)
	{
		throw new IllegalStateException("Accept channel can't connect to client.");
	}
}
