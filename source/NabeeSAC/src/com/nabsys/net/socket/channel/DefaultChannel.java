package com.nabsys.net.socket.channel;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultChannel implements Channel {

	protected volatile IChannelFactory factory;
	protected volatile IHandlerChainFactory handlerChainFactory;
	protected volatile HandlerChain handlerChain;
	private int maxBufferSize = 1024;
	private volatile Integer id;
	static final HashMap<Integer, Channel> channelMap = new HashMap<Integer, Channel>();

	
	private final ReadWriteLock			lock;
	private final Lock 					readLock;
    private final Lock 					writeLock;
	
	protected boolean isConnected;
	protected boolean isBound;
	protected boolean readable;
	
	protected ChannelCore core = null;
	DefaultChannel()
	{
		lock		= new ReentrantReadWriteLock(false);
		readLock 	= lock.readLock();
	    writeLock 	= lock.writeLock();
		
		isConnected = false;
		isBound = false;
		readable = false;
		id = genChannelId(this);
	}
	
	private static final Integer genChannelId(Channel channel) {
        Integer id = Integer.valueOf(System.identityHashCode(channel));
        for (;;) {
            if (!DefaultChannel.channelMap.containsKey(id)) {
            	DefaultChannel.channelMap.put(id, channel);
                return id;
            } else {
                id = Integer.valueOf(id.intValue() + 1);
            }
        }
    }
	
	public Integer getId()
	{
		return this.id;
	}
	
	public SocketAddress getRemoteAddress() {
		return null;
	}

	public IChannelResult bind(SocketAddress localAddress) {
		return null;
	}

	public IChannelResult unbind() {
		return null;
	}

	public IChannelResult disconnect() {
		return null;
	}


	public IChannelResult close() {
		return null;
	}

	public IChannelResult connect(SocketAddress remoteAddress) {
		return null;
	}

	public IChannelResult write(Object message) {
		return null;
	}

	public void setMaxBufferSize(int size) 
	{
		this.maxBufferSize = size;
	}
	
	public int getMaxBufferSize()
	{
		return this.maxBufferSize;
	}

	public IChannelFactory getChannelFactory()
	{
		return this.factory;
	}

	public HandlerChain getHandlerChain()
	{
		return this.handlerChain;
	}

	public void setHandlerChainFactory(IHandlerChainFactory handlerChainFactory) 
	{
		this.handlerChainFactory = handlerChainFactory;
	}
	
	public IHandlerChainFactory getHandlerChainFactory() 
	{
		return this.handlerChainFactory;
	}

	public boolean addChild(Channel channel) {
		return false;
	}

	public ChannelList getChildren() {
		return null;
	}

	public Channel getParent() {
		return null;
	}

	public void setDisconnected() {
	}

	public void setUnbound() {
	}

	public boolean isConnected() {
		return false;
	}

	public boolean isBound() {
		return false;
	}

	public Lock getReadLock()
	{
		return this.readLock;
	}
	
	public Lock getWriteLock()
	{
		return this.writeLock;
	}
	
	protected void free()
	{
		DefaultChannel.channelMap.remove(id);
		this.core = null;
		this.factory = null;
		this.handlerChain.attachChannelCore(null);
	}

	public void setReadable(boolean readable) {
		if(this.readable != readable)
		{
			if(readable)
			{
				this.readable = readable;
				core.notifyMessageListener();
			}
			else
			{
				this.readable = readable;
			}
		}
	}

	public boolean isReadable() {
		return this.readable;
	}
}
